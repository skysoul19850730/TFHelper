package tasks.hanbing2.zhanjiang

import data.HeroBean
import data.HeroCreator
import kotlinx.coroutines.delay
import tasks.hanbing2.BaseSimpleHBHeroDoing
import java.awt.event.KeyEvent

class HB5ZHeroDoingBo2 : BaseSimpleHBHeroDoing() {


    val zhanjiang = HeroCreator.zhanjiang2.create()
    val tieqi = HeroCreator.tieqi.create()
    val saman = HeroCreator.saman2.create()
    val yuren = HeroCreator.yuren.create()
    val sishen = HeroCreator.sishen2.create()
    val haiyao = HeroCreator.haiyao.create()
    val huanqiu = HeroCreator.huanqiu.create()
    val dianfa = HeroCreator.dianfa.create()
    val baoku = HeroCreator.shexian.create()
    val guangqiu = HeroCreator.guangqiu.create()

    override fun initHeroes() {
        super.initHeroes()
        heros = arrayListOf(zhanjiang, tieqi, saman, yuren, sishen, haiyao, huanqiu, dianfa, baoku, guangqiu)
        heroDoNotDown129 = zhanjiang

        guanDealList.add(GuanDeal(0, isOver = {
            fullBase()
        }, chooseHero = {
            if (zhanjiang.isGold()) {
                upAny(zhanjiang, haiyao, dianfa, tieqi, saman, sishen, baoku)
            } else {
                upAny(zhanjiang)
            }
        }))

        guanDealList.add(GuanDeal(19, isOver = {
            fullBase() && longxin
        }, chooseHero = {
            carDoing.downHero(haiyao)
            upBase(zhuangbei = { longxin })
        }))

        guanDealList.add(GuanDeal(91, isOver = { haiyao.isFull() }, chooseHero = {
            carDoing.downHero(yuren)
            upAny(haiyao)
        }))

        guanDealList.add(GuanDeal(99, isOver = {
            false
        }, chooseHero = {
            while (zs99Clicked == 0 && currentGuan() < 100) {
                delay(200)
            }
            if (zs99Clicked == 1) {
                carDoing.downHero(tieqi)
                delay(10000)
                zs99Clicked = 2
            }
            if (zs99Clicked == 2) {
                upAny(tieqi)
            } else -1

        }))

        guanDealList.add(GuanDeal(100, isOver = {
            fullBase() && yandou
        }, chooseHero = {
            carDoing.downHero(haiyao)
            upBase(zhuangbei = { yandou })
        }))


        guanDealList.add(
            GuanDeal(109, isOver = { false },
                chooseHero = {
                    delay(300)
                    val ind = upBase()
                    if (ind < 0) {
                        upAny(guangqiu)
                    } else ind
                })
        )

        guanDealList.add(GuanDeal(111, isOver = {
            fullBase() && qiangxi
        }, chooseHero = {
            if (needReCheckStar) {
                carDoing.reCheckStars()
                needReCheckStar = false
            }
            upBase(zhuangbei = { qiangxi })
        }, onGuanDealStart = {
            needReCheckStar = true
        }))

        guanDealList.add(
            GuanDeal(129, onlyDoSomething = {
                startChuanZhangOberserver()
            })
        )
        guanDealList.add(GuanDeal(130, isOver = {
            fullBase() && yandou
        }, chooseHero = {
            upBase { yandou }
        }, onGuanDealStart = { stopChuanZhangOberserver() }))

        changeZhuangbei(150) { qiangxi }


        guanDealList.add(
            GuanDeal(189, isOver = { currentGuan() > 189 },
                chooseHero = {
//                delay(300)
//                val ind =upBase()
//                if(ind<0){
//                    upAny(guangqiu)
//                }else ind
                    checkHeroStarAndFull(this) { currentGuan() > 189 }
                })
        )

        guanDealList.add(GuanDeal(191, isOver = {
            fullBase() && longxin
        }, chooseHero = {
            if (needReCheckStar) {
                carDoing.reCheckStars()
                needReCheckStar = false
            }
            upBase() { longxin }
        }, onGuanDealStart = {
            needReCheckStar = true
        }))


        guanDealList.add(GuanDeal(
            199,
            isOver = {
                false
            },
            chooseHero = {
                deal199(this)
            },
        ).apply {
            des =
                "白球撞上后 按3 进入监听点名，期间可以按数字键盘进行下卡，点名结束后，可以按3重新进入白球卡阶段（下萨满补到3星）"
        })

        guanDealList.add(GuanDeal(
            startGuan = 209,
            isOver = {
                false
            },
            chooseHero = {
               deal210(this)
            },
        ).apply
         {
             des = "哪里被标记为黑洞就点哪里"
         })

        curGuanDeal = guanDealList.first()
    }

    private var step199 = 1  // 1打白球阶段， 2点名阶段，
    private var count199 = 0

    private suspend fun deal199(heros: List<HeroBean?>): Int {
        while (step199 == 0) {
            delay(200)
        }
        if (step199 == 1) {
            if(tieqi.isFull()){
                carDoing.downHero(tieqi)
            }
            if (saman.isFull()) {
                carDoing.downHero(saman)
            }

            if (saman.currentLevel < 3 && tieqi.currentLevel<3) {
                return heros.upAny(saman,tieqi)
            }else if(saman.currentLevel<3){
                return heros.upAny(saman, useGuang = false)
            }else if(tieqi.currentLevel<3){
                return heros.upAny(tieqi, useGuang = false)
            }
            else {
                //等点名
//                    XueLiang.observerXueDown()//掉血 等于 白球撞上了
//                    step199 = 2
//                    delay(300)//怕不同步，延迟300，满上萨满

                //按3 会改成2，不监听掉血了，怕处于涨血状态判断不准。白球撞完按3
                while (step199 == 1) {
                    delay(100)
                }

                return heros.upAny(saman,tieqi)
            }
        } else if (step199 == 2) {

            var dianmingIndex = carDoing.getHB199Selected()
            if (position199 > -1 || dianmingIndex > -1) {
                carDoing.downPosition(position199)
                carDoing.downPosition(dianmingIndex)
                count199++
                position199 = -1
            }
            if (carDoing.hasAllOpenSpace() || carDoing.hasNotFull()) {
                return heros.upAny(zhanjiang, dianfa, sishen, tieqi, yuren, saman, baoku)
            } else {
                while (step199 == 2) {
                    var dianmingIndex = carDoing.getHB199Selected()
                    if (position199 > -1 || dianmingIndex > -1) {
                        carDoing.downPosition(position199)
                        carDoing.downPosition(dianmingIndex)
                        position199 = -1
                        count199++
                        return heros.upAny(zhanjiang, dianfa, sishen, tieqi, yuren, saman, baoku)
                    }
                    delay(100)
                }
            }

        }


        return -1
    }

    var click210Pos = -1
    var lastClick210Pos = -1
    var upList210 = arrayListOf(zhanjiang,sishen,tieqi,yuren,saman, dianfa)

    private suspend fun deal210(heros: List<HeroBean?>): Int {

        if(!upList210.all {
            it.isFull()
            }){
            return heros.upAny(*upList210.toTypedArray())
        }

        while(click210Pos==-1 || lastClick210Pos==click210Pos){
            delay(200)
        }
        val carPos = carDoing.carps.get(click210Pos)
        val hero = carPos.mHeroBean
        if(hero!=null){
            carPos.isUnEnable = true
           if(hero==zhanjiang){//还要辅助副卡战将，所以还得上来，但加到鱼人后面
               carDoing.resetHero(zhanjiang)
                upList210.remove(zhanjiang)
               upList210.add(3,zhanjiang)
           }

            val lastH = upList210.last()
            carDoing.downHero(lastH)
            upList210.remove(lastH)
            lastClick210Pos = click210Pos
            return heros.upAny(*upList210.toTypedArray())
        }else{
            lastClick210Pos = click210Pos
        }
        return -1

    }


    var zs99Clicked = 0
    override suspend fun onKeyDown(code: Int): Boolean {
        if (guankaTask?.currentGuanIndex == 98 || guankaTask?.currentGuanIndex == 99) {
            if (code == KeyEvent.VK_NUMPAD3) {
                zs99Clicked = 1
                return true
            }
        }
        if (code == KeyEvent.VK_NUMPAD3) {//9强制改变waitting，防止waiting有逻辑错误不上卡
            if (guankaTask?.currentGuanIndex == 199 || guankaTask?.currentGuanIndex == 198) {

                step199 = if (step199 == 1) 2 else 1

                return true
            }
        }

        if (guankaTask?.currentGuanIndex == 209 ||guankaTask?.currentGuanIndex == 208 ) {
            click210Pos = when (code) {
                KeyEvent.VK_NUMPAD2 -> 0
                KeyEvent.VK_NUMPAD1 -> 1
                KeyEvent.VK_NUMPAD5 -> 2
                KeyEvent.VK_NUMPAD4 -> 3
                KeyEvent.VK_NUMPAD8 -> 4
                KeyEvent.VK_NUMPAD7 -> 5
                KeyEvent.VK_NUMPAD0 -> 6
                else -> -1
            }

            if(click210Pos>-1){
                return true
            }
        }


        return super.onKeyDown(code)
    }


    private fun currentGuan(): Int {
        return guankaTask?.currentGuanIndex ?: 0
    }


    fun fullBase(): Boolean {
        return fulls(zhanjiang, tieqi, saman, dianfa, sishen, yuren, baoku)
    }

    fun List<HeroBean?>.upBase(zhuangbei: (() -> Boolean)? = null): Int {
        return upAny(zhanjiang, tieqi, saman, sishen, dianfa, yuren, baoku, zhuangbei = zhuangbei)
    }

    private suspend fun checkHeroStarAndFull(heros: List<HeroBean?>, over: () -> Boolean): Int {
        if (carDoing.hasOpenSpace() || carDoing.hasNotFull()) {
            return heros.upBase()
        }
        while (!over.invoke()) {
            delay(500)
            carDoing.reCheckStars()

            if (carDoing.hasOpenSpace() || carDoing.hasNotFull()) {
                return heros.upBase()
            }
        }
        return -1
    }


    override fun onChuanzhangClick(position: Int) {
    }

}