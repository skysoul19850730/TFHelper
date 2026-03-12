package tasks.daxuanwo

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.delay
import tasks.XueLiang
import java.awt.event.KeyEvent

class XWZJHeroDoingDaiRen3 : BaseSimpleXWHeroDoing() {
    
    //副卡：天使，咕咕，小野，闪，巫医,死神（中间放 闪 天使）  射线  幻  魔  光， 马车融吸血或生命
    
    val dianfa = HeroCreator.dianfa.create()
    val jiaonv = HeroCreator.jiaonv.create()
    val yuren = HeroCreator.yuren.create()
    val feiting = HeroCreator.feiting.create()
    val tianshi = HeroCreator.tianshi.create()
    val niutou = HeroCreator.niutou.create()
    
    
    val bingqi = HeroCreator.bingqi.create()
    val haiyao = HeroCreator.haiyao.create()
    
    
    val wangjiang = HeroCreator.wangjiang.create()
    val guangqiu = HeroCreator.guangqiu.create()

    override fun initHeroes() {
        super.initHeroes()
        auto59 = true

        heros = arrayListOf(dianfa,jiaonv,yuren,feiting,tianshi,niutou,wangjiang,haiyao,guangqiu,bingqi)
        addGuanDeal(0){
            over {
                fulls(jiaonv,tianshi,feiting,niutou,dianfa,haiyao,yuren)
            }
            chooseHero {
                val list = listOf(jiaonv,dianfa,haiyao,niutou)
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
                    upAny(listOf(feiting,tianshi,yuren,jiaonv,dianfa,haiyao,niutou))
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


        add49(feiting)

        add50(listOf(), listOf(tianshi,yuren), onlySetMid = true)

        addGuanDealWithHerosFull(52, listOf(bingqi), listOf(haiyao))

        add69(auto = true)

        addGuanDealWithHerosFull(70, listOf(wangjiang), listOf(bingqi))
        
        curGuanDeal = guanDealList.get(0)
    }

    override suspend fun onKeyDown(code: Int): Boolean {

        return super.onKeyDown(code)
    }
}