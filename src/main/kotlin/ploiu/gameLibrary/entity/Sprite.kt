package ploiu.gameLibrary.entity

import java.awt.Graphics2D
import java.awt.image.BufferedImage

/**
 * Base class for a sprite. Sprites are optional elements of the game library,
 * but serve as a simple boilerplate class for an entity in the game.
 *
 * Sprites have common properties often used in games, such as position, health, movement speed, etc
 */
open class Sprite(
    var x: Int,
    var y: Int,
    var width: Int,
    var height: Int,
    var health: Float,
    var speedX: Float,
    var speedY: Float
) {
    init {
        // TODO once sprite tracking system is created, add this sprite to the tracking system
    }

    /**Marks if this sprite is dead. Dead sprites will be automatically cleaned up by the game system and removed from tracking*/
    private var dead = false

    /**
     * Marks the sprite as dead, and removes this sprite from the sprite tracking system
     */
    open fun setDead() {
        this.dead = true
        // TODO remove the sprite from the tracking system
    }

    /**
     * This method is called every tick, so use it to perform actions you want to be performed
     * every tick for this sprite, such as movement, health, etc
     */
    open fun update() {

    }

    /**
     * This method is called every time the game window is painted. The game window's graphics are
     * passed to this method, and allow you to determine how this sprite should be rendered.
     *
     * This method will always be called after [update]
     */
    open fun render(graphics: Graphics2D) {

    }
}

/**
 * This type of sprite is a sprite that renders using an image. The image can change
 * during the update sequence based on keeping track of how long it's been since the last image was displayed
 *
 * Just like in the [Sprite] base class, this behavior can be customized through extending
 */
open class ImageSprite(
    x: Int,
    y: Int,
    health: Float,
    speedX: Float,
    speedY: Float,
    var images: Array<BufferedImage>
) : Sprite(x, y, images[0].width, images[0].height, health, speedX, speedY) {
    // the number of ticks since we switched images
    protected open var lastTicksSinceLastImage = 0

    // the number of ticks we should wait before displaying the next image
    protected open var numberOfTicksBetweenImages = 100

    // the current image index we are on
    protected open var imageIndex = 0

    override fun update() {
        super.update()
        this.lastTicksSinceLastImage++
        if (this.lastTicksSinceLastImage >= this.numberOfTicksBetweenImages) {
            this.lastTicksSinceLastImage = 0
            this.imageIndex++
            // make sure we do not go out of bounds for our array of images
            if (this.imageIndex > this.images.size - 1) {
                this.imageIndex = 0
            }
        }
    }

    override fun render(graphics: Graphics2D) {
        super.render(graphics)
        // draw our current image to the screen
        val image = this.images[this.imageIndex]
        graphics.drawImage(image, this.x, this.y, null)
    }
}