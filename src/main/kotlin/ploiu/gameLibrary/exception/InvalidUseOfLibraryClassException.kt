package ploiu.gameLibrary.exception

class InvalidUseOfLibraryClassException(message: String) : UnrecoverableGameException(message) {
    override fun toString(): String = String.format("You're using a library class incorrectly: %s", this.message)
}