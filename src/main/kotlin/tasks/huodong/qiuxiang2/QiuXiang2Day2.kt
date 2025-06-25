package tasks.huodong.qiuxiang2

import data.HeroCreator

class QiuXiang2Day2 : BaseQiuxiang2() {
    val dianfa = HeroCreator.dianfa.create()
    val mengyan = HeroCreator.mengyan.create()
    val xiongmao = HeroCreator.xiongmao.create()
    val kui = HeroCreator.kui.create()
    val bingqi = HeroCreator.bingqi.create()
    val maomi = HeroCreator.maomi.create()
    val ganglie = HeroCreator.ganglie.create()
    val moqiu = HeroCreator.moqiu.create()
    val nvyao = HeroCreator.nvyao.create()
    val baoku = HeroCreator.baoku.create()




    override fun initHeroes() {
        heros = arrayListOf(dianfa, xiongmao, maomi, baoku, nvyao, ganglie, kui, bingqi, mengyan, moqiu)

        guanDealList.add(GuanDeal(
            startGuan = 0,
            isOver = {
                fulls(mengyan,dianfa,nvyao,ganglie,kui,baoku)
            },
            chooseHero = {
                upAny(mengyan,dianfa,nvyao,ganglie,kui,baoku)
            }
        ))
        guanDealList.add(GuanDeal(
            startGuan = 50,
            isOver = {
                fulls(maomi)
            },
            chooseHero = {
                upAny(maomi)
            }
        ))

        guanDealList.add(GuanDeal(
            startGuan = 80,
            isOver = {
                fulls(xiongmao)
            },
            chooseHero = {
                carDoing.downHero(nvyao)
                upAny(xiongmao)
            }
        ))
        guanDealList.add(GuanDeal(
            startGuan = 91,
            onlyDoSomething = {
                carDoing.downHero(baoku)
            }
        ))

        guanDealList.add(GuanDeal(
            startGuan = 101,
            isOver = {
                fulls(baoku)
            },
            chooseHero = {
                upAny(baoku)
            }
        ))

        gudingShuaQiuTask("moqiu",149,5000,null,150)

        curGuanDeal = guanDealList.get(0)
    }


}