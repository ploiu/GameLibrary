package ploiu.gameLibrary.event

import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent


sealed class MouseGameEvent(val event: MouseEvent) : GameEvent() {
    val isPrimary
        get() = this.event.button == 1

    val isSecondary
        get() = this.event.button == 3

    val isMiddle
        get() = this.event.button == 2
}

/**
 * an event that represents when a plugged-in mouse's button is clicked
 */
class MouseClickEvent(event: MouseEvent) : MouseGameEvent(event)

/**
 * an event that represents when a plugged-in mouse's button is pressed down
 */
class MousePressedEvent(event: MouseEvent) : MouseGameEvent(event)

/**
 * an event that represents when a plugged-in mouse's button is released
 */
class MouseReleaseEvent(event: MouseEvent) : MouseGameEvent(event)

/**
 * an event that represents when a plugged-in mouse enters the game window
 */
class MouseEnteredEvent(event: MouseEvent) : MouseGameEvent(event)

/**
 * an event that represents when a plugged-in mouse leaves the game window
 */
class MouseExitEvent(event: MouseEvent) : MouseGameEvent(event)

/**
 * an event that represents when a plugged-in mouse's scroll wheel is scrolled
 */
class MouseScrolledEvent(event: MouseWheelEvent) : MouseGameEvent(event)