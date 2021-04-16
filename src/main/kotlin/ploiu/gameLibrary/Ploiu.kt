package ploiu.gameLibrary

import ploiu.gameLibrary.event.EventRegistry
import ploiu.gameLibrary.event.GameEvent

/**
 * Represents a single object to access some of the game's core features.
 * TODO rename
 * TODO migrate event code elsewhere
 */
object Ploiu {
    var gameWindow: GameWindow? = null

    // used for ease of use during refactor TODO remove once fully refactored
    @Deprecated("used for ease of use during refactor")
    private val staticEventHandlers
        get() = EventRegistry.staticEventHandlers

    // used for ease of use during refactor TODO remove once fully refactored
    @Deprecated("used for ease of use during refactor")
    private val dynamicEventHandlers
        get() = EventRegistry.dynamicEventHandlers

    // internal field that controls whether or not we can accept events
    private var acceptingEvents = true

    /**
     * The events to be processed in the current game loop.
     * Users do not have direct read or write access to this list since directly modifying the list
     * on top of what this library tries to do can cause instability
     */
    private val internal_events: MutableList<GameEvent> = ArrayList()

    /**
     * a read-only iterator to loop through all game events.
     * 
     * Since a likely action immediately after getting this iterator is looping through each event,
     * referencing this property prevents further events from being posted until [tick] is called
     */
    val events: Iterator<GameEvent>
        get() {
            // the user is likely about to iterate through events, so stop accepting events until the next tick
            this.acceptingEvents = false
            return internal_events.iterator()
        }

    /**
     * adds an event to the event pool. These events can be processed by their handlers automatically by calling [processEvents]
     *
     * Events do not get handled immediately, and events without an associated handler will not be processed by the library (you must process it on your own by finding it using [events])
     * To attempt to process an event, you must call [processEvents] in your game loop
     *
     * This method locks the internal event pool, meaning it is thread-safe
     */
    fun postEvent(gameEvent: GameEvent) {
        // TODO store in an "overflow" list of events so that way no events are dropped?
        if (this.acceptingEvents) {
            synchronized(this.internal_events) {
                this.internal_events.add(gameEvent)
            }
        }
    }

    /**
     * Executes all handlers for all events in the current pool, and marks each handled event has
     * handled. While events are being processed, events cannot be submitted to the pool as it would throw a [ConcurrentModificationException]
     */
    fun processEvents() {
        // disable accepting of events while we are processing them to prevent concurrent modification
        this.acceptingEvents = false
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
        // allow accepting events again now that we're done looping through them
        this.acceptingEvents = true
    }

    /**
     * delays game execution by the passed time in milliseconds, and performs house cleaning tasks such as:
     * - clearing events that have been marked as handled
     * - allowing events to be submitted again
     *
     * This method locks the event pool and clears events that are already handled. This action is thread safe
     */
    fun tick(time: Long) {
        // the user is likely done iterating over events, so allow accepting of events again
        this.acceptingEvents = true
        synchronized(this.internal_events) {
            this.internal_events.removeIf { it.handled }
        }
        try {
            Thread.sleep(time)
        } catch (e: InterruptedException) {
            // TODO log error
        }
    }

    /**
     * Clears the event pool of all events, regardless of if they have been processed. This action is thread-safe
     */
    fun clearEventPool() {
        synchronized(this.internal_events) {
            this.internal_events.clear()
        }
    }

}