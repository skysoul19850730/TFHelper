package tasks

import colorCompare
import data.MPoint
import data.MRect
import getImage
import kotlinx.coroutines.delay
import log
import utils.MRobot
import java.awt.Color
import java.awt.image.BufferedImage

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

    suspend fun observerXueDown(){
        var recordXue = getXueLiang(getImage(App.rectWindow))
        var curXue = recordXue

        while(recordXue-curXue<0.05){
            delay(100)
            curXue = getXueLiang(getImage(App.rectWindow))
            if(curXue>recordXue){//有可能处于回血状态，回血的话就把记录的血提升到当前血，再继续监听掉血
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


    fun getBossXueliang(img: BufferedImage? = null): Float {
        var Image = img ?: getImage(App.rectWindow)

        log("getXueLiang start")
        for (x in mRectBoss.right downTo mRectBoss.left step 10) {
            var checkPoint = MPoint(x, y)
            if (colorCompare(Color(Image.getRGB(checkPoint.x, checkPoint.y)), blackColor)) {
                log("getXueLiang end")
                return (mRectBoss.right - x).toFloat() / mRectBoss.width
            }
        }
        log("getXueLiang end")
        return 1f

    }
}