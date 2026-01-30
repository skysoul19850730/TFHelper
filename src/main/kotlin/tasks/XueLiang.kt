package tasks

import colorCompare
import data.MPoint
import data.MRect
import getImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import log
import utils.MRobot
import java.awt.Color
import java.awt.image.BufferedImage
import java.math.BigDecimal

object XueLiang {

    private val y = 100

    private val mRect = MRect.createWH(100, y, 308, 1)
    private val otherRect = MRect.createWH(593, y, 308, 1)

    private var blackColor = Color(46, 39, 41)


    private val mRectBoss = MRect.createWH(596, y, 300, 1)

    fun isMLess(rate: Float): Boolean {

        var checkPoint = MPoint((mRect.left + mRect.width * rate).toInt(), y)
        return colorCompare(MRobot.robot.getPixelColor(checkPoint.x, checkPoint.y), blackColor)

    }

    suspend fun observerXueDown(xueRate: Float = 0.05f, over: (() -> Boolean)? = null) {
        var recordXue = getXueLiang(getImage(App.rectWindow))
        var curXue = recordXue

        while (recordXue - curXue < xueRate && (over?.invoke() != true)) {
            delay(10)
            curXue = getXueLiang(getImage(App.rectWindow))
            if (curXue > recordXue) {//有可能处于回血状态，回血的话就把记录的血提升到当前血，再继续监听掉血
                recordXue = curXue
            }
        }
    }

    fun getXueLiang(img: BufferedImage? = null): Float {
        var Image = img ?: getImage(App.rectWindow)
        log("getXueLiang start")
        for (x in mRect.left..mRect.right) {
            var checkPoint = MPoint(x, y)
            if (colorCompare(Color(Image.getRGB(checkPoint.x, checkPoint.y)), blackColor)) {
                log("getXueLiang end")
                return (x - mRect.left).toFloat() / mRect.width
            }
        }
        log("getXueLiang end")
        return 1f
    }

    /**
     * 是否多少秒内血量一直低于某值（不回血）
     * xueLess:血量低于多少 比如低于0.6
     */
    suspend fun xueNotBack(xueLess: Float = 0.95f, over: (() -> Boolean)? = null) {
        var xueState = 0
        var startTimt = System.currentTimeMillis()
        while (over?.invoke() != true) {
            if(System.currentTimeMillis() - startTimt >5*60* 1000){//防止外面异常结束，over一直不true
                return
            }
            val curXue = getXueLiang()
            if (curXue < xueLess) {
                xueState++
            } else {
                xueState = 0
            }
            delay(500)
        }
    }


    fun getBossXueliang(img: BufferedImage? = null): Float {
        var Image = img ?: getImage(App.rectWindow)

        log("getXueLiang start")
        for (x in mRectBoss.right downTo mRectBoss.left) {
            var checkPoint = MPoint(x, y)
            if (colorCompare(Color(Image.getRGB(checkPoint.x, checkPoint.y)), blackColor)) {
                var xue = (mRectBoss.right - x).toFloat() / mRectBoss.width
                log("getXueLiang end:${xue}")
                return xue
            }
        }
        log("getXueLiang end")
        return 1f

    }
}