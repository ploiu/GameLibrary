package ploiu.gameLibrary.event

import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

class GameWindowCloseAdapter(private val function: Function0<Unit>?) : WindowAdapter() {
    override fun windowClosing(e: WindowEvent?) {
        super.windowClosing(e)
        this.function?.invoke()
    }
}