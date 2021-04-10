package ploiu.gameLibrary.extensions

import java.awt.image.BufferedImage

fun BufferedImage.copyPixelsFrom(image: BufferedImage) = this.graphics.drawImage(image, 0, 0, null) 