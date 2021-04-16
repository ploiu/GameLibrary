package ploiu.gameLibrary.event

import java.awt.event.KeyEvent

/**
 * An event that represents if the player presses and releases a key on their keyboard
 */
class KeyboardTypeEvent(val keyEvent: KeyEvent) : GameEvent()


/**
 * An event that represents if the player is pressing a key down and hasn't released it yet
 */
class KeyboardDownEvent(val keyEvent: KeyEvent) : GameEvent()

/**
 * An event that represents if the player releases a key that they were holding down
 */
class KeyboardReleaseEvent(val keyEvent: KeyEvent) : GameEvent()