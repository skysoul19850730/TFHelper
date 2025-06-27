package test

import com.sun.jna.platform.WindowUtils
import data.Config
import data.MPoint
import getImage
import kotlinx.coroutines.delay
import logOnly
import utils.ImgUtil
import utils.MRobot
import java.awt.Shape
import java.awt.Window
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage

object WindowTest {
    fun test(){
        WindowUtils.getAllWindows(true).forEach {
            logOnly("${it.title}")
            if(it.title.contains("微信")){
                var x = it.locAndSize.x
                var y = it.locAndSize.y
                MRobot.robot.mouseMove(x+15,y+15)
                MRobot.robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
                MRobot.robot.delay(3000)
                MRobot.robot.mouseMove(15,15)
                MRobot.robot.delay(3000)
                MRobot.robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
            }
        }
    }

    suspend fun getLongPic():BufferedImage{
        var imgList = arrayListOf<BufferedImage>()
        var img = getImage(Config.changtuRect)
        imgList.add(img)

        var notDown = true

        while(notDown){
            MRobot.singleClick(MPoint(600,400),null)
            MRobot.robot.keyPress(KeyEvent.VK_PAGE_DOWN)
            delay(200)
            MRobot.robot.keyRelease(KeyEvent.VK_PAGE_DOWN)
            delay(1500)
            var img2 = getImage(Config.changtuRect)
            var imgTopSec = img2.getSubimage(0,0,img.width,15)


            var downOVer = false
            var img2Add = true
            for(y in 0 until img.height-15){
                var iT = img.getSubimage(0,y,img.width,15)
                if(ImgUtil.isImageSim(iT,imgTopSec,0.999)){
                    var h =img2.height-img.height+y
                    if(h>0) {
                        img2 = img2.getSubimage(0, img.height - y, img2.width, img2.height - img.height + y)
                    }else img2Add = false
                    notDown = false
                    break
                }
            }

            if(img2Add) {
                imgList.add(img2)
            }
            img = img2

        }

        //imglist中得图合成到一张长图里
        var totalH = imgList.sumOf {
            it.height
        }
        var imgR = BufferedImage(imgList[0].width,totalH,BufferedImage.TYPE_INT_RGB)
        var y = 0
        for(img in imgList){
            imgR.graphics.drawImage(img,0,y,null)
            y += img.height
        }

        return imgR
    }
}