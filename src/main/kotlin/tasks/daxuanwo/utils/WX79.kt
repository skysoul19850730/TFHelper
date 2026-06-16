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
            delay(9000) //大约9秒开打
            val platImg = getImageFromRes("${Config.platName}/tezheng/xuanwo/xw79.png")

            while(!over.invoke() && doing){

                val img = getImage(App.rectWindow)

                //这里原来是怕rate得比较值设置太低，会误认为识别到气泡，所以加了rate>0.5，写此注释时改成了0.2尝试
                //但实际用0.1都是比计较准得，看日志，比如识别到不了0.5，但会打印一堆0.1得，因为我会手动点掉气泡，日志也会在我点掉后不再输出0.1得日志
                //所以实际不会太误判，另一种更靠谱点的就是抓到boss出技能的时间，在这个时间过后的比如1秒内，连续识别
                //找到识别率最大的那个（即使它是0.05也可以），1秒内多次轮询取新图查验，因为差几百毫秒可能气泡被挡的就会有少的时候，方便更精准验证
                //另外可以增加补救，就是执行click后观察有没有出现英雄详情面板，出了就关掉（也代表不是它的气泡）
                //先继续用0.2尝试，毕竟还没时间抓图
                for (index in rects.indices) {
                    val it = rects[index]
                    val tarImg = img.getSubImage(it)
                    val rate = ImgUtil.slidingPixelMatch(platImg,tarImg).first
                    if(rate>0.2){
                        println("识别到气泡在位置:${index} rate :${rate}")
                        log(img)
                        it.clickPoint.click()
                        delay(2000)
                        break
                    }else if(rate>0.1){
                        println("按0.1可能识别到气泡在位置:${index} rate :${rate}")
                        log(img)
                    }
                }
                delay(200)

            }

        }

    }

    /**
     * 以后这里可以补充下这个方法，开启自动关闭英雄面板
     * 两个原因，因为脚本是会帮队友点气泡的（偶尔尝试出来可以帮队友点），所以一个是可能自己识别不准
     * 点出来，需要关闭补救，一个是队友可能先点到了气泡（发生在识别和点击之间），每次识别会间隔200ms，如果
     * 间隔时点了就没影响，点了之后自然就识别不到了
     */
    private fun autoCloseHeroBoard(){

    }

}