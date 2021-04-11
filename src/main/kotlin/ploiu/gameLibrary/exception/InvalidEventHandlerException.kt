package ploiu.gameLibrary.exception

import java.lang.Exception

/**
 * represents an exception caused by trying to register an invalid event handler, but not unrecoverable.
 * This is thrown by the library when the user tries to dynamically register an event handler but it does not match the
 * proper form of an event handler function
 */
open class InvalidEventHandlerException(message: String) : Exception(message) {
}

/**
 * represents an invalid event handler exception that cannot be recovered from, and the program must exit.
 * An exception like this means an unrecoverable error has occurred with registering event handlers, and in order to prevent
 * runtime issues the program must exit. These exceptions must not be caught.
 */
class TerminalInvalidEventHandlerException(message: String) : InvalidEventHandlerException(message) {

}