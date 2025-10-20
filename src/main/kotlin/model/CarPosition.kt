package model

import App
import colorCompare
import data.*
import getImage
import getSubImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import log
import logOnly
import loges
import model.CarDoing.Companion.salePoint
import utils.AYUtil
import utils.ImgUtil.forEach
import utils.ImgUtil.sim
import utils.MRobot
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.abs
import kotlin.math.max
import kotlin.system.measureTimeMillis

/**
 * 一个车有7个position
 */
data class CarPosition(
    val mPos: Int = -1,
    var mRect: MRect,
    var starPoint: MPoint,
    val carDoing: CarDoing
) {

    var isUnEnable = false//位置被禁用，寒冰210，比如电法在位置5，死神在位置3.如果黑洞下了死神，逻辑是下电法，上死神，但addhero还是会认为死神上在了位置3，所以加个禁用属性

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    fun reInitRectAndPoint(
        mRect: MRect,
        starPoint: MPoint
    ) {
        this.mRect = mRect
        this.starPoint = starPoint
    }

    fun swipeHeroWithPos(cps: CarPosition) {
        var h = mHeroBean
        mHeroBean = cps.mHeroBean
        mHeroBean?.position = mPos
        cps.mHeroBean = h
        cps.mHeroBean?.position = cps.mPos
        carDoing.sysDataToMain()
    }

    companion object {
        val level5 = Color(255, 70, 53)
        val level4 = Color(255, 193, 10)
        val level3 = Color(173, 142, 255)
        val level2 = Color(102, 192, 255)
        val level1 = Color(102, 248, 32)
    }

    /**
     * 车位是否已打开，0，6默认就是开的。暂时不用这个属性，但如果比如实时计算 工程位的位置，那么就得知道开了多少格子（开了格子不一定上英雄，所以不能按英雄null来判断)
     */
    var mIsOpen = false
        get() {
            return if (mPos == 0) true//0号位和6号位，上来就是开启状态
            else field
        }

    /**
     * 本车位寄住英雄
     */
    var mHeroBean: HeroBean? = null


    fun hasHero() = mHeroBean != null

    fun addHero(heroBean: HeroBean? = null) {
        if (heroBean == null) {
            if (mHeroBean == null) return
            else {
                mHeroBean!!.currentLevel++
            }
            return
        }
        mIsOpen = true
        mHeroBean = heroBean
        heroBean.currentLevel++
        heroBean.position = mPos
    }


    suspend fun downHero(downEvey:Boolean = false) {
        if (mHeroBean != null || downEvey) {
            withContext(Dispatchers.Main) {
                log("车位:$mPos 下卡开始 ${mHeroBean?.heroName}")
                var start = System.currentTimeMillis()
                if (mHeroBean?.heroName != "xiaolu" && carDoing.downCardSpeed) {
                    click()
                    delay(20)
                    MRobot.singleClick(salePoint)
                } else {
                    var cardShow = false
                    withTimeoutOrNull(2000) {
                        var clickNum = 0
                        while (!cardShow) {
                            if (clickNum > 1) {//有些英雄使用原来的click点位，点不出弹窗，甚至直接鼠标移动到那个点点击也点不出来。这种就移动2，3个像素去点击一下
                                //目前见到过的只有在家时的龙拳，以为是家里电脑问题，在pc上也遇到了一个水灵不下卡。
                                click2()
                            } else {
                                click()
                            }
                            clickNum++
                            cardShow = withTimeoutOrNull(300) {
                                while (!Recognize.saleRect.isFit()) {//下卡时 被结束的弹窗挡住，这里一直不fit（挡住了就检测不到出售按钮）。然后也不会执行 结束的按钮点击。所以这里加个超时
                                    delay(10)//妈的，这里不加delay就检测不会timeout，fuck
                                }
//                                log(getImage(App.rectWindow, null))
                                log("售卖按钮就位")
                                true
                            } ?: false
                        }
                    }
                }

                var cardMiss = false
                while (!cardMiss) {
                    MRobot.singleClick(salePoint)//todo 这个按钮可能点完没效果但是还产生了动画，导致下第二个卡或本次都失败了实际。需要关闭按钮来检测
                    delay(10)
                    //其实只要上一行代码 一产生点击，这个按钮就变小了，就不fit了。所以这里其实判断没有用处。
                    cardMiss = withTimeoutOrNull(300) {
                        while (CarDoing.cardClosePoint.isFit() || Recognize.saleRect.isFit()) {
                            delay(10)//妈的，这里不加delay就检测不会timeout，fuck
                        }
//                        log(getImage(App.rectWindow, null))
                        log("卡片弹窗已关闭")
                        true
                    } ?: false
                }

                log("下卡完成：${mHeroBean?.heroName} position:${mHeroBean?.position} coast:${System.currentTimeMillis() - start}")
                mHeroBean?.reset()
                mHeroBean = null
//                delay(50)//多delay50，否则连续下卡时太快，第二次点击hero（其实没点击到）第一个的弹窗还没消息，弹窗按钮点击会缩小。所以一点击其实就不fit了
            }
        }
    }

    suspend fun click() {
        MRobot.singleClick(mRect.clickPoint)
    }

    suspend fun click2() {
        var point = MPoint(mRect.clickPoint.x + 2, mRect.clickPoint.y + 3)
        MRobot.singleClick(point)
    }


    fun over19Selected(testImg: BufferedImage? = null): Boolean {
        var img = testImg ?: getImage(App.rectWindow, null)
        var rect = MRect.createPointR(mRect.clickPoint, 20)
        var count = 0
        rect.forEach { x, y ->
            var color = img.getRGB(x, y)
            if (colorCompare(Color(color), Config.Color_AY19_Over, 10) || colorCompare(
                    Color(color),
                    Config.Color_AY19_Over2,
                    10
                )
            ) {
                count++
            }
        }
        if (count > 120) {
            log("识别到19点名一次结束")
        }
        return count > 120
    }

    fun isChuanZhangPointIn(point: MPoint): Boolean {
        return (abs(point.x - mRect.clickPoint.x) < 8
                && abs(point.y - mRect.clickPoint.y) < 30
                )
    }

    fun rateSelectByChuanZhang(testImg: BufferedImage): Float {
        var mHeight = mRect.bottom - mRect.top + 1
        var simCount = 0
//        logOnly("top ${mRect.top} bottom:${mRect.bottom}")
//            var resultImg = BufferedImage(testImg.width, testImg.height, TYPE_INT_RGB)
//            resultImg.graphics.drawImage(testImg, 0, 0, testImg.width, testImg.height, null)
        for (y in mRect.top..mRect.bottom) {
            var fit = try {
                colorCompare(Color(testImg.getRGB(mRect.clickPoint.x, y)), Config.Color_ChuangZhang, 15)
            } catch (e: Exception) {
                loges(e.toString())
//                loges("mRect : ${mRect.toString()}, y is $y")
                false
            }
            if (fit) {
                simCount++
//                    resultImg.setRGB(mRect.clickPoint.x,y,Color.RED.rgb)
//                    resultImg.setRGB(mRect.clickPoint.x-1,y,Color.RED.rgb)
//                    resultImg.setRGB(mRect.clickPoint.x-2,y,Color.RED.rgb)
//                    resultImg.setRGB(mRect.clickPoint.x+1,y,Color.RED.rgb)
//                    resultImg.setRGB(mRect.clickPoint.x+2,y,Color.RED.rgb)
            }
//                if (colorCompare(Color(testImg.getRGB(mRect.clickPoint.x-1, y)), Config.Color_ChuangZhang, 10)) {
//                    simCount++
//                }
//                if (colorCompare(Color(testImg.getRGB(mRect.clickPoint.x+1, y)), Config.Color_ChuangZhang, 10)) {
//                    simCount++
//                }
        }
        var rate = (simCount * 1f) / (mHeight)
//            log(resultImg)
//        logOnly("compareHeight:${mRect.width}X${mRect.height} ${mRect.width * mRect.height} totalCount $mHeight       okCount:$simCount   rate:$rate")

        return rate
    }

    private fun horizonCountsOfChuanzhang(testImg: BufferedImage, y: Int): Boolean {
        var simCount = 0
        val left = max(mRect.left, 0)
        var mwidth = mRect.right - left + 1
        for (x in left..mRect.right) {
            var fit = try {
                colorCompare(Color(testImg.getRGB(x, y)), Config.Color_ChuangZhang, 15)
            } catch (e: Exception) {
                false
            }
            if (fit) {
                simCount++
//                if(simCount>120)//这里不能用某个样本采集的120，rect宽度大约250.但船长有的点的位置显示的圆圈很小的，所以横向有40%就可以了
                if (simCount * 1f / mwidth > 0.4) {
                    log("horizonCountsOfChuanzhang ${simCount} y:$y")
                    return true
                }
            }

        }
        if (simCount * 1f / mwidth > 0.2) {
            logOnly("horizonCountsOfChuanzhang ${simCount} y:$y")
        }


        return false
    }

    fun horizonHasLineOfChuanzhang(testImg: BufferedImage): Boolean {
        var centerY = (mRect.bottom + mRect.top) / 2

//        val maxDistance = maxOf(centerY - mRect.top, mRect.bottom - centerY)
        val maxDistance = 25 //这里不能用centerY - mRect.top 因为当时取得rect很大，用这个范围也会导致横线一直偏离很多后，会到达另一个位置的连续横线处.
        for (i in 0..maxDistance) {
            // 检查上方行
            if (centerY - i >= mRect.top) {
                var countH = horizonCountsOfChuanzhang(testImg, centerY - i)
                if (countH) {
                    log("horizonHasLineOfChuanzhang:true  y:${centerY - i}")
                    return true
                }
            }

            // 检查下方行
            if (centerY + i <= mRect.bottom && i > 0) {
                var countH = horizonCountsOfChuanzhang(testImg, centerY + i)
                if (countH) {
                    log("horizonHasLineOfChuanzhang:true y:${centerY + i}")
                    return true
                }
            }

        }

        log("horizonHasLineOfChuanzhang:false")



        return false
    }

    suspend fun checkStarMuluty() {
        var hl = getStarLevelDirect()
        if (hl <= 0) {
            mHeroBean?.checkStarLevelUseCard(carDoing)
        } else {
            mHeroBean?.currentLevel = hl
        }
    }

    suspend fun checkStarLevelUseCard() {
        mHeroBean?.run {
            checkStarLevelUseCard(carDoing)
            log("${heroName} level is ${currentLevel}")
        }
    }

    fun getStarLevelDirect(imgTest: BufferedImage? = null): Int {
//            var starColor = Color(img.getRGB(starPoint.x, starPoint.y))
        var testColor = imgTest?.getRGB(starPoint.x, starPoint.y)?.run {
            Color(this)
        }
        var starColor = testColor ?: MRobot.robot.getPixelColor(starPoint.x, starPoint.y)

        if (Config.debug) {
            var starRect = MRect.create4P(starPoint.x - 5, starPoint.y - 5, starPoint.x + 5, starPoint.y + 5)
            var img = imgTest?.getSubImage(starRect) ?: getImage(starRect)
            log(img)
            logOnly("star color is red:${starColor.red} green:${starColor.green} blue:${starColor.blue}")
        }
        if (colorCompare(starColor, level5)) {
            return 5
        }
        if (colorCompare(starColor, level4)) {
            return 4
        }
        if (colorCompare(starColor, level3)) {
            return 3
        }
        if (colorCompare(starColor, level2)) {
            return 2
        }
        if (colorCompare(starColor, level1)) {
            return 1
        }
        return 0
    }


    fun getAy19Img(img: BufferedImage): BufferedImage {
        return img.getSubImage(MRect.createPointR(mRect.clickPoint, 15))
    }

    fun isAy19Selected(img2: BufferedImage?): Boolean {
        var result = false
        measureTimeMillis {
            var bitRect = MRect.createPointR(mRect.clickPoint, 19)//稍微大两个像素看看效率
            var bitImg = if (img2 != null) img2.getSubImage(bitRect) else getImage(bitRect)
            result = AYUtil.isImgCall(bitImg)
        }.apply {
            logOnly("cpos:${mPos} isSelect : $result coast:${this}")
        }
        return result

    }


    fun isHB199Selected(imgTest: BufferedImage): Boolean {
//        MRect.createPointR(mRect.clickPoint, 3).forEach { i, i2 ->
//            imgTest.setRGB(i,i2,Color.RED.rgb)
//        }

        val px = mRect.clickPoint.x
        val py = mRect.clickPoint.y
        var hasCount = 0
        var rX = 10
        var rY = 25
        var startX = px - rX
        var starty = py - rY
        var totalCount = (rX * 2 + 1) * (rY * 2 + 1).toFloat()
        for (y in starty..starty + 2 * rY) {
            for (x in startX..startX + 2 * rX) {
                var color = imgTest.getRGB(x, y).run {
                    Color(this)
                }
                if (colorCompare(color, Color(5, 10, 10), 10)) {
                    hasCount++
                }
            }
        }

        logOnly("car:${carDoing.chePosition} position:${mPos} hb199 hasCount :$hasCount")
        if (hasCount > 300) {
            App.save()
            return true
        }
        return false
    }

    fun isAy19SelectedV2(img2: BufferedImage): Boolean {

        val px = mRect.clickPoint.x
        val py = mRect.clickPoint.y

        var angle = 0.0

        var hasCount = 0

        while (angle < 360) {
            var has = false
            for (r in 20..46) {
                var x = px + Math.floor(Math.cos((angle / 180.0) * Math.PI) * r)
                var y = py + Math.floor(Math.sin((angle / 180.0) * Math.PI) * r)
                if (colorCompare(Color(img2.getRGB(x.toInt(), y.toInt())), Color(250, 0, 0), 20)) {
                    has = true
                    break
                }
            }
            if (has) {
                hasCount++
            }
            angle += 45
        }

        //红圈圆的8个角度上 有6个有红
        if (hasCount / 8.0 >= 0.75) {
            logOnly("识别成功角度个数:${hasCount}")
            return true
        }

//        for(x in (px+20)..(px+40)){
//            if(colorCompare(Color(img2.getRGB(x,py)),Color(250,0,0),20)){
//                has = true
//                break
//            }
//        }
//        if(!has)return false
//        for(x in (px-40)..(px-20)){
//            if(colorCompare(Color(img2.getRGB(x,py)),Color(250,0,0),20)){
//                has = true
//                break
//            }
//        }
//        if(!has)return false
//        for(y in (py+20)..(py+40)){
//            if(colorCompare(Color(img2.getRGB(px,y)),Color(250,0,0),20)){
//                has = true
//                break
//            }
//        }
//        if(!has)return false
//        for(y in (py-40)..(py-20)){
//            if(colorCompare(Color(img2.getRGB(px,y)),Color(250,0,0),20)){
//                has = true
//                break
//            }
//        }
//        if(!has)return false

        return false
    }
}