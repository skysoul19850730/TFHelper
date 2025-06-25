package tasks.anyue.zhanjiang.deprecated

import App
import MainData
import data.*
import kotlinx.coroutines.*
import log
import tasks.Zhuangbei
import tasks.anyue.zhanjiang.BaseAnYueHeroDoing
import ui.zhandou.UIKeyListenerManager
import java.awt.event.KeyEvent.*

class AYZhanNvHeroDoing3 : BaseAnYueHeroDoing(),
    UIKeyListenerManager.UIKeyListener {//默认赋值0，左边，借用左边第一个position得点击，去识别车位置后再更改
//es8  es4 ye5   6e0 y80  y50 y56 e04

    val zhanjiang = HeroBean("zhanjiang", 100)
    val nvwang = HeroBean("nvwang", 90)
    val saman = HeroBean("saman2", 80)
    val jiaonv = HeroBean("jiaonv", 70)
    val shahuang = HeroBean("shahuang", 60, compareRate = 0.9)
    val baoku = HeroBean("baoku", 30, needCar = true, isGongCheng = true, compareRate = 0.9)
    val niutou = HeroBean("niutou2", 30)
    val dijing = HeroBean("dijing", 30)
    val huanqiu = HeroBean("huanqiu", 20, needCar = false, compareRate = 0.95)
    val guangqiu = HeroBean("guangqiu", 0, needCar = false)

    //副卡 底层 石头，萨满 其他：女王 小野 死神 亡将 射线（宝库）地精 光球 魔球

    enum class Guan(val des: String? = null) {
        /**
         * 满战将，萨满，地精， 娇女，牛头，女王，宝库
         */
        g1("满战将，女王（不满） 娇女，宝库"),

        /**
         * 满上女王，下地精，补满沙皇，备地精
         */
        g17("满上女王，下地精，补满沙皇，备地精"),

        /**
         * 开识别，上下地精操作
         */
        g19("开识别，上下地精操作"),

        /**
         * 补满 沙皇 牛头 宝库，娇女，女王，萨满1星
         */
        g21("补满 沙皇 牛头 宝库，娇女，女王，萨满1星"),

        g28("下地精，补满沙皇"),
        g31("圣剑,交换女王沙皇位置，沙皇去底部"),
        g37("圣剑,交换女王沙皇位置，沙皇去底部"),
        g41("烟斗"),
//        /**
//         * 开识别，(快捷键点哪个下哪个),按3上回，副卡可以都下
//         */
//        g39("开识别，(快捷键点哪个下哪个),按3上回，副卡可以都下"),

        /**
         * 上满阵容，女王萨满不满
         */
//        g41("上满阵容，女王萨满不满")
        g48("下地精，上女王，副卡控球"),

        g51("用圣剑")
    }

    override fun doOnGuanChanged(guan: Int) {
        if (guan == 51) {
            guanka = Guan.g51
            waiting = false
            return
        }

        if (guan == 48) {//37把上面都下了。副卡操作，副卡带个狂将吧，暂时只自己打
            guanka = Guan.g48
            waiting = false
            return
        }


        if (guan == 41) {//49暂时也是用21的guan来满，之后副卡控制球到结束
//            guanka = Guan.g41
            App.stopAutoSave()
            guanka = Guan.g41
            waiting = false
            return
        }


//        if (guan == 39) {//39保持waitingtrue，只监听按键进行下卡。等41再上回，不保险的话，手动上下女王保持伤害吧
//            App.startAutoSave()
////            start19Oberserver()
////            waiting = false
//            return
//        }
        if (guan == 37) {//37把上面都下了。副卡操作，副卡带个狂将吧，暂时只自己打
            GlobalScope.launch {
                guanka = Guan.g37
                carDoing.downHero(saman)//下了重上一星
                waiting = false
            }
            return
        }


        if (guan == 31) {
            GlobalScope.launch {
                guanka = Guan.g31
                if (shahuang.position != 1) {//将沙皇移动到下面。方便上下女王，副卡女王这时就满了
                    carDoing.downHero(shahuang)
                    carDoing.downPosition(1)
                }

//                carDoing.downHero(nvwang)
                waiting = false
            }
//            guanka = Guan.g31
//            waiting = false
            return
        }

        if (guan == 28) {
            guanka = Guan.g28
            waiting = false
            return
        }

        if (guan == 21) {
//            App.stopAutoSave()
            stop19Oberserver()
            guanka = Guan.g21
            preHero(null)//释放补卡
            waiting = false
            return
        }

        if (guan == 19) {
//            App.startAutoSave()
//            guanka = Guan.g19
            start19Oberserver()
            return
        }

        if (guan == 17) {
            guanka = Guan.g17
//            if (isGkOver(Guan.g11) && !dijing.isInCar()) {//g11 over即车位满了，如果地精不在车上就要pre dijing了
//                preHero(dijing)
//            }
            waiting = false
            return
        }

    }

    var guanka = Guan.g1
        set(value) {
            field = value
            MainData.curGuanKaName.value = value.name
            MainData.curGuanKaDes.value = value.des ?: "无"
        }


    override fun onStart() {
        super.onStart()
        UIKeyListenerManager.addKeyListener(this)
    }

    override fun onStop() {
        super.onStop()
        UIKeyListenerManager.removeKeyListener(this)
    }


    override fun onHeroPointByG19(hero: HeroBean): Boolean {//点战将无敌抗，点两次认输。。。
        return hero != zhanjiang
    }

    var gk1Over = false
    fun isGkOver(g: Guan): Boolean {

        var heroOk = zhanjiang.isFull()
        if (!heroOk) return false

        return when (g) {
            Guan.g1 -> {
                if (gk1Over) true
                else (dijing.isFull() && jiaonv.isFull() && niutou.isFull() && nvwang.isInCar() && saman.isFull() && baoku.isFull()).apply {
                    gk1Over = this
                }
            }

//            Guan.g17 -> {
//                (dijing.isFull() && jiaonv.isFull() && niutou.isFull() && nvwang.isFull() && saman.isFull() && baoku.isFull())
//            }

            Guan.g21 -> {
                dijing.isInCar() && jiaonv.isFull() && niutou.isFull() && nvwang.isFull() && saman.isFull() && baoku.isFull() && Zhuangbei.isYandou()
            }

            Guan.g28 -> shahuang.isFull() && jiaonv.isFull() && niutou.isFull() && nvwang.isFull() && saman.isFull() && baoku.isFull() && Zhuangbei.isYandou()
            Guan.g31 -> shahuang.isFull() && baoku.isFull()
//            Guan.g31 -> Zhuangbei.isShengjian() && shahuang.isFull()  && baoku.isFull()
            Guan.g37 -> nvwang.isInCar()
            Guan.g41 -> shahuang.isFull() && jiaonv.isFull() && niutou.isFull() && dijing.isFull() && saman.isFull() && baoku.isFull() && Zhuangbei.isYandou()
            Guan.g48 -> nvwang.isInCar()
            Guan.g51 -> {
                shahuang.isFull() && jiaonv.isFull() && niutou.isFull() && nvwang.isFull() && saman.isFull() && baoku.isFull() && Zhuangbei.isShengjian()
            }

            else -> false
        }
    }


    override fun initHeroes() {
        heros = arrayListOf()
        heros.add(zhanjiang)
        heros.add(nvwang)
        heros.add(saman)
        heros.add(jiaonv)
        heros.add(shahuang)
        heros.add(niutou)
        heros.add(dijing)
        heros.add(baoku)
        heros.add(huanqiu)
        heros.add(guangqiu)
    }

    suspend fun doOnKeyDown(code: Int): Boolean {

        if (guankaTask?.currentGuanIndex == 39 || guankaTask?.currentGuanIndex == 38
            || guankaTask?.currentGuanIndex == 49 || guankaTask?.currentGuanIndex == 48
        ) {//19
            if (code == VK_NUMPAD3) {
                GlobalScope.launch {
                    if (nvwang.isInCar()) {//女王在车上，下女王，备女王
                        waiting = true
                        carDoing.downHero(nvwang)
                        preHero(nvwang)
                        waiting = false
                    } else {//不在车上，上女王
                        preHero(null)
                    }
                }
            }
            return true
        }

        return true
    }


    override suspend fun doAfterHeroBeforeWaiting(heroBean: HeroBean) {
        if (heroBean.heroName == "muqiu") {
            return
        }
        if (!waiting && isGkOver(guanka)) {
            waiting = true
        }
    }

    override suspend fun dealHero(heros: List<HeroBean?>): Int {
        while (waiting) {
            delay(100)
        }
//        if (!waiting && isGkOver(guanka)) {
//            waiting = true
//        }

        if (guanka == Guan.g1) {//第一阶段
            if (!zhanjiang.isFull()) {//直上战将
                log("战将没满")
//                log("战将没满")
                var index = heros.indexOf(zhanjiang)
                if (index > -1) {
                    return index
                }
                index = heros.indexOf(guangqiu)
                if (index > -1 && zhanjiang.isInCar()) {
                    if (dijing.isInCar() && !dijing.isFull()) {
                        carDoing.downHero(dijing)
                    }
                    return index
                }
                return heros.indexOf(dijing)
            } else {
                var curG = guankaTask?.currentGuanIndex ?: 0
                if (curG < 8) {
                    if (heros.indexOf(dijing) > -1) {
                        return heros.indexOf(dijing)
                    }
                    if (heros.indexOf(guangqiu) > -1) {
                        if (dijing.isInCar() && !dijing.isFull()) {
                            return heros.indexOf(guangqiu)
                        }
                    }
                    return defaultDealHero(heros, arrayListOf(dijing, baoku, jiaonv, saman, niutou, guangqiu))
                }


//                while (curG < 7) {
//                    delay(100)
//                    curG = guankaTask?.currentGuanIndex ?: 0
//                }
                if (!nvwang.isInCar()) {//女王补底层坑位，一星
                    var index = heros.indexOf(nvwang)
                    if (index > -1) {
                        return index
                    }

                    return defaultDealHero(heros, arrayListOf(dijing, baoku, jiaonv, saman, niutou, guangqiu))
                } else {
                    return defaultDealHero(heros, arrayListOf(dijing, baoku, jiaonv, saman, niutou, guangqiu))
                }
            }
            return -1
        } else if (guanka == Guan.g17) {

            if (!isGkOver(Guan.g1)) {//g11没结束，代表还没满车位，对于19来说很危险，所以继续完成11，但实际，这里情况基本没有，只是防止万一，比如确实卡牌很严重等。
                log("guan11还未结束")
                return defaultDealHero(heros, arrayListOf(dijing, baoku, jiaonv, saman, niutou, guangqiu, nvwang))
            }

            if (dijing.isInCar()) {
                log("g17:地精在车上")
                heros.filter {
                    it != null && it.needCar
                }.ifEmpty {
                    return -1
                }.apply {

                    firstOrNull { !it!!.isInCar() }?.let {
                        log("找到${it.heroName} 不在车上，下地精 上${it.heroName} ,备地精")
                        carDoing.downHero(dijing)
                        return heros.indexOf(it)
                    }//找不在车上的


                    //比如第一次时地精等其他都满，女王不满（防止抢兵）.这时如果上面找到了沙皇就下地精上沙皇了，后面会满上
                    //如果没有沙皇，那么我们就先满女王
                    firstOrNull { it!!.isFull() && !it.isFull() }?.let {
                        return heros.indexOf(it)
                    }

                    //因为地精1星，比如前面下了女王，如果这一组没女王，那么就补地精，补满，女王就出来了，否则万一就一直不出女王就尴尬了
                    if (!dijing.isFull()) {
                        return heros.indexOf(dijing)
                    }
                }
                if (carDoing.hasNotFull()) {
                    return heros.indexOf(guangqiu)
                }
            } else {
                //未出现地精时上这几个
                log("地精不在车上")
                if (carDoing.hasNotFull()) {//先升满，然后备地精
                    return defaultDealHero(heros, arrayListOf(shahuang, baoku, jiaonv, saman, niutou, guangqiu, nvwang))
                } else {
                    preHero(dijing)
                }

            }
            return -1
        } else if (guanka == Guan.g21) {
            //dijing赚点钱,满不满都行
            if (shahuang.isInCar()) {
                carDoing.downHero(shahuang)
            }
            var index =
                defaultDealHero(heros, arrayListOf(dijing, baoku, jiaonv, saman, niutou, nvwang, zhanjiang))
            if (index > -1) {
                return index
            }

            if (carDoing.hasNotFull()) {
                index = heros.indexOf(guangqiu)
                if (index > -1) {
                    return index
                }
            }


            index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isYandou() && Zhuangbei.hasZhuangbei()) {//小翼 烟斗
                return index
            }

        } else if (guanka == Guan.g28) {
            carDoing.downHero(dijing)
            var index =
                defaultDealHero(heros, arrayListOf(shahuang, baoku, jiaonv, saman, niutou, nvwang, zhanjiang))
            if (index > -1) {
                return index
            }

            if (carDoing.hasNotFull()) {
                index = heros.indexOf(guangqiu)
                if (index > -1) {
                    return index
                }
            }

            index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isYandou() && Zhuangbei.hasZhuangbei()) {//小翼 烟斗
                return index
            }
        } else if (guanka == Guan.g31) {
            var index = -1
            if (!shahuang.isInCar()) {
                index = heros.indexOf(shahuang)
                if (index > -1) return index
            }
//            else {
//                index = defaultDealHero(heros, arrayListOf(shahuang, dijing))
//                if (index > -1) return index
//            }
//            index = defaultDealHero(heros, arrayListOf(nvwang,shahuang,saman,jiaonv,niutou,zhanjiang))
//
//            if(index>-1){
//                return index
//            }
//
            if (carDoing.hasNotFull()) {
                index = heros.indexOf(guangqiu)
                if (index > -1) return index
            }

//            index = heros.indexOf(huanqiu)
//            if (index > -1 && !Zhuangbei.isShengjian() && Zhuangbei.hasZhuangbei()) {//小翼 烟斗
//                return index
//            }

        } else if (guanka == Guan.g37) {

            carDoing.downHero(dijing)
            carDoing.downHero(jiaonv)
            carDoing.downHero(saman)
            carDoing.downHero(niutou)
            if (nvwang.isFull()) {
                carDoing.downHero(nvwang)
            }
            if (!nvwang.isInCar()) {
                return heros.indexOf(nvwang)
            }

//            var index = heros.indexOf(nvwang)
//            if(index>-1 && !nvwang.isInCar()){
//                return index
//            }
//            index = heros.indexOf(saman)
//            if(index>-1 && !saman.isInCar()){
//                return index
//            }
        } else if (guanka == Guan.g41) {

            carDoing.downHero(nvwang)
            var index =
                defaultDealHero(heros, arrayListOf(dijing, shahuang, baoku, jiaonv, saman, niutou, zhanjiang))
            if (index > -1) {
                return index
            }

            if (carDoing.hasNotFull()) {
                index = heros.indexOf(guangqiu)
                if (index > -1) {
                    return index
                }
            }

            index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isYandou() && Zhuangbei.hasZhuangbei()) {//小翼 烟斗
                return index
            }
        } else if (guanka == Guan.g48) {
            carDoing.downHero(dijing)
            return heros.indexOf(nvwang)
        } else if (guanka == Guan.g51) {
            var index = heros.indexOf(huanqiu)
            if (index > -1 && !Zhuangbei.isShengjian() && Zhuangbei.hasZhuangbei()) {//小翼 烟斗
                return index
            }
        }
        return -1
    }

    override fun changeHeroWhenNoSpace(heroBean: HeroBean): HeroBean? {
        return null
    }

    override suspend fun onKeyDown(code: Int): Boolean {
        if (super.onKeyDown(code)) {
            return true
        }
        return doOnKeyDown(code)
    }

    override fun onWaitingClick() {
        waiting = !waiting
        log("cur waiting is $waiting")
    }

    override fun onLongWangZsClick() {
    }

    override fun onLongWangFsClick() {
    }

    override fun onChuanzhangClick(position: Int) {
    }

    override fun onKey3Down() {
        GlobalScope.launch {
            onKeyDown(VK_NUMPAD3)
        }
    }

    override fun onGuanFix(guan: Int) {
        guankaTask?.setCurGuanIndex(guan)
    }
}