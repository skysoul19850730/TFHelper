package tasks.daxuanwo

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.delay
import tasks.XueLiang
import java.awt.event.KeyEvent

class XWZJHeroDoingDaiRen3 : BaseSimpleXWHeroDoing() {
    val dianfa = HeroCreator.dianfa.create()
    val jiaonv = HeroCreator.jiaonv.create()
    val yuren = HeroCreator.yuren.create()
    val feiting = HeroCreator.feiting.create()
    val tianshi = HeroCreator.tianshi.create()
    val niutou = HeroCreator.niutou.create()
    val sishen = HeroCreator.sishen.create()
    
    val huanqiu = HeroCreator.huanqiu.create()
    val hunqiu = HeroCreator.hunqiu.create()
    val guangqiu = HeroCreator.guangqiu.create()

    var lastHun = 0L

    private suspend fun backHun(index: Int): Int {
        if (index > -1) {
            if (System.currentTimeMillis() - lastHun > 2000) {
                return index
            } else {
                delay(2000 - (System.currentTimeMillis() - lastHun))
                return index
            }
        }
        return index
    }
    private suspend fun g69(list: List<HeroBean?>, step: Int):Int{
        val index = list.indexOf(hunqiu)
        if(XueLiang.getXueLiang()<0.95){
            return index
        }
        return -1
    }
    override fun initHeroes() {
        super.initHeroes()
        g49StartBoss = {
            var index = it.indexOf(hunqiu)
            backHun(index)
        }

        g69StartBoss = {list,step->
            g69(list,step)
        }
        
        heros = arrayListOf(dianfa,jiaonv,yuren,feiting,tianshi,niutou,huanqiu,hunqiu,guangqiu,sishen)
        addGuanDeal(0){
            over {
                fulls(jiaonv,tianshi,feiting,niutou,dianfa,sishen,yuren)
            }
            chooseHero {
                val list = listOf(jiaonv,dianfa,sishen,niutou)
                val listMid = listOf(tianshi,yuren)
                val listInCar  = list.filter { it.isInCar() }
                val listNoInCar = list.filter { !it.isInCar() }
                if(listInCar.size<2){//先上两个
                    val listToIn = arrayListOf<HeroBean>()
                    if(!feiting.isInCar()) {
                        listToIn.add(feiting)//优先飞艇
                    }
                    listToIn.addAll(listNoInCar)//优先上没上车得，这样可以后续能上中间两个了
                    listToIn.addAll(listInCar)
                    if(feiting.isInCar()) {
                        listToIn.add(feiting)//优先飞艇
                    }
                    upAny(listToIn)
                }else if(listMid.all { it.isInCar() }){
                    upAny(listOf(feiting,tianshi,yuren,jiaonv,dianfa,sishen,niutou))
                }else {
                    val listToIn = arrayListOf<HeroBean>()
                    if(!feiting.isInCar()) {
                        listToIn.add(feiting)//优先飞艇
                    }
                    listToIn.addAll(listMid)//优先上中间
                    listToIn.addAll(listInCar)//前面没有，可以继续上
                    if(feiting.isInCar()) {
                        listToIn.add(feiting)//优先飞艇
                    }
                    upAny(listToIn)
                }
                
            }
        }

        changeZhuangbei(31, {yandou})

        add49WithQiu(feiting,hunqiu,5000)

        add50(listOf(), listOf(tianshi,yuren), onlySetMid = true)

        changeZhuangbei(55, {longxin})
        
        add69(auto = true)
        
        curGuanDeal = guanDealList.get(0)
    }

    override suspend fun onKeyDown(code: Int): Boolean {

        return super.onKeyDown(code)
    }
}