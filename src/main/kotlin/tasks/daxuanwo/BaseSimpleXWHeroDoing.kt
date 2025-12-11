package tasks.daxuanwo

import data.Config
import data.HeroBean
import data.MPoint
import data.MRect
import getImage
import getImageFromRes
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tasks.SimpleHeZuoHeroDoing
import ui.zhandou.UIKeyListenerManager
import utils.ImgUtil
import utils.MRobot
import java.awt.event.KeyEvent

// 9  29 都自动执行
abstract class BaseSimpleXWHeroDoing() : SimpleHeZuoHeroDoing(), UIKeyListenerManager.UIKeyListener {

    var heroDown49:HeroBean?=null
    var midHeros69:List<HeroBean>? = null

    var auto59 = true

    override suspend fun onKeyDown(code: Int): Boolean {
        //如果龙王识别出错可以按快捷下对应卡牌，但不知道快捷键按下得时间，所以不能延时进行上卡，只能快捷键9来恢复上卡

        if (code == KeyEvent.VK_NUMPAD9) {//9强制改变waitting，防止waiting有逻辑错误不上卡
            waiting = !waiting
            return true
        }

        if (code == KeyEvent.VK_NUMPAD0) {
            if (curGuan == 9){
                GlobalScope.launch {
                    MRobot.moveFullScreen()
                }
                return true
            }
            if(curGuan==49){
                g49 = 1
                return true
            }
            if(curGuan==69){
                g69State+=1
                if(g69State>=2){
                    g69State=0
                }
                return true
            }

        }
        if(code == KeyEvent.VK_NUMPAD3){
            if(curGuan == 49){
                g49 = 2
                return true
            }
        }
        return false


    }


    fun add50(fullsHeros:List<HeroBean>, midHeros:List<HeroBean>){
        midHeros69 = midHeros
        addGuanDeal(50){
            over {
                fulls(*fullsHeros.toTypedArray())
            }
            chooseHero{
                val otherHeros = fullsHeros.filter { !midHeros.contains(it)}
                if(!carDoing.carps.get(0).hasHero() || !carDoing.carps.get(1).hasHero()){
                    //如果01位置空着，就上其他的
                    upAny(*otherHeros.toTypedArray())
                }else if(!carDoing.carps.get(2).hasHero() || !carDoing.carps.get(3).hasHero()){
                    //如果01都上好后，2，3有空的，则上mids
                   var index = upAny(*midHeros.toTypedArray())
                    if(index>-1){
                        index
                    }else{
                        //如果没有mids预选，可以上 0，1位置的英雄
                        val list = carDoing.carps.take(2).map {
                            it.mHeroBean!!
                        }
                        upAny(*list.toTypedArray())
                    }
                }else{
                    //如果0123都不空了，就开始全部上满
                    upAny(*fullsHeros.toTypedArray())
                }
            }

            onStart {

                //如果不在fulls里，就下掉
                carDoing.carps.forEach {
                    if(it.mHeroBean!=null && !fullsHeros.contains(it.mHeroBean)){
                        carDoing.downHero(it.mHeroBean!!)
                    }
                }

                //如果要摆中间的两个 不在中间，就下掉要重上
                midHeros.forEach {
                    if(it.position!=2 && it.position!=3){
                        carDoing.downHero(it)
                    }
                }

                //看中间的两个是不是要摆放的，不是的话，要下掉
                var hero2 = carDoing.carps.get(2).mHeroBean
                if(hero2!=null && !midHeros.contains(hero2)){
                    carDoing.downPosition(2)
                }
                hero2 = carDoing.carps.get(3).mHeroBean
                if(hero2!=null && !midHeros.contains(hero2)){
                    carDoing.downPosition(3)
                }



            }

        }
    }

    var g49 = 0
    fun add49(heroBean: HeroBean){
        heroDown49 = heroBean

        addGuanDeal(48){
            over {
                heroDown49!!.isInCar()
            }
            chooseHero {
                upAny(heroDown49!!)
            }
            onStart {
                carDoing.downHero(heroDown49!!)
            }
        }

        addGuanDeal(49){
            over {
                curGuan>49 || (g49==2 && heroDown49!!.isFull() && g49StartBoss == null)
            }
            chooseHero {
                if(heroDown49!!.isFull() && g49StartBoss!=null){
                    g49 =3
                }

                if(g49 == 3){
                      g49StartBoss?.invoke(this)?:-1
                }else {

                    val index = indexOf(heroDown49)
                    if (index > -1) {
                        while (g49 == 0) {
                            delay(100)
                        }
                        if (g49 != 2) {
                            carDoing.downHero(heroDown49!!)
                            g49 = 0
                            delay(100)
                        }
                        index
                    } else {
                        -1
                    }
                }
            }
            des = "需要切的时候按0，会自动下卡再上卡，收集完成后按3，切换的卡会上满"
        }
    }

    var g49StartBoss : (suspend (List<HeroBean?>)->Int)?=null


    var g69State = 0 //0:全上，1 下中间俩
    fun add69(){
        addGuanDeal(69){
            over {
                curGuan>69
            }

            chooseHero {
                if(g69State==0){
                    if(midHeros69?.all { it.isFull() }==true){
                        while(g69State == 0){
                            delay(200)
                        }
                    }else{
                        upAny(*midHeros69!!.toTypedArray())
                    }
                }
                if(g69State == 1){
                    midHeros69?.forEach {
                        carDoing.downHero(it)
                    }
                    while(g69State == 1){
                        delay(100)
                    }
                }
                -1
            }

            des = "69关，按0依次执行 下中间两个或上满中间两个"
        }
    }

    override fun onGuanChange(guan: Int) {
        super.onGuanChange(guan)

        if(guan == 9){
            GlobalScope.launch {
                delay(1000)
                MRobot.moveFullScreen()
            }
        }
        if(guan == 29){
            start29()
        }else{
            stop29()
        }

        if(auto59){
            if(guan == 59){
                start59()
            }else {
                stop59()
            }
        }


        if(guan in listOf(59)){
            App.startAutoSave(200)
        }else{
            App.stopAutoSave()
        }
    }


    override fun onGuanFix(guan: Int) {
        guankaTask?.setCurGuanIndex(guan)
    }

    override fun onLongWangZsClick() {
    }

    override fun onLongWangFsClick() {
    }

    override fun onKey3Down() {
    }

    override fun onWaitingClick() {
        waiting = !waiting
    }


    override fun onStart() {
        super.onStart()
        UIKeyListenerManager.addKeyListener(this)
    }

    override fun onStop() {
        super.onStop()
        UIKeyListenerManager.removeKeyListener(this)
        App.stopAutoSave()
    }


    var job29:Job?=null
    fun start29(){
        var subFoler = "${Config.platName}/xuanwo/"
        val daxia = getImageFromRes("${subFoler}xw_daxia.png")
        val xw_pangxie = getImageFromRes("${subFoler}xw_pangxie.png")
        val xw_shaoji = getImageFromRes("${subFoler}xw_shaoji.png")
        val xw_shaoyu = getImageFromRes("${subFoler}xw_shaoyu.png")
        val xw_zhutou = getImageFromRes("${subFoler}xw_zhutou.png")

        job29?.cancel()
        job29 = GlobalScope.launch {
            while(curGuan==29){
                val img = getImage(MRect.createWH(675,210,54,30))
                val sim = 0.95
              val point =   if(ImgUtil.isImageSim(img,daxia,sim)){
                    MPoint(650,300)
                }else  if(ImgUtil.isImageSim(img,xw_pangxie,sim)){
                  MPoint(410,310)
              }else if(ImgUtil.isImageSim(img,xw_shaoji,sim)){
                  MPoint(470,410)
              }else if(ImgUtil.isImageSim(img,xw_shaoyu,sim)){
                  MPoint(580,410)
              }else if(ImgUtil.isImageSim(img,xw_zhutou,sim)){
                  MPoint(530,270)
              }else{
                  null
              }
                if(point!=null){
                    point.clickPc()
                    delay(5000)
                }else{
                    delay(100)
                }
            }
        }
    }
    fun stop29(){
        job29?.cancel()
    }

    fun start59(){

    }
    fun stop59(){

    }
}