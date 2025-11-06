package tasks.huodong.qiuxiang

import data.HeroBean
import data.HeroCreator

class QiuXiangDay2 : BaseQiuxiang() {
    val tieqi = HeroCreator.tieqi.create()
    val yuren = HeroCreator.yuren.create()
    val kuangjiang = HeroCreator.kuangjiang.create()
    val nvyao = HeroCreator.wugui.create()
    val guangqiu = HeroCreator.gugu.create()
    val yanmo = HeroCreator.yanmo.create()

    val moqiu = HeroCreator.baoku.create()

    val maomi = HeroCreator.guangqiu.create()
    val shexian = HeroCreator.moqiu.create()
    val aishen = HeroCreator.saman.create()

    override var downUpHero: HeroBean = moqiu

    override var upHeros: ArrayList<HeroBean> = arrayListOf(
        tieqi,
        yuren,
        kuangjiang,
        nvyao,
        guangqiu,
        moqiu,
        yanmo
    )

    override fun initHeroes() {
        heros = arrayListOf(tieqi, yuren, yanmo, kuangjiang, nvyao, guangqiu, maomi, shexian, moqiu, aishen)
        super.initHeroes()
    }
}