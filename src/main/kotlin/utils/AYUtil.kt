package utils

import colorCompare
import data.Config
import data.HeroBean
import data.MPoint
import data.MRect
import foreach
import getImage
import getImageFromFile
import getImageFromRes
import getSubImage
import kotlinx.coroutines.*
import log
import logOnly
import model.CarDoing
import resFile
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import kotlin.coroutines.resume
import kotlin.system.measureTimeMillis

object AYUtil {

    var anyueFolder = "${Config.platName}/tezheng/anyue"

    data class XYColor(var x: Int, var y: Int, var rgb: Int)

    var lists = arrayListOf<List<XYColor>>()

    private val whSize = 31
    private val rate = 0.75

    fun doInit() {
        lists.clear()
        resFile(anyueFolder + "/anyue19").listFiles().forEach {
            val list = arrayListOf<XYColor>()
            val img = getImageFromFile(it)
            img.foreach { x, y ->
                var rgb = img.getRGB(x, y)
                if (rgb != Color.BLACK.rgb) {
                    list.add(XYColor(x, y, rgb))
                }
                false
            }
            lists.add(list)
        }
    }

    fun isImgCall(img: BufferedImage): Boolean {
        if (img.width < 31 || img.height < 31) return false
        var maxRate = 0f
        for (x in 0..img.width - whSize) {
            for (y in 0..img.height - whSize) {


                lists.forEach { list ->
                    //这里是验一个图样的
                    var rate = 0f
                    list.forEach {
                        if (colorCompare(Color(img.getRGB(it.x + x, it.y + y)), Color(it.rgb), 10)) {
                            rate++
                            if (rate / list.size > 0.75) {
                                return true
                            }
                        }
                    }
                    val tmpRate = rate / list.size
                    if (tmpRate > maxRate) {
                        maxRate = tmpRate
                    }
                }
            }
        }
        logOnly("单次测量最大的rate 是：${maxRate}")
        return false

    }

    fun testAY19() {
        val list = File(Config.caiji_main_path + "\\tmp").listFiles()
        val carDoing = CarDoing(0, CarDoing.CheType_MaChe)
        carDoing.initPositions()
        val carDoing2 = CarDoing(1, CarDoing.CheType_MaChe)
        carDoing2.initPositions()

        var car0Count = 0
        var car1Count = 0

        val allTimes = measureTimeMillis {
            list.forEach {
                val img = getImageFromFile(it)
//                val img = getImageFromFile(File(Config.caiji_main_path + "\\tmp","1725017635441.png"))
//                log(img)
//           carDoing2.carps.get(1).over19Selected(img)
                var has = false
//                log("正在识别图片的名称：${it.name}")
                carDoing.getAY19Selected(img).apply {
                    if (this > -1) {
                        car0Count++
                        has = true
                        log(img)
                        log("che 0 ay selected at index :${this}")
                    }
                }
                carDoing2.getAY19Selected(img).apply {
                    if (this > -1) {
                        car1Count++
                        has = true
                        log(img)
                        log("che 1 ay selected at index :${this}")
                    }
                }
                if (!has) {
                    log(img)
                    log("未识别：${it.name}")
                }
            }
        }

        log("识别图片${list.size}张，0车识别到${car0Count}次，1车识别到${car1Count}次，总耗时:${allTimes}")

//        val file = File(  Config.caiji_main_path + "\\tmp" ,"1724407869518.png")
//        val img = getImageFromFile(file)
//        carDoing.getAY19Selected(img)
//        carDoing2.getAY19Selected(img)
    }

    fun testHB199() {
        val list = File(Config.caiji_main_path + "\\tmphb199").listFiles()
        val carDoing = CarDoing(0, CarDoing.CheType_MaChe)
        carDoing.initPositions()
        measureTimeMillis {
            carDoing.getHB199Selected()
        }.apply {
            log("一个车，识别自己199 ，算上截图，共消耗时间：$this")
        }
        val carDoing2 = CarDoing(1, CarDoing.CheType_MaChe)
        carDoing2.initPositions()

        var car0Count = 0
        var car1Count = 0

        val allTimes = measureTimeMillis {
            list.forEach {
                val img = getImageFromFile(it)
//                val img = getImageFromFile(File(Config.caiji_main_path + "\\tmp","1725017635441.png"))
//                log(img)
//           carDoing2.carps.get(1).over19Selected(img)
                var has = false
//                log("正在识别图片的名称：${it.name}")
                carDoing.getHB199Selected(img).apply {
                    if (this > -1) {
                        car0Count++
                        has = true
                        log(img)
                        log("che 0 ay selected at index :${this}")
                    }
                }
                carDoing2.getHB199Selected(img).apply {
                    if (this > -1) {
                        car1Count++
                        has = true
                        log(img)
                        log("che 1 ay selected at index :${this}")
                    }
                }
//                img.saveTo(File(Config.caiji_main_path + "\\tmp", "hb199_${System.currentTimeMillis()}.png"))
                if (!has) {
                    log(img)
                    log("未识别：${it.name}")
                }
            }
        }

        log("识别图片${list.size}张，0车识别到${car0Count}次，1车识别到${car1Count}次，总耗时:${allTimes}")

//        val file = File(  Config.caiji_main_path + "\\tmp" ,"1724407869518.png")
//        val img = getImageFromFile(file)
//        carDoing.getAY19Selected(img)
//        carDoing2.getAY19Selected(img)
    }

    fun testAY39() {
        val list = File(Config.caiji_main_path + "\\tmpAY39").listFiles().filter {
            it.name.contains("5727")
        }
//        measureTimeMillis {
//            getAy39SelectedPositions()
//        }.apply {
//            log("识别39 ，算上截图，共消耗时间：$this")
//        }

        var count = 0
        var allTimes = 0L
        list.forEach {
            val img = getImageFromFile(it)
            allTimes = measureTimeMillis {


//                    img = img.getSubImage(Config.qianRect)
//                var text = Tess.getText(img)
//                log("text is $text")
//                var list = getAy39SelectedPositions(img)
//                if (list.size > 0) {
//                    count++
//                    log("ay39 file name is ${it.name}")
//                }
                testHeroRec(img)
            }
        }

        log("识别图片${list.size}张， 识别到的点名有：${count} 总耗时:${allTimes}")

    }

    val zhanjiang = HeroBean("zhanjiang", 100)
    val tieqi = HeroBean("tieqi", 90)
    val saman = HeroBean("saman", 80)
    val sishen = HeroBean("sishen", 70)
    val yuren = HeroBean("yuren", 60, compareRate = 0.9)
    val baoku = HeroBean("shexian", 30, needCar = true, isGongCheng = true, compareRate = 0.9)
    val xiaoye = HeroBean("xiaoye", 30)
    val dijing = HeroBean("dijing", 30)
    val huanqiu = HeroBean("huanqiu", 20, needCar = false, compareRate = 0.95)
    val guangqiu = HeroBean("guangqiu", 0, needCar = false)
    var heros = arrayListOf(zhanjiang, tieqi, saman, sishen, yuren, baoku, xiaoye, dijing, huanqiu, guangqiu)
    private fun testHeroRec(img: BufferedImage) {
        var heros = arrayListOf(dijing, huanqiu, zhanjiang, tieqi, saman, sishen, yuren, baoku, xiaoye, guangqiu)
        GlobalScope.launch {
            getPreHeros(img, heros)
        }
    }


    open suspend fun getPreHeros(img: BufferedImage, heros: List<HeroBean>, timeout: Long = 2300) =
        suspendCancellableCoroutine<List<HeroBean?>?> {
            val startTime = System.currentTimeMillis()
            GlobalScope.launch {
                var h1: HeroBean? = null
                var h2: HeroBean? = null
                var h3: HeroBean? = null

                try {
                    withTimeout(timeout) {
                        while (h1 == null || h2 == null || h3 == null) {
                            val hero1 = if (h1 == null) {
                                async { getHeroAtRect(heros, img.getSubImage(Config.zhandou_hero1CheckRect), 0) }
                            } else null
                            val hero2 = if (h2 == null) {
                                async { getHeroAtRect(heros, img.getSubImage(Config.zhandou_hero2CheckRect), 1) }
                            } else null
                            val hero3 = if (h3 == null) {
                                async { getHeroAtRect(heros, img.getSubImage(Config.zhandou_hero3CheckRect), 2) }
                            } else null

                            if (h1 == null) {
                                h1 = hero1?.await()
                                if (h1 != null) {
                                    log("识别到英雄:${h1?.heroName} 耗时：${System.currentTimeMillis() - startTime}")
                                }
                            }
                            if (h2 == null) {
                                h2 = hero2?.await()
                                if (h2 != null) {
                                    log("识别到英雄:${h2?.heroName} 耗时：${System.currentTimeMillis() - startTime}")
                                }
                            }
                            if (h3 == null) {
                                h3 = hero3?.await()
                                if (h3 != null) {
                                    log("识别到英雄:${h3?.heroName} 耗时：${System.currentTimeMillis() - startTime}")
                                }
                            }
//                        var i=0;
//                        if(h1!=null)i++
//                        if(h2!=null)i++
//                        if(h3!=null)i++
//                        if(i>=2)break
//                        if (h1 == null || h2 == null || h3 == null) {//省去最后的100ms
//                            delay(50)
//                        }
                        }
                        logOnly("getPreHeros cost time:${System.currentTimeMillis() - startTime}")
                        it.resume(arrayListOf(h1, h2, h3))
//                    return@withTimeout arrayListOf(h1, h2, h3)
                    }
                } catch (e: Exception) {
                    if (h1 == null && h2 == null && h3 == null) {
                        it.resume(null)
//                    return null
                    } else {
                        it.resume(arrayListOf(h1, h2, h3))
//                    return arrayListOf(h1, h2, h3)
                    }
                }
            }
        }

    //原来上卡判断是否上去了，耗时189ms，识别手卡200ms-500ms不等，日志大部分在400ms左右，
    //更改3卡分别截图到只截图一张大图后（截图的api内部是同步的,所以即使使用时加了async和await，但截图的耗时还是线性想加的，只不过对比hero的时候是并行的)，
    // 等待测试结果。像上面189，单次识别就要40多ms，里面还有个delay30ms的。所以两轮过去（一般两三轮比较就差不多知道上去没有了），大约就是3个40+2个30，大约就是180左右
    //所以日志大部分上卡耗时都是189左右，剩下的就是比较的时间，可见比较耗时贼少，主要还是截图浪费了时间。
    open suspend fun getHeroAtRect(heros: List<HeroBean>, img: BufferedImage, position: Int) =
        suspendCancellableCoroutine<HeroBean?> {
            val hero = heros.filter {//满了就不会出现在预选卡里，减少比较
                !it.isFull()
            }.firstOrNull {
//            ImgUtil.isImageSim(img, it.img)
//            ImgUtil.isHeroSim(img,it.img)
                it.fitImage(img, position)
            }
            if (Config.debug) {
                log(img)
            }
            if (hero != null) {
                logOnly("getHeroAtRect ${hero?.heroName ?: "无结果"}")
            } else logOnly("getHeroAtRect null")
            it.resume(hero)

        }

    fun getAy39SelectedPositions(chePos: Int, img: BufferedImage? = null): List<Int> {
        var list = getAy39SelectedPositions(img)



        if (list.isNotEmpty()) {
            if (chePos == 0) {
                return list[0]
            } else {
                return list[1]
            }
        }
        return arrayListOf()
    }

    private fun is39BlackNull(color: Color): Boolean {
        if (colorCompare(color, Config.Color_AY39_Null, 20))
            return true

        if (color.red < 20 && color.blue < 20 && color.green < 20) {
            return true
        }
        return false

    }

    private fun isBlack(img: BufferedImage, rect: MRect): Boolean {
        var count = rect.hasColorCount(
            Config.Color_AY39_Null,
            25, img
        )
//        return count > rect.width * rect.height * 0.2
        return count > 30
    }

    var imgAy39Clear: BufferedImage? = null

    fun getAy39SelectedPositions(img2: BufferedImage? = null): List<List<Int>> {
        log("ay39,getAy39SelectedPositions start")
        if (imgAy39Clear == null) {
            imgAy39Clear = getImageFromRes("ay39clear.png")
        }
        val img = img2 ?: getImage(App.rectWindow)
        val list = arrayListOf<List<Int>>()
        var line1Y = 290
        var line2Y = 354

        var startX = 375//300


        var sim = 20
        var firstBlockbgX = 0

        val r = 13
        var checkBlackWidth = 192

        var endX = 730 - checkBlackWidth
        var allBlack = true
        for (x in startX..endX) {
            allBlack = true
            var blackRect = MRect.createWH(x, 262, checkBlackWidth, 1)
            if (blackRect.right > img.width - 100) return list
            var colorList = arrayListOf<Color>()
            var count = (blackRect.left..blackRect.right).count {
                var color = Color(img.getRGB(it, 262))
                if (color.equals(Color(imgAy39Clear!!.getRGB(it, 262)))) {
                    allBlack = false
                }
                var s = is39BlackNull(color) && !color.equals(Color(imgAy39Clear!!.getRGB(it, 262)))
                if (!s) {
                    colorList.add(color)
//                    log("x :$it color is ")
                }
                s
            }
            if (allBlack) {
                allBlack = count > (checkBlackWidth * 0.9)
            }

            if (allBlack) {

                firstBlockbgX = x

                var left0 = firstBlockbgX + 22
                var left1 = firstBlockbgX + 66

                var right0 = firstBlockbgX + 122
                var right1 = firstBlockbgX + 166


                var listLeft = arrayListOf<Int>()
                var color = Config.Color_AY39_Left

                var check1 = MRect.createPointR(MPoint(left0, line1Y), r)

                if (check1.hasSimColor(img, Config.Color_AY39_Right, sim)) {
                    continue
                }

                if ((!check1.hasSimColor(img, color, sim) || isBlack(
                        img,
                        check1
                    ))
                ) {
                    listLeft.add(5)
                }

                check1 = MRect.createPointR(MPoint(left1, line1Y), r)
                if (check1.hasSimColor(img, Config.Color_AY39_Right, sim)) {
                    continue
                }
                if ((!check1.hasSimColor(img, color, sim) || isBlack(
                        img,
                        check1
                    ))
                ) {
                    listLeft.add(4)
                }

                check1 = MRect.createPointR(MPoint(left0, line2Y), r)
                if (check1.hasSimColor(img, Config.Color_AY39_Right, sim)) {
                    continue
                }
                if ((!check1.hasSimColor(img, color, sim) || isBlack(
                        img,
                        check1
                    )
                            )
                ) {
                    listLeft.add(3)
                }

                check1 = MRect.createPointR(MPoint(left1, line2Y), r)
                if (check1.hasSimColor(img, Config.Color_AY39_Right, sim)) {
                    continue
                }
                if ((!check1.hasSimColor(img, color, sim) || isBlack(
                        img,
                        check1
                    ))
                ) {
                    listLeft.add(2)
                }
                if (listLeft.size == 1 || listLeft.size == 2) {

                    var listRight = arrayListOf<Int>()
                    var color = Config.Color_AY39_Right

                    check1 = MRect.createPointR(MPoint(right0, line1Y), r)
                    if (check1.hasSimColor(img, Config.Color_AY39_Left, sim)) {
                        continue
                    }
                    if ((!check1.hasSimColor(img, color, sim) || isBlack(
                            img,
                            check1
                        ))
                    ) {
                        listRight.add(5)
                    }

                    check1 = MRect.createPointR(MPoint(right1, line1Y), r)
                    if ((!check1.hasSimColor(img, color, sim) || isBlack(
                            img,
                            check1
                        ))
                    ) {
                        listRight.add(4)
                    }

                    check1 = MRect.createPointR(MPoint(right0, line2Y), r)
                    if ((!check1.hasSimColor(img, color, sim) || isBlack(
                            img,
                            check1
                        ))
                    ) {
                        listRight.add(3)
                    }
                    check1 = MRect.createPointR(MPoint(right1, line2Y), r)
                    if ((!check1.hasSimColor(img, color, sim) || isBlack(
                            img,
                            check1
                        ))
                    ) {
                        listRight.add(2)
                    }

                    if (listRight.size == 1 || listRight.size == 2) {
                        //左右都识别到更保险，万一只识别左边 左边碰巧满足了，就错了，两边一起的话，容错率就更高了
                        list.clear()
                        list.add(listLeft)
                        list.add(listRight)



                        if (list.isNotEmpty()) {
//                            colorList.forEach {
//                                log("dis color is ${it}")
//                            }
                            log("ay39,车1 需下卡位置：${list[0]}")
                            log("ay39,车0 需下卡位置：${list[1]}")
//                            log(img)
                        } else {
                            log("ay39  未识别到 ")
                        }

                        return list
                    }
                }


            }
        }

        log("ay39  未识别到 ")
//        log(img)

        return list
    }

    val pukes = arrayListOf<Pair<BufferedImage, Int>>()
    fun getPuke(): Int {
        val img = getImage(Config.AY_Puke_rect)
        if (pukes.isEmpty()) {
            resFile("$anyueFolder/puke").listFiles().forEach {
                pukes.add(
                    Pair(
                        getImageFromFile(it),
                        it.nameWithoutExtension.substring(it.nameWithoutExtension.lastIndexOf("_")+1).toInt()
                    )
                )
            }
        }
        pukes.forEach {
            if(ImgUtil.isImageSim(img,it.first,0.95)){
                return it.second
            }
        }

        return -1

    }

}