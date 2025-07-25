package tasks.guankatask

import data.Config
import getImage
import getImageFromFile
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import log
import resFile
import utils.ImgUtil
import java.awt.image.BufferedImage

class GuankaTask {

    var currentGuanIndex = 0

    var changeListener: ChangeListener? = null

    var running = false

    var imgList = arrayListOf<BufferedImage>().apply {
        var suFolder = resFile("${Config.platName}/guanka/gf1")
        suFolder.listFiles().sortedBy {
            it.name.substring(1, it.name.length - 4).toInt()
        }.forEach {
            add(getImageFromFile(it))
        }
    }

    interface ChangeListener {
        fun onGuanChange(guan: Int)
    }


    fun setCurGuanIndex(guan: Int) {
        currentGuanIndex = guan
        MainData.guan.value = currentGuanIndex + 1
        log("当前关卡：${currentGuanIndex + 1}")

        changeListener?.onGuanChange(currentGuanIndex + 1)
    }

    fun start() {
        if (running) return
        running = true
        GlobalScope.launch {
            while (running) {

                var curImg = getImage(Config.zhandou_hezuo_guankaRect)
                var has = false
                for (i in currentGuanIndex..imgList.size - 1) {
                    if (ImgUtil.isImageSim(curImg, imgList.get(i), 0.97)) {
                        if (currentGuanIndex != i) {
                            setCurGuanIndex(i)
                            has = true
                            //识别到管卡变化，就延迟多点再循环
//                            delay(2000)
                            break
                        }
                    }
                }
                if (!has) {
//                    log("未识别到关卡")
//                    log(curImg)
                }

                delay(50)
            }
        }
    }

    fun stop() {
        running = false
        MainData.guan.value = 0
    }
}