package tasks.daxuanwo.utils

import data.Config
import data.MRect
import getImage
import getImageFromRes
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import log
import opencv.MatSearch
import opencv.toMat

object WX89 {

    val rect1 = MRect.createWH(460, 194, 72, 72)

    val rect2 = MRect.createWH(460 + 2 + 72, 194, 72, 72)
    val rect3 = MRect.createWH(460 + 2 + 72 + 3 + 72, 194, 72, 72)
    val rect4 = MRect.createWH(460, 194 + 3 + 72, 72, 72)
    val rect5 = MRect.createWH(460 + 2 + 72, 194 + 3 + 72, 72, 72)
    val rect6 = MRect.createWH(460 + 2 + 72 + 3 + 72, 194 + 3 + 72, 72, 72)
    val rect7 = MRect.createWH(460, 194 + 3 + 72 + 3 + 72, 72, 72)
    val rect8 = MRect.createWH(460 + 2 + 72, 194 + 3 + 72 + 3 + 72, 72, 72)
    val rect9 = MRect.createWH(460 + 2 + 72 + 3 + 72, 194 + 3 + 72 + 3 + 72, 72, 72)
    val rects = listOf(rect1, rect2, rect3, rect4, rect5, rect6, rect7, rect8, rect9)


    var doing = false

    fun autoDo(over:()->Boolean) {
        MainData.curGuanKaDes.value = "开启了自动点击，按0键可以终止"
        doing = true
        GlobalScope.launch {
            //适当加个delay
            var folder = "${Config.platName}/tezheng/xuanwo/xw89"

            while (!over.invoke() && doing) {
                rects.forEachIndexed { index, mRect ->
                    if(!doing){
                        return@launch
                    }
                    log("识别位置:${index}")
                    val okImg = getImageFromRes("${folder}/xw89_${index}.png").toMat()

                    var img = getImage(mRect.scale(1.2f)).run {
                        log(this)
                        toMat()
                    }
                    var count = 0
                    while (!MatSearch.templateFit(okImg, img) && doing) {
                        count++
                        log("识别失败,点击旋转第${count}次")
                        mRect.clickPoint.click()
                        delay(300)
                        img = getImage(mRect.scale(1.2f)).run {
                            log(this)
                            toMat()
                        }
                    }
                    log("识别成功,共点击$count 次")
                }
            }

        }

    }

}