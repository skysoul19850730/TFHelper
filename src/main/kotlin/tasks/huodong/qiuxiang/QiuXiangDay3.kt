package tasks.huodong.qiuxiang

import data.HeroBean
import data.HeroCreator

class QiuXiangDay3 : BaseQiuxiang() {
    val tieqi = HeroCreator.dianfa.create()
    val yuren = HeroCreator.feiji.create()
    val kuangjiang = HeroCreator.gugu.create()
    val nvyao = HeroCreator.wawa.create()
    val guangqiu = HeroCreator.xiongmao.create()
    val moqiu = HeroCreator.yanmo.create()
    val yanmo = HeroCreator.longwang.create()

    val maomi = HeroCreator.guangqiu.create()
    val shexian = HeroCreator.nvwang.create()
    val aishen = HeroCreator.youling.create()

    override var downUpHero: HeroBean = nvyao

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