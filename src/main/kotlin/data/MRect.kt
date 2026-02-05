@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package data

import colorCompare
import getImage
import getSubImage
import utils.ImgUtil.forEach
import java.awt.Color
import java.awt.image.BufferedImage

class MRect {
    var top = 0

    var left = 0

    var right = 0

    var bottom = 0

    var clickPoint = MPoint(0, 0)

    val width: Int
        get() {
            return right - left + 1
        }
    val height: Int
        get() {
            return bottom - top + 1
        }

////    @file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")
//    fun getScale():Float{
//
//        var ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
//      return  (ge.defaultScreenDevice.defaultConfiguration.device as Win32GraphicsDevice).defaultScaleX
//    }

    override fun toString(): String {

        return "mRect :${super.toString()}.  top is $top,left is $left bottom is $bottom ,right is $right,clickPoint is ${clickPoint.toString()}"
    }

    /**
     * 放大，一般用于找图
     */
    fun scale(scale: Float):MRect{
        return MRect.createWH((left-(width*(scale-1)/2)).toInt(),
            (top-(height*(scale-1)/2)).toInt(), (width*scale).toInt(), (height*scale).toInt())
    }

    fun hasWhiteColor(): Boolean {
        var img = getImage(this)
        forEach { i, i2 ->
            val color = img.getRGB(i - left, i2 - top)
            if (color == Color.WHITE.rgb) {
                return true
            }
        }
        return false
    }
    fun hasColor(tColor: Color):Boolean{
        var img = getImage(this)
        forEach { i, i2 ->
            val color = img.getRGB(i - left, i2 - top)
            if (color == tColor.rgb) {
                return true
            }
        }
        return false
    }
    fun hasSimColor(img: BufferedImage,tColor: Color,sim: Int):Boolean{
        forEach { i, i2 ->
            val color = Color(img.getRGB(i , i2 ))
            if (colorCompare(color,tColor,sim)) {
                return true
            }
        }
        return false
    }

    /**
     * 只能判断主色调，比如红绿蓝有偏向的，可以是红绿的高混或者绿蓝高混（青色）,不能判断黑白
     */
    fun hasHSVColorCount(vararg pairs: Pair<Int,Int>,testImg: BufferedImage? = null): Int {
        var img = testImg?.getSubImage(this) ?: getImage(this)
        var count = 0
        forEach { i, i2 ->
            val color = img.getRGB(i - left, i2 - top).toHSB()
            val h = (color[0]*360).toInt()
            val s = color[1]

            if(s>0.2) {
                pairs.forEach {
                    if (h in it.first..it.second) {
                        count++
                    }
                }
            }
        }
        return count
    }




    fun hasColorCount(toColor: Color, sim: Int = 20, testImg: BufferedImage? = null): Int {
        var img = testImg?.getSubImage(this) ?: getImage(this)
        var count = 0
        forEach { i, i2 ->
            val color = img.getRGB(i - left, i2 - top)
            if (colorCompare(Color(color), toColor, sim)) {
                count++
            }
        }
        return count
    }

    constructor()

    companion object {
        fun create4P(left: Int, top: Int, right: Int, bottom: Int): MRect {
            return MRect().apply {
                this.left = left
                this.top = top
                this.right = right
                this.bottom = bottom
                clickPoint.apply {
                    x = (right + left) / 2
                    y = (bottom + top) / 2
                }
            }
        }

        fun createWH(left: Int, top: Int, width: Int, height: Int): MRect {
            return MRect().apply {
                this.left = left
                this.top = top
                this.right = left + width - 1
                this.bottom = top + height - 1
                clickPoint.apply {
                    x = left + width / 2
                    y = top + height / 2
                }
            }
        }

        fun createPointR(centerPoint: MPoint, r: Int): MRect {
            return MRect().apply {
                clickPoint = centerPoint
                this.left = clickPoint.x - r
                this.right = clickPoint.x + r
                this.top = clickPoint.y - r
                this.bottom = clickPoint.y + r
            }
        }
    }

}


fun Int.toHSB():FloatArray{
    return Color.RGBtoHSB(this shr 16 and 0xFF, this shr 8 and 0xFF, this  and 0xFF, null)
}