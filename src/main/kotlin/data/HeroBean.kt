package data

import data.Config.delayNor
import getImageFromRes
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import log
import resFile
import model.CarDoing
import utils.ImgUtil
import utils.MRobot
import java.awt.image.BufferedImage
import java.io.File

//weightModel 0有就行，1 优先满金,2不满星
//heroname取文件夹名字，但文件夹名字就是取得英雄名字
class HeroBean(
    val heroName: String,
    var weight: Int = 0,
    val weightModel: Int = 1,
    val needCar: Boolean = true,
    var position: Int = -1,
    var compareRate: Double = ImgUtil.simRate,
//    var compareRate: Double = 0.95,
    var isGongCheng: Boolean = false,
    var isMohua: Boolean = false
) {
    //-1未上，其他代表车号位置
//    val img: BufferedImage = getImageFromRes(imgPath + ".png")
//    val img: BufferedImage = ImageIO.read(File(imgPath+".png"))

    var trueFull4Duizhan = false

    var currentLevel = 0
        //0也未上，绿-》金为1234
        set(value) {
            field = value
            if (field > 4 && !isMohua) {
                field = 4
            } else if (field > 5 && isMohua) {
                field = 5
            }
        }

    val imgList = arrayListOf<BufferedImage>()
    val imgList2 = arrayListOf<BufferedImage>()
    val imgList3 = arrayListOf<BufferedImage>()

    var is3Files = false

    init {
        var subFoler = "${Config.platName}/heros/${heroName}"
        var heroFolder = resFile(subFoler)

        var childs = heroFolder.listFiles()

        if(childs.size>0){

            if(childs.get(0).isDirectory){
                is3Files = true
                childs.forEachIndexed { index, file ->
                    var list = when(index){
                        0->imgList
                        1->imgList2
                        else->imgList3
                    }
                    file.listFiles().forEach {

                        list.add(getImageFromRes("$subFoler${File.separator}${file.name}${File.separator}${it.name}"))
                    }

                }
            }else{
                childs.forEach {
                    imgList.  add(getImageFromRes("$subFoler${File.separator}${it.name}"))
                }
            }

        }

//        add(getImageFromRes(heroName + ".png"))
//        for (i in 1..9) {
//            try {
//
//                add(getImageFromRes(heroName + "$i" + ".png"))
//            } catch (e: Exception) {
//                println(e.message)
//            }
//        }
    }

    suspend fun checkStarMix(carDoing: CarDoing): Boolean {
        if (!checkStarLevelDirect(carDoing) || isFull()) {//如果检测到了，但满星了，就用卡片确认一次
            return checkStarLevelUseCard(carDoing)
        }
        return true
    }

    suspend fun checkStarLevelDirect(carDoing: CarDoing): Boolean {
        var checked = false
        try {
            withTimeout(300) {
                while (!checked) {
                    var level = carDoing.carps.get(position).getStarLevelDirect()
//                    if (level > 0 && level-currentLevel==1) {//检测星,按升星1.其它的检测到就不算了
                    if (level > 0) {
                        currentLevel = level
                        checked = true
                    } else {
                        delay(100)
                    }
                }
            }
        } catch (e: Exception) {

        }
        return checked
    }

    /**
     * use click show card way to check star ,because of the star here will not be hide by something else
     * result is tell if has starChanged
     */
    suspend fun checkStarLevelUseCard(carDoing: CarDoing): Boolean {
        if (isInCar()) {
//            delay(delayNor)
            carDoing.carps.get(position).click()
//            delay(delayNor)
            var level = 0
            var list = arrayListOf<Recognize>(
                Recognize.heroStar1,
                Recognize.heroStar2,
                Recognize.heroStar3,
                Recognize.heroStar4,
                Recognize.heroStar5
            )

            try {
                withTimeout(1000) {
                    while (!Recognize.saleRect.isFit()) {
                        delay(30)
                    }
                    log("售卖按钮就位")
                }
            } catch (e: Exception) {
            }

            try {
                withTimeout(1000) {
                    level@ while (level == 0) {
                        for (i in 0..4) {
                            if (list.get(i).isFit()) {
                                level = i + 1
                                break@level
                            }
                        }
                        delay(30)//为了尽快识别，每100ms检测下，否则要delay 比如300等弹窗出来再识别，万一多余了300，就无法识别了
                    }
                }
            } catch (e: Exception) {
                log("未检测到星级，超时")
            }
            var result = (level != currentLevel)
            if (level > 0) {
                currentLevel = level
            }



            var cardMiss = false
            while (!cardMiss) {
                CarDoing.cardClosePoint.click()//todo 这个按钮可能点完没效果但是还产生了动画，导致下第二个卡或本次都失败了实际。需要关闭按钮来检测
                delay(10)
                //其实只要上一行代码 一产生点击，这个按钮就变小了，就不fit了。所以这里其实判断没有用处。
                cardMiss = withTimeoutOrNull(500) {
                    while (CarDoing.cardClosePoint.isFit() || Recognize.saleRect.isFit()) {
                        delay(10)//妈的，这里不加delay就检测不会timeout，fuck
                    }
                    log("卡片弹窗已关闭")
                    true
                } ?: false
            }


            log("$heroName 卡片检测星级结果： ${level}级 changed:${result}")
            return result
        }
        return false
    }

    fun fitImage(oImg: BufferedImage,position: Int): Boolean {

        var list = if(!is3Files) imgList else  when(position){
            0->imgList
            1->imgList2
            else->imgList3
        }

        list.forEach {
            if (ImgUtil.isImageSim(it, oImg,if(is3Files) 0.97 else compareRate,heroName)) {
//                log(it)
                return true
            }
        }
        return false
    }

    fun isInCar() = position > -1

    fun isFull() = if (isMohua) currentLevel >= 5 else currentLevel >= 4

    fun isGold() = currentLevel >= 4
    fun reset() {
        position = -1
        currentLevel = 0
    }
}