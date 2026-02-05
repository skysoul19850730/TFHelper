package utils

import App
import colorCompare
import data.Config
import data.MPoint
import data.MRect
import foreach
import getImage
import getImageFromFile
import getImageFromRes
import getSubImage
import log
import logOnly
import model.CarDoing
import resFile
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
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
    fun getPuke(img2:BufferedImage?=null): Int {
        val img =img2?: getImage(Config.AY_Puke_rect)
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
                log("识别到扑克：${it.second}")
                return it.second
            }
        }

        return 0

    }


    class Ay139Icon(
        val name:String,
        val border:String,
        val fill:String
    ){
        override fun toString(): String {
            return "name:$name,border:$border,fill:$fill"
        }

        override fun equals(other: Any?): Boolean {
            if (other is Ay139Icon) {
                if(other.name==name && other.border==border && other.fill==fill){
                    return true
                }
            }
            return false
        }
    }

    //583,180 54 54

    val rect1 = MRect.createWH(374,206,78,78)
    val rect2 = MRect.createWH(474,168,78,78)
    val rect3 = MRect.createWH(572,168,78,78)
    //    val rect3 = MRect.createWH(583,180,55,55)
    val rect4 = MRect.createWH(668,206,78,78)

    fun testFill(rect:MRect, img:BufferedImage):String{
        val wcount = rect.hasColorCount(Color.WHITE,20,img)
        log(wcount)
        if(wcount>800){
            return "填充"
        }
        return "描边"
    }

    fun testBorder(img:BufferedImage): String{
        var vLineWhiteCount = 0
        for(x in 0 until 10){
            vLineWhiteCount = 0
            for(y in 0 until img.height){
                val rgb = Color(img.getRGB(x,y))
                if(rgb.red>240 && rgb.green>240 && rgb.blue>240){
                    vLineWhiteCount++
                }
            }
            if(vLineWhiteCount>img.height*0.8){
                return "方形"
            }
        }
        return "圆形"
    }

    fun getRunnitIcon(img: BufferedImage):Ay139Icon?{
        //改大图，找到一张运行中的法杖描边图（可能斧头描边图也可以？）
        val imgFlag = getImageFromRes("$anyueFolder/ay139flagtop.png")
        val checkPoints = arrayListOf<MPoint>()

        imgFlag.foreach { i, i2 ->
            if(imgFlag.getRGB(i,i2)!=0){
                checkPoints.add(MPoint(i,i2))
            }
            false
        }
        //去最开始的一个差不多的rect，左边留出一些像素空间，从右边开始遍历，得到结果就可以结束，不用管太靠后的地方
        //这里肯定也是提前就开冰，冰了之后才来识别，所以一出现就可以识别了，如果漏了就下一张图基本还在这个rect内

        return null
    }

    fun getDiffIcon(img:BufferedImage):Ay139Icon?{
        val imgFlag = getImageFromRes("$anyueFolder/ay139flagtop.png")
        val checkPoints = arrayListOf<MPoint>()

        imgFlag.foreach { i, i2 ->
            if(imgFlag.getRGB(i,i2)!=0){
                checkPoints.add(MPoint(i,i2))
            }
            false
        }
        val img1 = img.getSubImage(rect1)
        val img2 = img.getSubImage(rect2)
        val img3 = img.getSubImage(rect3)
        val img4 = img.getSubImage(rect4)

        val f1 = testFill(rect1,img)
        val b1 = testBorder(img1)
        val c1 = test4Rect(checkPoints,imgFlag,img1)
        val data1 = Ay139Icon(c1,b1,f1)
        log("第一个是${data1.name}, ${data1.fill}，${data1.border}")

        val f2 = testFill(rect2,img)
        val b2 = testBorder(img2)
        val c2 = test4Rect(checkPoints,imgFlag,img2)
        val data2 = Ay139Icon(c2,b2,f2)
        log("第二个是${data2.name}, ${data2.fill}，${data2.border}")

        val f3 = testFill(rect3,img)
        val b3 = testBorder(img3)
        val c3 = test4Rect(checkPoints,imgFlag,img3)
        val data3 = Ay139Icon(c3,b3,f3)
        log("第三个是${data3.name}, ${data3.fill}，${data3.border}")

        val f4 = testFill(rect4,img)
        val b4 = testBorder(img4)
        val c4 = test4Rect(checkPoints,imgFlag,img4)
        val data4 = Ay139Icon(c4,b4,f4)
        log("第四个是${data4.name}, ${data4.fill}，${data4.border}")

        listOf(data1,data2,data3,data4).apply {
            if(count { it.name=="法杖" }==1){
                return first { it.name == "法杖" }
            }
            if(count { it.name=="斧头" }==1){
                return first { it.name == "斧头" }
            }
            if(count { it.fill == "填充" } ==1){
                return first { it.fill == "填充" }
            }
            if(count { it.fill == "描边" } ==1){
                return first { it.fill == "描边" }
            }
            if(count { it.border == "方形" } ==1){
                return first { it.border == "方形" }
            }
            if(count { it.border == "圆形" } ==1 ){
                return first { it.border == "圆形" }
            }
        }
        return null
    }
    //374 206  78
    //474 168  78
    // 572 168  78
    //668  206   78
    fun test4Rect(checkPoints:List<MPoint>, imgFlag:BufferedImage, img:BufferedImage):String{
        val dx = img.width - imgFlag.width
        val dy = img.height - imgFlag.height

        for(x in 0 until dx){
            for(y in 0 until dy){
                val newImg = img.getSubImage(MRect.createWH(x,y,imgFlag.width,imgFlag.height))
                if(test4SameRect(checkPoints,newImg)){
                    return "法杖"
                }
            }
        }

        return "斧头"

    }
    fun test4SameRect(checkPoints:List<MPoint>,img:BufferedImage):Boolean{
        var sameCount =
            checkPoints.count {
                val rgb = Color(img.getRGB(it.x,it.y))
                !(rgb.red<200 && rgb.green<200 && rgb.blue<200)
            }
//        if(sameCount>400) {
//            log("test4SameRect,count is $sameCount")
//        }
        return sameCount>checkPoints.size*0.8
    }

}