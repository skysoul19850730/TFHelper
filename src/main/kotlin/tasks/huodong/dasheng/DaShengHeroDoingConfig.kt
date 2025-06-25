package tasks.huodong.dasheng

import data.HeroBean
import ui.zhandou.tiankong.TianKongModel

class DaShengHeroDoingConfig:BaseDaShengHeroDoing() {

    override fun initHeroes() {
        heros = arrayListOf<HeroBean>()
        var h1 = TianKongModel.zr_heros.value.split(",")
        if(h1.size>0){
            heros.addAll(h1.map {
                HeroBean(it)
            })
        }
        var h2 = TianKongModel.zr_qiu.value.split(",")
        if(h2.size>0){
            heros.addAll(h2.map {
                HeroBean(it, needCar = false)
            })
        }
        var h3 = TianKongModel.zr_gongcheng.value.split(",")
        if(h3.size>0){
            heros.addAll(h3.map {
                HeroBean(it, isGongCheng = true)
            })
        }

        upHeros = arrayListOf()
        var h4 = TianKongModel.sz_heros.value.split(",")
        if(h4.size>0){
            upHeros.addAll(heros.filter {
                h4.contains(it.heroName)
            })
        }
    }
}