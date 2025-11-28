package tasks.anyue.zhanjiang

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tasks.XueLiang
import java.awt.event.KeyEvent
import kotlin.math.abs

class AYWuZhanHeroDoingSimpleBoBack2 : BaseSimpleAnYueHeroDoing() {

    val shuiling = HeroCreator.shuiling.create()
    val tieqi = HeroCreator.tieqi.create()
    val zhanjiang = HeroCreator.zhanjiang.create()
    val tuling = HeroCreator.tuling.create()
    val sishen = HeroCreator.sishen.create()
    val dijing = HeroCreator.dijing.create()
    val huanqiu = HeroCreator.huanqiu.create()
    val shexian = HeroCreator.feiting.create()
    val yuren = HeroCreator.yuren.create()
    val guangqiu = HeroCreator.guangqiu.create()

    override fun initHeroes() {
        heros = arrayListOf(zhanjiang, tieqi, shuiling, yuren, sishen, shexian, tuling, dijing, guangqiu, huanqiu)
        heros39Up4 = arrayListOf(sishen, shuiling, yuren, tuling)

        guanDealList.add(GuanDeal(0, isOver = {
            zhanjiang.isGold()
        }, chooseHero = {
            upAny(zhanjiang)
        }))
//        guanDealList.add(GuanDeal(19, isOver = { false }, chooseHero = {
//            deal19(this)
//        }, onGuanDealStart = {
//            start19Oberserver(true)
//        }))
        guanDealList.add(GuanDeal(9, isOver = {
            fulls(zhanjiang, tieqi, dijing, yuren, shuiling, tuling, shexian) && yandou
        }, chooseHero = {
            if (!tieqi.isInCar()) {
               upAny(tieqi,zhanjiang)
            } else {
                upAny(zhanjiang, yuren, shexian, dijing, tieqi, shuiling, tuling, zhuangbei = { yandou })
            }
        }))


        guanDealList.add(GuanDeal(21, isOver = {
            fulls(zhanjiang, sishen, tieqi, shuiling, yuren, tuling, shexian) && yandou
        }, chooseHero = {
            upAny(zhanjiang, sishen, tieqi, shuiling, yuren, tuling, shexian, zhuangbei = { yandou })
        }, onGuanDealStart = {
            carDoing.downHero(dijing)
        }))

        guanDealList.add(
            GuanDeal(39, isOver = { false },
                chooseHero = {
                    deal39(this)
                })
        )

        guanDealList.add(GuanDeal(40, isOver = {
            fulls(zhanjiang, sishen, tieqi, shuiling, yuren, tuling, shexian)
        }, chooseHero = {
            upAny(zhanjiang, sishen, tieqi, shuiling, yuren, tuling, shexian)
        }
        ))
        guanDealList.add(GuanDeal(51, isOver = {
            fulls(zhanjiang, tieqi, shuiling, yuren, tuling, sishen, shexian) && longxin
        }, chooseHero = {
            upAny(zhanjiang, sishen, tieqi, shuiling, yuren, tuling, shexian, zhuangbei = { longxin })
        }))
//        guanDealList.add(GuanDeal(70, isOver = {
//            fulls(zhanjiang,tieqi, sishen, dijing, yuren, tuling, shexian)
//        }, chooseHero = {
//            upAny(tieqi,dijing)
//        }, onGuanDealStart = {
//            carDoing.downHero(shuiling)
//        }))
//        guanDealList.add(GuanDeal(79, isOver = {
//            tuling.isInCar()
//        }, chooseHero = {
//            upAny(tuling)
//        }, onGuanDealStart = {
//            carDoing.downHero(tuling)
//        }))
//        guanDealList.add(GuanDeal(80, isOver = {
//            fulls(tuling)
//        }, chooseHero = {
//            upAny(tuling)
//        }))
//        guanDealList.add(GuanDeal(89, isOver = {
//            tuling.isInCar()
//        }, chooseHero = {
//            upAny(tuling)
//        }, onGuanDealStart = {
//            carDoing.downHero(tuling)
//        }))
//        guanDealList.add(GuanDeal(90, isOver = {
//            fulls(zhanjiang,tieqi, sishen, shuiling, yuren, tuling, shexian)
//        }, chooseHero = {
//            upAny(tieqi,shuiling)
//        }, onGuanDealStart = {
//            carDoing.downHero(dijing)
//        }))
//        guanDealList.add(GuanDeal(99, isOver = {
//            tieqi.isInCar()
//        }, chooseHero = {
//            upAny(tieqi)
//        }, onGuanDealStart = {
//            carDoing.downHero(tieqi)
//        }))



        guanDealList.add(GuanDeal(100, isOver = {
            fulls(shuiling,tieqi) && shengjian
        }, chooseHero = {
            carDoing.downHero(dijing)
            upAny(tieqi,shuiling,zhuangbei = { shengjian })
        }))

        guanDealList.add(GuanDeal(111, onlyDoSomething = {
            carDoing.downHero(shexian)
        }).apply { des = "下射线" })


        guanDealList.add(GuanDeal(120, isOver = {
            fulls(shexian) && longxin
        }, chooseHero = {
            upAny(shexian, zhuangbei = {longxin})
        }
        ))

        guanDealList.add(GuanDeal(129, isOver = { currentGuan() > 129 }, chooseHero = { g129Index(this) }
            , onGuanDealStart = {
                g129State = 0
                GlobalScope.launch {
                    check129Xue()
                }
            })
            .apply { des = "按0下射线，再按0上射线" })

        guanDealList.add(
            GuanDeal(
                130,
                isOver = { shexian.isFull() },
                chooseHero = {
                    upAny(shexian)
                })
        )


        guanDealList.add(GuanDeal(179, isOver = {
            currentGuan()>179
        }, chooseHero = {
            this.deal179()
        }
        ).apply {
            des= "按0 回规初始（下土灵），按1-7对应处理1-7的牌,大王按8（其实等于按0，初始态吃王），其他牌按9打死，按7时要等球快撞上再按，上太早会打死，无法释放冰球"
        }
        )

//        addGuanDeal(180){
//            over {
//                tuling.currentLevel==2 &&
//            }
//        }


        curGuanDeal = guanDealList.get(0)
    }

    /**
     * //179  0初始态（其他都满，下土灵得状态，到boss就按0）。1，杀敌状态，不要的牌都按1（杀死后按0回初始态）。
     *     //2 特殊态5，下 射线土灵，鱼人（遇到牌5按2，撞车后按0回初始态）
     *     //3  特殊态7，冰球触发后按7上土灵，下一轮前按0回初始态。
     */
    var state179 = 0

    //179  0初始态（其他都满，下土灵得状态，到boss就按0）。1，杀敌状态，不要的牌都按1（杀死后按0回初始态）。
    //2 特殊态5，下 射线土灵，鱼人（遇到牌5按2，撞车后按0回初始态）
    //3  特殊态7，冰球触发后按7上土灵，下一轮前按0回初始态。
    suspend fun List<HeroBean?>.deal179():Int{
        if(state179==0){
            carDoing.downHero(tuling)
            if(!shexian.isFull() || !yuren.isFull()){
                return upAny(shexian,yuren)
            }
        }
        while (state179==0){
            delay(200)
        }
        if(state179==1){
            while(tuling.isInCar() && state179==1){
                delay(200)
            }
            if(!tuling.isInCar()){
                return this.indexOf(tuling)
            }
        }


        return -1
    }


    private fun currentGuan(): Int {
        return guankaTask?.currentGuanIndex ?: 0
    }

    var g149Type = -1;  //0 石柱，切强袭，死神换狂将； 1 火灵boss，狂将切咕咕，切回 满咕咕尽量；2是月亮boss，暂时打不过


    var g139State = 0

    var g129State = 0//0等待,1 下宝库 备宝库，2上宝库 //回到了初始态，等1再下宝库循环
    var g129XueCount = 1//0,1 下射线，2，3上射线
    suspend fun check129Xue() {

        while (curGuan <= 129) {
            XueLiang.observerXueDown(xueRate = 0.1f, over = { curGuan > 129 })
            g129XueCount++
            if (g129XueCount == 2) {
                delay(500)
                g129XueCount = 0
                g129State = if (g129State == 0) 1 else 0
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
                carDoing.downHero(shexian)
                var index = heros.indexOf(shexian)
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

    var puked179 = arrayListOf<Int>()

    fun addPuke(puke: Int){

        var add = false

        if(puked179.size==0){
            puked179.add(puke)
            add = true
        }else if(puked179.size==1){
            val one = puked179.get(0)
            if(puke!=one && abs(puke-one)<5){
                puked179.add(puke)
                add = true
            }
        }else{
            var min = puked179.minOrNull()!!
            var max = puked179.maxOrNull()!!

            if(!puked179.contains(puke)
                && ((puke>min && puke-min<5) || (puke<max && max-puke<5)          )
            ){
                puked179.add(puke)
                add = true
            }


        }

        if(!add){
            state179 = 1
        }
    }

    override suspend fun onKeyDown(code: Int): Boolean {

        if(currentGuan()== 179 || currentGuan() == 178){
            when(code){
                KeyEvent.VK_NUMPAD0->{
                    state179 = 0
                }
                KeyEvent.VK_NUMPAD1->{
                    addPuke(1)
                }
                KeyEvent.VK_NUMPAD2->{
                    addPuke(2)
                }
                KeyEvent.VK_NUMPAD3->{
                    addPuke(3)
                }
                KeyEvent.VK_NUMPAD4->{
                    addPuke(4)
                }
                KeyEvent.VK_NUMPAD5->{
                    state179 = 1
                }
                KeyEvent.VK_NUMPAD6->{
                    addPuke(6)
                }
                KeyEvent.VK_NUMPAD7->{
                    addPuke(7)
                }
                KeyEvent.VK_NUMPAD8->{//8是大王，吃了就行，不用addpuke。pk只计算最大最小不重复就行
                    state179 = 1
                }
                KeyEvent.VK_NUMPAD9->{ //按9就杀
                    state179 = 1
                }

            }


            return true
        }


        if (guankaTask?.currentGuanIndex == 129 || guankaTask?.currentGuanIndex == 128
        ) {//按0 下射线，备射线，再按0，上射线
            when (code) {
                KeyEvent.VK_NUMPAD0 -> {
                    g129State = if (g129State == 0) 1 else 0
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