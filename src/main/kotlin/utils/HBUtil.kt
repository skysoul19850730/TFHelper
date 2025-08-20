package utils

import colorCompare
import data.Config
import data.MRect
import getImage
import getImageFromFile
import log
import utils.ImgUtil.forEach
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import kotlin.system.measureTimeMillis

object HBUtil {

    fun test199Bai(){
        File(Config.caiji_main_path, "tmphb199").listFiles().forEach {

            var bai = is199Bai(getImageFromFile(it))
            log("图片${it.name}  ${if(bai) "是" else "不是"}199白")

        }
    }

    fun is199Bai(image: BufferedImage?):Boolean{
        var result = false
        measureTimeMillis {
            result = is199BaiDo(image)
        }.apply {
            log("is199Bai coast ${this}")
        }
        return result
    }
    //x  400-700  y 300, wh 60
   private fun is199BaiDo(image: BufferedImage?):Boolean{
        val img = image?:getImage(App.rectWindow)
        for(x in 700 downTo  400){
            if(isRectAllBai(img,MRect.createWH(x,300,60,60)))
            {
                return true
            }
        }

        return false
    }

    private fun isRectAllBai(img:BufferedImage,rect: MRect):Boolean{
        var wCount = 0
        rect.forEach { x, y ->
            var color = img.getRGB(x, y).run {
                Color(this)
            }
            if (colorCompare(color, Color.WHITE, 10)) {
                wCount++
            }
        }
        val total = rect.width * rect.height
        if(wCount*1f/total>0.95){
            return true
        }
        return false
    }

}