package ploiu.gameLibrary

import ploiu.gameLibrary.event.GameEvent
import ploiu.gameLibrary.event.annotation.SubscribeEvent
import ploiu.gameLibrary.exception.InvalidEventHandlerException
import ploiu.gameLibrary.exception.InvalidUseOfLibraryClassException
import ploiu.gameLibrary.exception.TerminalInvalidEventHandlerException
import ploiu.gameLibrary.extensions.hasAnnotation
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

/**
 * Represents a single object to access some of the game's core features.
 * TODO rename
 */
object Ploiu {
    var gameWindow: GameWindow? = null

    /**
     * The events to be processed in the current game loop.
     * Users do not have direct read or write access to this list since directly modifying the list
     * on top of what this library tries to do can cause instability
     */
    private val internal_events: MutableCollection<GameEvent> = ArrayList()
    val eventHandlers: MutableMap<Class<out GameEvent>, MutableCollection<Method>> = hashMapOf()

    val events: Iterator<GameEvent>
        get() = internal_events.iterator()

    fun postEvent(gameEvent: GameEvent) {
        this.internal_events.add(gameEvent)
    }


    /**
     * registers all the functions marked with [ploiu.gameLibrary.event.annotation.SubscribeEvent] in the passed
     * classes as event handlers
     */
    fun registerStaticEventHandlers(vararg classes: Class<*>) {
        // iterate through all classes, get their functions marked as @SubscribeEvent, and add them to the eventHandlers
        val handlers = mutableListOf<Method>()
        for (clazz in classes) {
            val functions = clazz.methods
            for (function in functions) {
                if (function.hasAnnotation(SubscribeEvent::class)) {
                    handlers.add(function)
                }
            }
        }
        // all the handlers have a @SubscribeEvent annotation, so we can get the annotation from each handler and determine the type
        handlers.groupBy { it.getAnnotation(SubscribeEvent::class.java)?.value }
            .forEach {
                this.eventHandlers[it.key!!.java] = it.value.toMutableList()
            }
        // we should not try and process raw game events, so throw an exception if any of the events take a raw GameEvent
        for (key in this.eventHandlers.keys) {
            if (key == GameEvent::class.java) {
                throw InvalidUseOfLibraryClassException("Cannot create an event handler using a raw GameEvent. Try changing the handler to take a specific event")
            }
        }
        this.validateEventHandlers()
    }

    /**
     * dynamically registers an event handler and validates it to ensure it's correct
     */
    fun <T> registerEventHandler(clazz: Class<out T>, handler: Method) where T : GameEvent {
        // if there are already event handlers for the class, add it to the list
        if (this.eventHandlers[clazz] != null) {
            this.eventHandlers[clazz]!!.add(handler)
        } else {
            this.eventHandlers[clazz] = mutableListOf(handler)
        }
        // be sure to validate all our handlers
        this.validateDynamicEventHandler(clazz, handler)
    }

    /**
     * Kotlin implementation of [registerEventHandler]
     */
    fun <T> registerEventHandler(clazz: KClass<out T>, handler: KFunction<Unit>) where T : GameEvent {
        val handlerAsJavaMethod = handler.javaMethod as Method
        registerEventHandler(clazz.java, handlerAsJavaMethod)
    }

    /**
     * validates a single event handler added during runtime to make sure it conforms to the correct structure
     */
    private fun <T> validateDynamicEventHandler(clazz: Class<out T>, handler: Method) where T : GameEvent {
        if (handler.parameterCount == 0 || handler.parameterCount != 1 || handler.parameterTypes[0] != clazz) {
            // get some info on the handler to help the user better navigate to it and correct the issue
            val handlerClass = handler.declaringClass
            val handlerName = handler.name
            throw InvalidEventHandlerException(
                """Invalid event handler: Event Handlers must take exactly 1 argument of the same type on the @SubscribeEvent annotation.
                        |${handlerClass}.${handlerName} is required to take the type ${clazz.typeName} as the only argument, but it takes ${handler.parameterCount} arguments and the first argument is the type ${handler.parameterTypes[0].typeName} 
                    """.trimMargin()
            )
        }
    }

    /**
     * ensures that all registered event handlers are in the correct format.
     * A correct format means that the method must accept 1 argument of the same type passed to the `@SubscribeEvent` annotation on it
     */
    private fun validateEventHandlers() {
        for (entry in this.eventHandlers) {
            val requiredArgType = entry.key
            // search for a event handler that does not take only that type
            for (handler in this.eventHandlers[entry.key]!!) {
                if (handler.parameterCount == 0 || handler.parameterCount != 1 || handler.parameterTypes[0] != requiredArgType) {
                    // get some info on the handler to help the user better navigate to it and correct the issue
                    val handlerClass = handler.declaringClass
                    val handlerName = handler.name
                    throw TerminalInvalidEventHandlerException(
                        """Invalid event handler: Event Handlers must take exactly 1 argument of the same type on the @SubscribeEvent annotation.
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