package ploiu.gameLibrary

import ploiu.gameLibrary.event.Applicator
import ploiu.gameLibrary.event.DynamicEventHandler
import ploiu.gameLibrary.event.GameEvent
import ploiu.gameLibrary.event.annotation.SubscribeEvent
import ploiu.gameLibrary.exception.InvalidEventHandlerException
import ploiu.gameLibrary.exception.InvalidUseOfLibraryClassException
import ploiu.gameLibrary.exception.TerminalInvalidEventHandlerException
import ploiu.gameLibrary.extensions.hasAnnotation
import ploiu.gameLibrary.extensions.isValidForEventHandler
import java.lang.reflect.Method
import kotlin.reflect.KClass

/**
 * Represents a single object to access some of the game's core features.
 * TODO rename
 * TODO migrate event code elsewhere
 */
object Ploiu {
    var gameWindow: GameWindow? = null

    /**
     * The events to be processed in the current game loop.
     * Users do not have direct read or write access to this list since directly modifying the list
     * on top of what this library tries to do can cause instability
     */
    private val internal_events: MutableList<GameEvent> = ArrayList()
    val staticEventHandlers: MutableMap<Class<out GameEvent>, MutableCollection<Method>> = hashMapOf()

    // the event handlers added at runtime; kotlin is weird with static methods so we need to use a functional interface to do these things at runtime
    val dynamicEventHandlers: MutableMap<Class<out GameEvent>, MutableCollection<Applicator<out GameEvent>>> =
        hashMapOf()

    /**
     * a read-only iterator to loop through all game events.
     */
    val events: Iterator<GameEvent>
        get() = internal_events.iterator()

    /**
     * adds an event to the event pool. These events can be processed by their handlers automatically by calling [processEvents]
     *
     * Events do not get handled immediately, and events without an associated handler will not be processed by the library (you must process it on your own by finding it using [events])
     * To attempt to process an event, you must call [processEvents] in your game loop
     */
    fun postEvent(gameEvent: GameEvent) {
        this.internal_events.add(gameEvent)
    }

    /**
     * Executes all handlers for all events in the current pool, and marks each handled event has
     * handled.
     */
    fun processEvents() {
        val eventsWithHandlers = this.internal_events.filter {
            this.staticEventHandlers.keys.contains(it::class.java) || this.dynamicEventHandlers.keys.contains(it::class.java)
        }
        // process static event handlers first
        for (event in eventsWithHandlers) {
            // pass the event through all the handlers
            val handlerList = this.staticEventHandlers[event::class.java]
            // this should never be null but just in case ya know; each handler at this point is already validated to take the event
            handlerList?.forEach { it.invoke(null, event) }
            event.handled = true
        }
        // now process dynamic ones
        for (event in eventsWithHandlers) {
            // pass the event through all the handlers
            val handlerList = this.dynamicEventHandlers[event::class.java]
            // this should never be null but just in case ya know; each handler at this point is already validated to take the event
            handlerList?.forEach { it.apply(event) }
            event.handled = true
        }
    }

    /**
     * delays game execution by the passed time in milliseconds, and performs house cleaning tasks such as:
     * - clearing events that have been marked as handled
     */
    fun tick(time: Long) {
        this.internal_events.removeIf { it.handled }
        try {
            Thread.sleep(time)
        } catch (e: InterruptedException) {
            // TODO log error
        }
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
     * Registers an event handler at runtime, as opposed to the usual static event handlers you can created with [SubscribeEvent]
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
     * A correct format means that the method must accept 1 argument of the same type passed to the `@SubscribeEvent` annotation on it, and the method must be static
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
                        """Invalid event handler: Event Handler must be a static method that takes exactly 1 argument of the same type on the @SubscribeEvent annotation.
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