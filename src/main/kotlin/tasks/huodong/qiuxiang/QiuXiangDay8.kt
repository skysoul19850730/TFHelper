package tasks.huodong.qiuxiang

import data.HeroBean
import data.HeroCreator

class QiuXiangDay8 : BaseQiuxiang() {
    val tieqi = HeroCreator.gugong.create()
    val yuren = HeroCreator.feiji.create()
    val kuangjiang = HeroCreator.gugu.create()
    val nvyao = HeroCreator.hugong.create()
    val guangqiu = HeroCreator.houyi.create()
    val moqiu = HeroCreator.haiyao.create()
    //下卡
    val yanmo = HeroCreator.baoku.create()

    val maomi = HeroCreator.guangqiu.create()
    val shexian = HeroCreator.moqiu.create()
    val aishen = HeroCreator.shenv.create()

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