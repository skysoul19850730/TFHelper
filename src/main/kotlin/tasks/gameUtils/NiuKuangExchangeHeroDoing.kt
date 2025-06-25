package tasks.gameUtils

import data.*
import getImage
import getSubImage
import kotlinx.coroutines.*
import tasks.HeroDoing
import kotlin.coroutines.resume
import log
import logOnly
import model.CarDoing
import tasks.XueLiang
import utils.MRobot
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage

//牛和狂互换
class NiuKuangExchangeHeroDoing() : HeroDoing(flags = FLAG_KEYEVENT) {
    var niutou = HeroCreator.gugu.create()
    val kuangjiang = HeroCreator.kuangjiang.create()


    val gq = HeroCreator.huanqiu.create()
    val dijing = HeroCreator.dijing.create()

    override fun initHeroes() {
        heros = arrayListOf()
        heros.add(niutou)
        heros.add(kuangjiang)


        heros.add(gq)
        heros.add(dijing)
    }


    override suspend fun doUpHero(heroBean: HeroBean, position: Int, hasKuoJianClicked: Boolean) {
        var rect = when (position) {
            0 -> Config.zhandou_hero1CheckRect
            1 -> Config.zhandou_hero2CheckRect
            else -> Config.zhandou_hero3CheckRect
        }

        var uped = false
        while (!uped) {
            MRobot.singleClick(MPoint(rect.clickPoint.x, rect.clickPoint.y + 25))
            //点击上卡后，如果都空了，代表确实上去了
            uped = withTimeoutOrNull(300) {
                while (doGetPreHeros() != null) {
                    delay(10)
                }
                true
            } ?: false
        }


        if(heroBean == kuangjiang){
            delay(GameUtil.SomeDelay.value)
            downloadIt()
        }

    }

    override suspend fun shuaka(needShuaxin: Boolean): List<HeroBean?> {
        var hs: List<HeroBean?>? = null
        while (hs == null && running) {
            log("未识别到英雄")
            MRobot.singleClick(Config.zhandou_shuaxinPoint)
            delay(150)
            hs = getPreHeros(if (needShuaxin) Config.delayLong else Long.MAX_VALUE)
        }
        log("识别到英雄 ${hs?.getOrNull(0)?.heroName}  ${hs?.getOrNull(1)?.heroName}  ${hs?.getOrNull(2)?.heroName}")
        return hs!!
    }


    override suspend fun getPreHeros(timeout: Long): List<HeroBean?>? {
        return super.getPreHeros(700)
    }


    override suspend fun getHeroAtRect(img: BufferedImage,position: Int) = suspendCancellableCoroutine<HeroBean?> {
        //只需要检测没满的，满的不可能再出现
        val hero = heros.firstOrNull {
//            ImgUtil.isImageSim(img, it.img)
//            ImgUtil.isHeroSim(img,it.img)
            it.fitImage(img,position)
        }
        if (Config.debug) {
            log(img)
        }
        if (hero != null) {
            logOnly("getHeroAtRect ${hero?.heroName ?: "无结果"}")
        } else logOnly("getHeroAtRect null")
        it.resume(hero)

    }



    suspend fun click() {
        MRobot.singleClick(GameUtil.clickPoint.value)
    }

    private suspend fun downloadIt() {
        var cardShow = false
        withTimeoutOrNull(2000) {
            while (!cardShow) {
                click()
                cardShow = withTimeoutOrNull(500) {
                    while (!Recognize.saleRect.isFit()) {//下卡时 被结束的弹窗挡住，这里一直不fit（挡住了就检测不到出售按钮）。然后也不会执行 结束的按钮点击。所以这里加个超时
                        delay(10)//妈的，这里不加delay就检测不会timeout，fuck
                    }
                    log(getImage(App.rectWindow, null))
                    log("售卖按钮就位")
                    true
                } ?: false
            }
        }

        var cardMiss = false
        while (!cardMiss) {
            MRobot.singleClick(CarDoing.salePoint)//todo 这个按钮可能点完没效果但是还产生了动画，导致下第二个卡或本次都失败了实际。需要关闭按钮来检测
            delay(10)
            //其实只要上一行代码 一产生点击，这个按钮就变小了，就不fit了。所以这里其实判断没有用处。
            cardMiss = withTimeoutOrNull(500) {
                while (CarDoing.cardClosePoint.isFit() || Recognize.saleRect.isFit()) {
                    delay(10)//妈的，这里不加delay就检测不会timeout，fuck
                }
                log(getImage(App.rectWindow, null))
                log("卡片弹窗已关闭")
                true
            } ?: false
        }
    }



    override suspend fun dealHero(heros: List<HeroBean?>): Int {

        if(state== 0){//0备狂将，一直到 按键改变后 上狂将 再下狂将 上牛 备狂将
            if(heros.indexOf(kuangjiang)>-1){
                while(state == 0){
                    delay(100)
                }
                downloadIt()
                return heros.indexOf(kuangjiang)
            }
        }else{
            var index = heros.indexOf(niutou)
            if(index>-1){
                state = 0
                return index
            }
        }

     return -1
    }

    var state = 0//备狂将



    override fun changeHeroWhenNoSpace(heroBean: HeroBean): HeroBean? {
        return null
    }

    override suspend fun onKeyDown(code: Int): Boolean {

        if(code == KeyEvent.VK_NUMPAD0){
            log("按了0了，开始切狂切牛一次")
            state=1
            return true
        }

        return super.onKeyDown(code)
    }
}