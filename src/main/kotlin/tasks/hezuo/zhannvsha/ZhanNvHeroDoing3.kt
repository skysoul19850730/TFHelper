package tasks.hezuo.zhannvsha

import App
import data.Config
import data.HeroBean
import data.HeroCreator
import getImage
import getImageFromRes
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import log
import tasks.SimpleHeZuoHeroDoing
import tasks.Zhuangbei
import utils.ImgUtil
import java.awt.image.BufferedImage

class ZhanNvHeroDoing3 : SimpleHeZuoHeroDoing() {//默认赋值0，左边，借用左边第一个position得点击，去识别车位置后再更改


    val zhanjiang = HeroCreator.zhanjiang2.create()
    val nvwang = HeroBean("nvwang", 90)
    val gongjiang = HeroBean("gongjiang", 80)
    val saman = HeroBean("saman2", 70)
    val shahuang = HeroBean("shahuang", 60)
    val niutou = HeroCreator.niutou2.create()
    val maomi = HeroBean("maomi", 40)
    val xiaoye = HeroBean("xiaoye", 30)
    val huanqiu = HeroBean("huanqiu", 20, needCar = false)
    val guangqiu = HeroBean("guangqiu", 0, needCar = false)


    override fun initHeroes() {
        heros = arrayListOf(zhanjiang, nvwang, gongjiang, saman, shahuang, niutou, maomi, xiaoye, huanqiu, guangqiu)

        guanDealList.add(GuanDeal(0, { isGuanKa0Over() }, {
            guanDeal1(this)
        }))
        guanDealList.add(GuanDeal(250, { isGuanKa250Over() }, { zhuangbei { longxin } }))
//        guanDealList.add(GuanDeal(277, onlyDoSomething = {
//            carDoing.downHero(niutou)
//        }))
//        guanDealList.add(GuanDeal(281, {
//            xiaoye.isFull()
//
//        }, {
//            upAny(xiaoye)
//        }, onGuanDealStart = {
////            needCheckQian = false//199钱肯定多，节省识别钱的时间
////            carDoing.downCardSpeed = true
//        }))
        guanDealList.add(GuanDeal(301, {
           xiaoye.isFull() && maomi.isFull() && shengjian

        }, {
            upAny(xiaoye,maomi, zhuangbei = { shengjian })
        }, onGuanDealStart = {
            carDoing.downHero(gongjiang)
            carDoing.downHero(niutou)
        }))

        curGuanDeal = guanDealList.get(0)
    }

    suspend fun guanDeal1(heros: List<HeroBean?>): Int {
        if (!zhanjiang.isGold()) {//战将没满,不会开第四个格子
//                log("战将没满")
            var index = heros.indexOf(zhanjiang)//有战将用战将
            if (index > -1) return index
            index = heros.indexOf(guangqiu)
            if (index > -1) {//有光球
                if (zhanjiang.isInCar()) {//车上有战将（未满）
                    this.heros.filter { //下掉不满星的，用光球
                        it.isInCar() && !it.isFull() && it != zhanjiang
                    }.forEach {
                        carDoing.downHero(it)
                    }
                    return index
                } else {//车上没战将
                    //候选里有冰女用冰女，有娇女用娇女，没有就用光
                    if (niutou.isInCar()) {//有猴子了，优先娇女
                        val jiaonvP = heros.indexOf(saman)
                        if (jiaonvP > -1) return jiaonvP
                    }
                    val bingnvP = heros.indexOf(niutou)
                    if (bingnvP > -1) return bingnvP
                    val jiaonvP = heros.indexOf(saman)
                    if (jiaonvP > -1) return jiaonvP
                    if (niutou.isInCar() || saman.isInCar()) {//车上有人时用光球，否则就没必要用了
                        return index
                    }
                }
            }
            //战将没满，候选里有冰女用冰女，有娇女用娇女
            if (niutou.isInCar()) {//有猴子了，优先娇女
                val jiaonvP = heros.indexOf(saman)
                if (jiaonvP > -1) return jiaonvP
            }
            val bingnvP = heros.indexOf(niutou)
            if (bingnvP > -1) return bingnvP
            val jiaonvP = heros.indexOf(saman)
            if (jiaonvP > -1) return jiaonvP

            val huanqiuP = heros.indexOf(huanqiu)
            if (huanqiuP > -1 && Zhuangbei.hasZhuangbei()
//                && !Zhuangbei.isLongxin()
                && !Zhuangbei.isQiangxi()) {
                return huanqiuP//有装备时，不是龙心或强袭 用幻（前面遇到强袭或龙心都可以）
            }

            return -1 //战将没满不会上其他英雄
        } else {
//                log("战将满了")
//            if(!delayed){//测试攒够钱速度上卡会不会漏卡
//                delay(3*60*1000)
//                delayed = true
//            }
            //战将满了 优先冰女
//            if (!niutou.isInCar()) {//没猴子 优先猴子
//                var index = heros.indexOf(niutou)
//                if (index > -1) return index
//            }


            var index =
                heros.upAny(zhanjiang,niutou,saman, gongjiang, shahuang, nvwang, zhuangbei = { qiangxi || longxin
//                        || longxin
                                                                         }, useGuang = true)

            if (index > -1) return index

            return heros.indexOf(niutou)
        }
    }
    var delayed = false

    suspend fun List<HeroBean?>.upMainHero(
        hero: HeroBean,
        useGuang: Boolean = true,
        otherHero: ArrayList<HeroBean>,
        zhuangbei: (() -> Boolean)? = null,
    ): Int {
        if (hero.isFull()) return -1
        return indexOf(hero).notOk {
            if (hero.isInCar() && useGuang && indexOf(guangqiu) > -1) {
                otherHero.forEach {
                    if (!it.isFull()) {
                        carDoing.downHero(it)
                    }
                }
                indexOf(guangqiu)
            } else {
                var index = defaultDealHero(this, otherHero)
                if (index > -1) {
                    index
                } else {
                    if (zhuangbei != null) {
                        zhuangbei { zhuangbei() }
                    } else {
                        -1
                    }
                }
            }
        }
    }

    var startTime = 0L
    override fun onStart() {
        super.onStart()
        startTime = System.currentTimeMillis()
        GlobalScope.launch {
            log("开始识别哭脸")
            var cryImgs = arrayListOf<BufferedImage>().apply {
                add(getImageFromRes("cryface1.png"))
                add(getImageFromRes("cryface3.png"))
            }
            var ok = true
            while (ok) {

                var img = getImage(Config.rectOfCryFace)
                var hasCryFace = cryImgs.find {
                    ImgUtil.isImageSim(it, img)
                } != null
                if (hasCryFace) {
                    log("识别到哭脸，退出程序，稍后重启")
                    ok = false
                    App.stop()
                    App.restartGame()
                    break
                }
                delay(100)
                ok = (guankaTask?.currentGuanIndex ?: 0) < 10 && running
            }
            log("关卡超过10，停止识别哭脸")
        }
    }

    override fun onStop() {
        var gk = guankaTask?.currentGuanIndex ?: 0
        super.onStop()
        val endTime = System.currentTimeMillis()
        val cost = (endTime - startTime) / 1000
        var time = "合作完毕：$gk 关，用时： ${cost / 60}分${cost % 60}秒"
        log(time)
    }


    private fun isGuanKa0Over(): Boolean {
        return (zhanjiang.isFull()
                && niutou.isFull()
                && shahuang.isFull()
                && gongjiang.isFull()
                && saman.isFull()
                && nvwang.isFull()
                && (
                Zhuangbei.isLongxin() ||
                        Zhuangbei.isQiangxi())
                )
    }

    private fun isGuanKa250Over(): Boolean {
        return Zhuangbei.isLongxin()
    }


    override suspend fun afterHeroClick(heroBean: HeroBean) {
        super.afterHeroClick(heroBean)

    }

    override fun changeHeroWhenNoSpace(heroBean: HeroBean): HeroBean? {
        if (heroBean == zhanjiang) {
            if (saman.isInCar()) {
                return saman
            }
            if (niutou.isInCar()) {
                return niutou
            }
        } else if (heroBean == niutou) {
            if (saman.isInCar()) {
                return saman
            }
        }
        return null
    }
}