package ploiu.gameLibrary.event

/**
 * Base class for a game event
 */
abstract class GameEvent {
    /**
     * if this event has been handled as part of an event listener
     */
    var handled: Boolean = false
}