package ploiu.gameLibrary

import ploiu.gameLibrary.event.*
import ploiu.gameLibrary.extensions.copyPixelsFrom
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.*
import java.awt.image.BufferedImage
import javax.swing.JFrame
import javax.swing.JPanel

/**
 * An extension of a [javax.swing.JFrame] that is used to display and render everything in your game
 */
class GameWindow(title: String, size: Dimension) : JFrame(title) {
    var graphics: Graphics2D
    private var image: BufferedImage
    private var panel: JPanel
    var doubleBuffered: Boolean

    init {
        this.size = size
        this.image = BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB)
        this.graphics = image.createGraphics()
        // custom JPanel that draws our image
        this.panel = object : JPanel() {
            override fun paint(graphics: Graphics?) {
                // TODO only paint when necessary
                graphics?.drawImage(this@GameWindow.image, 0, 0, null)
            }
        }
        this.add(panel)
        panel.size = this.size
        this.doubleBuffered = false
        // add an event handler to resize our image if this frame is resized
        this.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                super.componentResized(e)
                val window = this@GameWindow
                window.panel.size = window.size
                val oldImage = window.image
                window.image = BufferedImage(window.width, window.height, BufferedImage.TYPE_INT_ARGB)
                // copy the old image onto the new one
                window.image.copyPixelsFrom(oldImage)
                window.graphics = window.image.createGraphics()
                window.render()
            }
        })
        this.setOnCloseListener()
        this.setKeyboardListeners()
        this.setMouseListeners()
    }

    override fun isDoubleBuffered(): Boolean = doubleBuffered

    fun render() {
        // TODO paint to the image if needed
        this.repaint()
    }

    fun setFullScreen() {
        this.extendedState = MAXIMIZED_BOTH
        this.isUndecorated = true
    }

    /**
     * registers an on closing listener to send a game event
     */
    private fun setOnCloseListener() {
        this.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                super.windowClosing(e)
                Ploiu.postEvent(GameExitEvent(this@GameWindow))
            }
        })
    }

    private fun setKeyboardListeners() {
        this.addKeyListener(object : KeyAdapter() {
            override fun keyTyped(e: KeyEvent?) {
                if (e != null) {
                    Ploiu.postEvent(KeyboardTypeEvent(e))
                }
            }

            override fun keyPressed(e: KeyEvent?) {
                if (e != null) {
                    Ploiu.postEvent(KeyboardDownEvent(e))
                }
            }

            override fun keyReleased(e: KeyEvent?) {
                if (e != null) {
                    Ploiu.postEvent(KeyboardReleaseEvent(e))
                }
            }
        })
    }

    private fun setMouseListeners() {
        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                if (e != null) {
                    println("Clicked frame from thread ${Thread.currentThread().name}")
                    Ploiu.postEvent(MouseClickEvent(e))
                }
            }

            override fun mousePressed(e: MouseEvent?) {
                if (e != null) {
                    Ploiu.postEvent(MousePressedEvent(e))
                }
            }

            override fun mouseReleased(e: MouseEvent?) {
                if (e != null) {
                    Ploiu.postEvent(MouseReleaseEvent(e))
                }
            }

            override fun mouseEntered(e: MouseEvent?) {
                if (e != null) {
                    Ploiu.postEvent(MouseEnteredEvent(e))
                }
            }

            override fun mouseExited(e: MouseEvent?) {
                if (e != null) {
                    Ploiu.postEvent(MouseExitEvent(e))
                }
            }

            override fun mouseWheelMoved(e: MouseWheelEvent?) {
                if (e != null) {
                    Ploiu.postEvent(MouseScrolledEvent(e))
                }
            }
        })
    }
}

/**
 * creates a [GameWindow] and binds it to the global game scope.
 * The flags should be  combined using binary operators and passed as a single number (this is done to prevent a large parameter set)
 *
 */
fun initGameWindow(title: String, size: Dimension, flags: Int) {
    val window = GameWindow(title, size)
    // check for each flag to apply to the game window
    if (flags and WindowFlags.DOUBLE_BUFFERED == WindowFlags.DOUBLE_BUFFERED) {
        window.doubleBuffered = true
    }
    if (flags and WindowFlags.FULL_SCREEN == WindowFlags.FULL_SCREEN) {
        window.setFullScreen()
    }
    if (flags and WindowFlags.CLOSE_KILLS_JVM == WindowFlags.CLOSE_KILLS_JVM) {
        window.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    }
    Ploiu.gameWindow = window
}

/**
 * a set of flags to be passed to [initGameWindow]
 */
object WindowFlags {
    const val FULL_SCREEN = 0b00000001
    const val DOUBLE_BUFFERED = 0b00000010
    const val CLOSE_KILLS_JVM = 0b00000100
}