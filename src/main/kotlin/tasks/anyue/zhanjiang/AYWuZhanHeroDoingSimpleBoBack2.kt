package tasks.anyue.zhanjiang

import MainData
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
    val zhanjiang = HeroCreator.zhanjiang2.create()
    val tuling = HeroCreator.tuling.create()
    val sishen = HeroCreator.sishen2.create()
    val dijing = HeroCreator.dijing.create()
    val huanqiu = HeroCreator.huanqiu.create()
    val shexian = HeroCreator.feiting.create()
    val kuangjiang = HeroCreator.kuangjiang.create()
    val houzi3 = HeroCreator.houzi3.create()

    override fun initHeroes() {
        heros = arrayListOf(zhanjiang, tieqi, shuiling, kuangjiang, sishen, shexian, tuling, dijing, houzi3, huanqiu)
        heros39Up4 = arrayListOf(sishen, shuiling, kuangjiang, tuling)

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
            fulls(zhanjiang, tieqi, dijing, shuiling, tuling, shexian, kuangjiang) && yandou
        }, chooseHero = {
            if (!tieqi.isInCar()) {
                this.indexOf(tieqi)
            } else {
                upAny(zhanjiang, shexian, dijing, tieqi, shuiling, tuling, kuangjiang, zhuangbei = { yandou })
            }
        }))


        guanDealList.add(GuanDeal(21, isOver = {
            fulls(zhanjiang, sishen, tieqi, shuiling, kuangjiang, tuling, shexian) && yandou
        }, chooseHero = {
            upAny(zhanjiang, sishen, tieqi, shuiling, kuangjiang, tuling, shexian, zhuangbei = { yandou })
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
            fulls(zhanjiang, sishen, tieqi, shuiling, kuangjiang, tuling, shexian)
        }, chooseHero = {
            upAny(zhanjiang, sishen, tieqi, shuiling, kuangjiang, tuling, shexian)
        }
        ))
        guanDealList.add(GuanDeal(51, isOver = {
            fulls(zhanjiang, tieqi, shuiling, kuangjiang, tuling, sishen, shexian) && longxin
        }, chooseHero = {
            upAny(zhanjiang, sishen, tieqi, shuiling, kuangjiang, tuling, shexian, zhuangbei = { longxin })
        }))
        guanDealList.add(GuanDeal(70, isOver = {
            fulls(zhanjiang,tieqi, sishen, dijing, kuangjiang, tuling, shexian)
        }, chooseHero = {
            upAny(tieqi,dijing)
        }, onGuanDealStart = {
            carDoing.downHero(shuiling)
        }))
        guanDealList.add(GuanDeal(79, isOver = {
            tieqi.isInCar()
        }, chooseHero = {
            upAny(tieqi)
        }, onGuanDealStart = {
            carDoing.downHero(tieqi)
        }))
        guanDealList.add(GuanDeal(80, isOver = {
            fulls(zhanjiang,tieqi, sishen, dijing, kuangjiang, tuling, shexian)
        }, chooseHero = {
            upAny(tieqi,dijing)
        }, onGuanDealStart = {
            carDoing.downHero(shuiling)
        }))
        guanDealList.add(GuanDeal(89, isOver = {
            tieqi.isInCar()
        }, chooseHero = {
            upAny(tieqi)
        }, onGuanDealStart = {
            carDoing.downHero(tieqi)
        }))
        guanDealList.add(GuanDeal(90, isOver = {
            fulls(zhanjiang,tieqi, sishen, dijing, kuangjiang, tuling, shexian)
        }, chooseHero = {
            upAny(tieqi,dijing)
        }, onGuanDealStart = {
            carDoing.downHero(shuiling)
        }))
        guanDealList.add(GuanDeal(99, isOver = {
            tieqi.isInCar()
        }, chooseHero = {
            upAny(tieqi)
        }, onGuanDealStart = {
            carDoing.downHero(tieqi)
        }))



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



        addGuanDeal(169){
            over { 
                houzi3.isFull()
            }
            chooseHero { 
                var index = indexOf(houzi3)
                if(index>-1){
                    while(g169State==0){
                        delay(100)
                    }
                }
                carDoing.downHero(kuangjiang)
                index
            }
            des = "按0 鱼人切猴子，在第9个球后 切"
        }
        

        guanDealList.add(GuanDeal(179, isOver = {
            currentGuan()>179
        }, chooseHero = {
            this.deal179()
        }
        ).apply {
            des= "按0 回规初始（下土灵），按1-7对应处理1-7的牌,大王啥也不按，大于7的牌按9打死，按7时要等球快撞上再按，上太早会打死，无法释放冰球"
        }
        )

        
        addGuanDeal(189){
            over { curGuan>189 }
            chooseHero { 
                if(state189==0){
                    var index = indexOf(kuangjiang)
                    if(index>-1) {
                        XueLiang.observerXueDown(xueRate = 0.1f, over = { curGuan > 189 })
                        carDoing.downHero(houzi3)
                        state189 = 1
                        index
                    }else index
                }else{
                    var index = indexOf(houzi3)
                    if(index>-1) {
                        while (state189 == 1) {
                            delay(100)
                        }
                        carDoing.downHero(kuangjiang)
                        index
                    }else index
                }
            }
            des = "打死牌后按0切猴子，掉血后会自动切狂将，所以如果这个不需要打死，也按0切回猴子就行，副卡会打冰这里"
        }
        
        addGuanDeal(190){
            over { 
                houzi3.isFull()
            }
            chooseHero { 
                carDoing.downHero(kuangjiang)
                upAny(houzi3)
            }
        }

        curGuanDeal = guanDealList.get(0)
    }

    /**
     * 掉血后，改1，切狂将输出，手按0，切猴子抗伤害
     */
    var state189 = 0
    
    /**
     * //179  0初始态（其他都满，下土灵得状态，到boss就按0）。1，杀敌状态，不要的牌都按1（杀死后按0回初始态）。
     *     //2 特殊态5，下 射线土灵，鱼人（遇到牌5按2，撞车后按0回初始态）
     *     //3  特殊态7，冰球触发后按7上土灵，下一轮前按0回初始态。
     */
    var state179 = 0

    //179  0初始态（其他都满，下土灵得状态，到boss就按0）。1，杀敌状态，不要的牌都按1（杀死后按0回初始态）。
    //3  特殊态7，冰球触发后按7上土灵，下一轮前按0回初始态。
    suspend fun List<HeroBean?>.deal179():Int{
        if(state179==0){
            carDoing.downHero(tuling)
            if(!shexian.isFull() || !houzi3.isFull()){
                return upAny(shexian,houzi3)
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


    var g169State = 0
    var g139State = 0

    var g129State = 0//0等待,1 下宝库 备宝库，2上宝库 //回到了初始态，等1再下宝库循环

    var g129XueCount = 1//0,1 下射线，2，3上射线
    suspend fun check129Xue() {

        while (curGuan <= 129) {
            XueLiang.observerXueDown(xueRate = 0.1f, over = { curGuan > 129 })
            g129XueCount++
            if (g129XueCount == 2) {
                delay(1500)
                g129XueCount = 0
                g129State = if (g129State == 0) 1 else 0
            }
            delay(2000)
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

        if (heroBean == houzi3 && (guankaTask?.currentGuanIndex == 139 || guankaTask?.currentGuanIndex == 138
                    || guankaTask?.currentGuanIndex == 149 || guankaTask?.currentGuanIndex == 148
                    || guankaTask?.currentGuanIndex == 159 || guankaTask?.currentGuanIndex == 158
                    )
        ) {
            delay(500)
            carDoing.downHero(houzi3)
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
        if (guankaTask?.currentGuanIndex == 189 || guankaTask?.currentGuanIndex == 188
        ) {//按0 下射线，备射线，再按0，上射线
            when (code) {
                KeyEvent.VK_NUMPAD0 -> {
                    state189 = 0
                }
            }

            return true
        }
        if (guankaTask?.currentGuanIndex == 169 || guankaTask?.currentGuanIndex == 168
        ) {//按0 下射线，备射线，再按0，上射线
            when (code) {
                KeyEvent.VK_NUMPAD0 -> {
                   g169State = 1
                }
            }

            return true
        }
        

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
                KeyEvent.VK_NUMPAD5->{//5不要，下卡太多上不回去可能
                    state179 = 1
                }
                KeyEvent.VK_NUMPAD6->{
                    addPuke(6)
                }
                KeyEvent.VK_NUMPAD7->{
                    addPuke(7)
                }
                KeyEvent.VK_NUMPAD8->{//8 攻击时猴子切鱼人
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