package tasks.daxuanwo.utils

import data.Config
import data.MRect
import getImage
import getImageFromRes
import getSubImage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import log
import opencv.MatSearch
import opencv.hasImage
import opencv.toMat
import kotlin.coroutines.cancellation.CancellationException

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

    //    val rects = listOf(rect1)
    val rects = listOf(rect1, rect2, rect3, rect4, rect5, rect6, rect7, rect8, rect9)


    var doing = false

    // 共享队列用于接收点击请求
    private val clickChannel = Channel<Pair<MRect, CompletableDeferred<Unit>>>(Channel.UNLIMITED)

    // 互斥锁，确保同一时间只有一个协程执行点击操作
    private val clickMutex = Mutex()

    // 启动一个协程专门处理点击任务
    init {
        GlobalScope.launch {
            for (request in clickChannel) {
                clickMutex.withLock {
                    request.first.clickPoint.click()
                    request.second.complete(Unit)
                    // 延迟 100ms
                    delay(50)
                }
            }
        }
    }


    fun autoDo(over: () -> Boolean) {
        MainData.curGuanKaDes.value = "开启了自动点击，按0键可以终止"
        doing = true
        GlobalScope.launch {
            //适当加个delay
            delay(6000)//加速了，boss出来几秒就扔龙了
//            delay(28000)//16秒后出土，再开始即可
            var folder = "${Config.platName}/tezheng/xuanwo/xw89"

//            while (!over.invoke() && doing) {//这里是防止队友又给转走，
            rects.forEachIndexed { index, mRect ->
                if (!doing) {
                    return@launch
                }
                GlobalScope.launch {
                    log("识别位置:${index}")
                    val okImg = getImageFromRes("${folder}/xw89_${index}.png")

                    var img = getImage(mRect.scale(1.2f))
                    var count = 0
                    while (!img.hasImage(okImg) && doing && !over.invoke()) {
                        count++
                        log("位置${index}识别失败,点击旋转第${count}次")
                        val completed = CompletableDeferred<Unit>()
                        clickChannel.send(mRect to completed)
                        completed.await()
                        delay(400)
                        log("位置${index}识别失败,延迟后重新获取图片")
                        img = getImage(mRect.scale(1.2f))
                    }
                    log("识别成功,共点击$count 次")
                }

            }
//            }

        }

    }

    fun testOne(over: () -> Boolean) {
        MainData.curGuanKaDes.value = "开启了自动点击，按0键可以终止"
        doing = true
        GlobalScope.launch {
            //适当加个delay
//            delay(2000)
//            delay(15000)//16秒后出土，再开始即可
            var folder = "${Config.platName}/tezheng/xuanwo/xw89"

//            while (!over.invoke() && doing) {//这里是防止队友又给转走，
            rects.forEachIndexed { index, mRect ->
                if (!doing) {
                    return@launch
                }
                GlobalScope.launch {
                    log("识别位置:${index}")
                    val okImg = getImageFromRes("${folder}/xw89_${index}.png")

                    var img = getImage(mRect.scale(1.2f))
                    var count = 0
                    while (!img.hasImage(okImg) && doing && !over.invoke()) {
                        count++
                        log("位置${index}识别失败,点击旋转第${count}次")
//                        val completed = CompletableDeferred<Unit>()
//                        clickChannel.send(mRect to completed)
//                        completed.await()
                        delay(400)
                        log("位置${index}识别失败,延迟后重新获取图片")
                        img = getImage(mRect.scale(1.2f))
                    }
                    log("识别成功,共点击$count 次")
                }

            }
//            }

        }

    }
}