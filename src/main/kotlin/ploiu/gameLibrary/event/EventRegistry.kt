package ploiu.gameLibrary.event

import ploiu.gameLibrary.event.annotation.EventHandler
import ploiu.gameLibrary.exception.InvalidEventHandlerException
import ploiu.gameLibrary.exception.InvalidUseOfLibraryClassException
import ploiu.gameLibrary.exception.TerminalInvalidEventHandlerException
import ploiu.gameLibrary.extensions.hasAnnotation
import ploiu.gameLibrary.extensions.isValidForEventHandler
import java.lang.reflect.Method
import kotlin.reflect.KClass

object EventRegistry {
    val staticEventHandlers: MutableMap<Class<out GameEvent>, MutableCollection<Method>> = hashMapOf()

    // the event handlers added at runtime; kotlin is weird with static methods so we need to use a functional interface to do these things at runtime
    val dynamicEventHandlers: MutableMap<Class<out GameEvent>, MutableCollection<Applicator<out GameEvent>>> =
        hashMapOf()


    /**
     * registers all the functions marked with [ploiu.gameLibrary.event.annotation.EventHandler] in the passed
     * classes as event handlers
     */
    fun registerStaticEventHandlers(vararg classes: Class<*>) {
        // iterate through all classes, get their functions marked as @EventHandler, and add them to the eventHandlers
        val handlers = mutableListOf<Method>()
        for (clazz in classes) {
            val functions = clazz.methods
            for (function in functions) {
                if (function.hasAnnotation(EventHandler::class)) {
                    handlers.add(function)
                }
            }
        }
        // all the handlers have a @EventHandler annotation, so we can get the annotation from each handler and determine the type
        handlers.groupBy { it.getAnnotation(EventHandler::class.java)?.value }
            .forEach {
                this.staticEventHandlers[it.key!!.java] = it.value.toMutableList()
            }
        // we should not try and process raw game events, so throw an exception if any of the events take a raw GameEvent
        for (key in this.staticEventHandlers.keys) {
            if (key == GameEvent::class.java) {
                throw InvalidUseOfLibraryClassException("Cannot create an event handler using a raw GameEvent. Try changing the handler to take a specific event")
            }
        }
        this.validateEventHandlers()
    }

    /**
     * Registers an event handler at runtime, as opposed to the usual static event handlers you can created with [EventHandler]
     */
    fun registerEventHandler(handler: DynamicEventHandler<out GameEvent>) {
        this.validateDynamicEventHandler(handler)
        val eventType: Class<out GameEvent> = handler.event.java
        val eventHandler: Applicator<out GameEvent> = handler.handler
        if (this.dynamicEventHandlers[eventType] != null) {
            this.dynamicEventHandlers[eventType]!!.add(eventHandler)
        } else {
            this.dynamicEventHandlers[eventType] = mutableListOf(eventHandler)
        }
    }

    /**
     * validates a single event handler added during runtime to make sure it conforms to the correct structure.
     * The method must be static and must take a single argument of the type the handler is for
     */
    private fun validateDynamicEventHandler(handler: DynamicEventHandler<out GameEvent>) {
        if (handler.event == GameEvent::class) {
            throw InvalidEventHandlerException(
                "Invalid event handler: Event Handler must not take raw GameEvent object, try passing in a more specific event, or create one yourself and pass that."
            )
        }
    }

    /**
     * ensures that all registered event handlers are in the correct format.
     * A correct format means that the method must accept 1 argument of the same type passed to the `@EventHandler` annotation on it, and the method must be static
     */
    private fun validateEventHandlers() {
        for (entry in this.staticEventHandlers) {
            val requiredArgType = entry.key
            // search for a event handler that does not take only that type
            for (handler in this.staticEventHandlers[entry.key]!!) {
                if (!handler.isValidForEventHandler(requiredArgType)) {
                    // get some info on the handler to help the user better navigate to it and correct the issue
                    val handlerClass = handler.declaringClass
                    val handlerName = handler.name
                    throw TerminalInvalidEventHandlerException(
                        """Invalid event handler: Event Handler must be a static method that takes exactly 1 argument of the same type on the @EventHandler annotation.
                        |${handlerClass}.${handlerName} is required to take the type ${requiredArgType.typeName} as the only argument, but it takes ${handler.parameterCount} arguments and the first argument is the type ${handler.parameterTypes[0].typeName} 
                    """.trimMargin()
                    )
                }
            }
        }
    }

    fun registerStaticEventHandlers(vararg classes: KClass<*>) {
        this.registerStaticEventHandlers(*classes.toList().map { it.java }.toTypedArray())
    }
}