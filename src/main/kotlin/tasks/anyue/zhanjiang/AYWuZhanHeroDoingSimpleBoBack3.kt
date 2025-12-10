package tasks.anyue.zhanjiang

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import log
import tasks.XueLiang
import tasks.anyue.base.An69
import java.awt.event.KeyEvent

class AYWuZhanHeroDoingSimpleBoBack3 : BaseSimpleAnYueHeroDoing() {

    val tieqi = HeroCreator.tieqi.create()
    val zhanjiang = HeroCreator.zhanjiang.create()
    val tuling = HeroCreator.tuling.create()
    val sishen = HeroCreator.sishen.create()
    val feiting = HeroCreator.feiting.create()
    val tianshi = HeroCreator.tianshi.create()
    val guangqiu = HeroCreator.guangqiu.create()
    val niutou = HeroCreator.niutou.create()
    val huanqiu = HeroCreator.huanqiu.create()
    val jiaonv = HeroCreator.jiaonv.create()

    override fun initHeroes() {
        heros = arrayListOf(zhanjiang, tieqi, tianshi, jiaonv, sishen, feiting, tuling, niutou, guangqiu, huanqiu)

        addGuanDeal(0) {
            over {
                fulls(zhanjiang, sishen, jiaonv, niutou, feiting)
            }
            chooseHero {
                if (zhanjiang.isInCar()) {
                    upAny(zhanjiang, jiaonv, niutou, sishen, feiting)
                } else {
                    upAny(zhanjiang)
                }
            }
        }

        addGuanDealWithHerosFull(25, listOf(tieqi))

        changeZhuangbei(30) { qiangxi }

        addGuanDealWithHerosFull(38, listOf(tianshi), delay = 2000)


        guanDealList.add(
            GuanDeal(39, isOver = { false },
                chooseHero = {
                    deal39(this)
                }, onGuanDealStart = {
                    heros39Up4.clear()
                    heros39Up4.addAll(carDoing.carps.subList(2, 5).map { it.mHeroBean!! })
                })
        )
        addGuanDealWithHerosFull(
            40,
            listOf(zhanjiang, tieqi, sishen, niutou, jiaonv, tuling, feiting),
            downHeros = listOf(tianshi),
            { yandou })

        changeZhuangbei(50) { qiangxi }

        An69(this, listOf(huanqiu)).addToHeroDoing()
        addGuanDealWithHerosFull(70, listOf(tianshi), listOf(niutou))


        guanDealList.add(GuanDeal(111, onlyDoSomething = {
            carDoing.downHero(feiting)
        }).apply { des = "下射线" })


        guanDealList.add(GuanDeal(120, isOver = {
            fulls(feiting) && longxin
        }, chooseHero = {
            upAny(feiting, zhuangbei = { longxin })
        }
        ))

        guanDealList.add(GuanDeal(
            129,
            isOver = { currentGuan() > 129 },
            chooseHero = { g129Index(this) },
            onGuanDealStart = {
                g129State = 0
                time=System.currentTimeMillis()
                if(carDoing.chePosition == 1){
                    //后车的话，黄蜂出来2.5秒后下，但不识别黄凤，就从129开始后
                    //掉血一次后，count满2，g129state会变回0再上射线。后面就一样了
                    //如果是前车就不用管，就等第一次掉血就可以
                    GlobalScope.launch {
                        delay(19000)
                        g129State = 1//下掉
                    }

                }
                GlobalScope.launch {
                    check129Xue()
                }
            })
            .apply { des = "按0下射线，再按0上射线,按3获取第一个旋风得时间" })
        //19232秒，含2.5，接近3了

        guanDealList.add(
            GuanDeal(
                130,
                isOver = { feiting.isFull() },
                chooseHero = {
                    upAny(feiting)
                })
        )

        addGuanDeal(141){
            onlyDo {
                carDoing.downHero(feiting)
            }
        }

        addGuanDealWithHerosFull(150, listOf(niutou,feiting), listOf(tuling))

        guanDealList.add(GuanDeal(179, isOver = {
            curGuan > 179
        }, chooseHero = {
            this.deal179()
        }, onGuanDealStart = {
            carDoing.downHero(tianshi)
        }
        ).apply {
            des =
                "按0 满天使"
        }
        )

        addGuanDeal(180) {
            over {
                jiaonv.currentLevel == 3
            }
            chooseHero {
                upAny(jiaonv)
            }
            onStart {
                carDoing.downHero(feiting)
                carDoing.downHero(jiaonv)
            }
        }
//        addGuanDeal(180){
//            over {
//                tuling.currentLevel==2 &&
//            }
//        }


        curGuanDeal = guanDealList.get(0)
    }
    var time = 0L

    /**
     * //179  0初始态（其他都满，下土灵得状态，到boss就按0）。1，杀敌状态，不要的牌都按1（杀死后按0回初始态）。
     *     //2 特殊态5，下 射线土灵，鱼人（遇到牌5按2，撞车后按0回初始态）
     *     //3  特殊态7，冰球触发后按7上土灵，下一轮前按0回初始态。
     */
    var state179 = 0

    //179  0初始态（其他都满，下土灵得状态，到boss就按0）。1，杀敌状态，不要的牌都按1（杀死后按0回初始态）。
    //2 特殊态5，下 射线土灵，鱼人（遇到牌5按2，撞车后按0回初始态）
    //3  特殊态7，冰球触发后按7上土灵，下一轮前按0回初始态。
    suspend fun List<HeroBean?>.deal179(): Int {
        if (tianshi.currentLevel == 3) {
            while (state179 == 0 && curGuan < 180) {
                delay(200)
            }

        }
        return indexOf(tianshi)
    }


    private fun currentGuan(): Int {
        return guankaTask?.currentGuanIndex ?: 0
    }

    var g149Type = -1;  //0 石柱，切强袭，死神换狂将； 1 火灵boss，狂将切咕咕，切回 满咕咕尽量；2是月亮boss，暂时打不过


    var g139State = 0

    val g129FirstState:Int //右车 1 (先下射线）  左车0（先不下）
        get() = if(chePosition==1) 1 else 0
    var g129State = 0//0等待,1 下宝库 备宝库，2上宝库 //回到了初始态，等1再下宝库循环
    var g129XueCount = 1//0,1 下射线，2，3上射线
    var checkXue = true
    suspend fun check129Xue() {

        while (curGuan <= 129 && checkXue) {
            XueLiang.observerXueDown(xueRate = 0.1f, over = { curGuan > 129 })
            g129XueCount++
            if (g129XueCount == 2) {
                delay(700)
                g129XueCount = 0
                if (checkXue) {
                    g129State = if (g129State == 0) 1 else 0
                }
            }
            delay(1500)
        }

    }

    suspend fun g129Index(heros: List<HeroBean?>): Int {
        if (currentGuan() > 129) return -1
        while (g129State == 0) {
            delay(100)
            if (currentGuan() > 129) return -1
        }
        when (g129State) {
            1 -> {
                carDoing.downHero(feiting)
                var index = heros.indexOf(feiting)
                if (index > -1) {
                    while (g129State == 1) {
                        delay(100)
                        if (currentGuan() > 129) return -1
                    }
                    return index
                }
                return -1
            }

        }
        return -1
    }

    override suspend fun doAfterHeroBeforeWaiting(heroBean: HeroBean) {

        if (heroBean == guangqiu && (guankaTask?.currentGuanIndex == 139 || guankaTask?.currentGuanIndex == 138
                    || guankaTask?.currentGuanIndex == 149 || guankaTask?.currentGuanIndex == 148
                    || guankaTask?.currentGuanIndex == 159 || guankaTask?.currentGuanIndex == 158
                    )
        ) {
            delay(500)
            carDoing.downHero(guangqiu)
        }

        super.doAfterHeroBeforeWaiting(heroBean)
    }

    override suspend fun onKeyDown(code: Int): Boolean {

        if (curGuan == 179) {
            when (code) {
                KeyEvent.VK_NUMPAD0 -> {
                    state179 = 1
                }
            }
            return true
        }


        if (guankaTask?.currentGuanIndex == 129 || guankaTask?.currentGuanIndex == 128
        ) {//按0 下射线，备射线，再按0，上射线
            when (code) {
                KeyEvent.VK_NUMPAD0 -> {
                    //如果按键了，就取消看血自动了，避免二者冲突
                    checkXue = false
                    g129State = if (g129State == 0) 1 else 0
                }
                KeyEvent.VK_NUMPAD3->{
                    log("129 旋风第一个 ${System.currentTimeMillis()-time}")
                    if(carDoing.chePosition == 1) {
                        g129State = 1
                    }
                }
            }

            return true
        }

        if (guankaTask?.currentGuanIndex == 139 || guankaTask?.currentGuanIndex == 138
            || guankaTask?.currentGuanIndex == 159 || guankaTask?.currentGuanIndex == 158
        ) {
            if (code == KeyEvent.VK_NUMPAD0) {
                g139State = 1
                return true
            }
        }
        var curGuan = guankaTask?.currentGuanIndex ?: 0
        if (curGuan in 141..149
        ) {
            if (code == KeyEvent.VK_NUMPAD0) {
                g139State = 1
                return true
            } else if (code == KeyEvent.VK_NUMPAD1) {

                g149Type = 0
                return true
            } else if (code == KeyEvent.VK_NUMPAD2) {
                g139State = -1
                g149Type = 1
                return true
            } else if (code == KeyEvent.VK_NUMPAD3) {
                g149Type = 2
                return true
            }
        }



        if (super.onKeyDown(code)) {
            return true
        }
        return doOnKeyDown(code)
    }

    suspend fun doOnKeyDown(code: Int): Boolean {


        return true
    }
}