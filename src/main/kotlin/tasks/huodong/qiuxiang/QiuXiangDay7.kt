package tasks.huodong.qiuxiang

import data.HeroBean
import data.HeroCreator

class QiuXiangDay7 : BaseQiuxiang() {
    val tieqi = HeroCreator.nvwang.create()
    val yuren = HeroCreator.feiji.create()
    val kuangjiang = HeroCreator.saman2.create()
    val nvyao = HeroCreator.yuren.create()
    val guangqiu = HeroCreator.shahuang.create()
    val moqiu = HeroCreator.haiyao.create()
    val yanmo = HeroCreator.shexian.create()

    val maomi = HeroCreator.guangqiu.create()
    val shexian = HeroCreator.moqiu.create()
    val aishen = HeroCreator.efei.create()

    override var downUpHero: HeroBean = yanmo

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