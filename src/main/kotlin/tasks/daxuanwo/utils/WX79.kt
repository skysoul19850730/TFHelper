package tasks.daxuanwo.utils

import data.Config
import data.MRect
import getImage
import getImageFromRes
import getSubImage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import log
import opencv.MatSearch
import opencv.toMat
import utils.ImgUtil

object WX79 {



    var doing = false

    fun autoDo(rects:List<MRect>, over:()->Boolean) {
        MainData.curGuanKaDes.value = "开启了自动点击，按0键可以终止"
        doing = true
        GlobalScope.launch {
            //适当加个delay
            val platImg = getImageFromRes("${Config.platName}/tezheng/xuanwo/xw79.png")

            while(!over.invoke() && doing){

                val img = getImage(App.rectWindow)

                for (index in rects.indices) {
                    val it = rects[index]
                    val tarImg = img.getSubImage(it)
                    val rate = ImgUtil.slidingPixelMatch(platImg,tarImg).first
                    if(rate>0.75){
                        println("识别到气泡在位置:${index}")
                        log(img)
                        it.clickPoint.click()
                        delay(2000)
                        break
                    }
                }
                delay(200)

            }

        }

    }

}