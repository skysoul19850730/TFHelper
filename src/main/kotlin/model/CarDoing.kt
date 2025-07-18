package model

import MainData
import data.*
import data.Config.debug
import getImage
import kotlinx.coroutines.*
import log
import logOnly
import java.awt.Color
import java.awt.image.BufferedImage

class CarDoing(var chePosition: Int = -1, var cheType: Int = CheType_YangChe) {

    var downCardSpeed = false

    var carps = arrayListOf<CarPosition>()

    companion object {

        val salePoint = MPoint().apply {
            x = 310
            y = 440
        }

        val CheType_YangChe = 0//羊车和寒冰车星星位置差不多，马车差两三个像素
        val CheType_MaChe = 1

        val starCheckRect = MRect.create4P(310, 340, 344, 368)
        val saleCheckRect = MRect.create4P(260, 430, 300, 460)

        val cardClosePoint = MPoint(752, 125, Color(222, 73, 57))

    }

    fun hasOpenSpace() = carps.take(6).count {
        it.mIsOpen && it.mHeroBean == null
    } > 0

    fun hasAllOpenSpace() = carps.count {
        it.mIsOpen && it.mHeroBean == null
    }>0
    fun hasSpace() = carps.take(6).count {
       it.mHeroBean == null
    } > 0

    fun hasNotFull() = carps.count {
        var isfull = it.mHeroBean?.isFull() ?: true
        !isfull
    } > 0

    fun hasNotFullExcepte(vararg heroBean: HeroBean) = carps.count {

        if(it.mHeroBean in heroBean){
            false
        }else {

            var isfull = it.mHeroBean?.isFull() ?: true
            !isfull
        }
    } > 0

    fun openCount() = carps.take(6).count {
        it.mIsOpen
    }

    suspend fun checkStarMuluti() {
        carps.filter { it.hasHero() }.forEach {
            it.checkStarMuluty()
        }
    }

    suspend fun checkStarsWithoutCard(): Boolean {

        var checkHasChanged = false
        try {
            withTimeout(500) {
                while (!checkHasChanged) {
                    var changeCount = 0
                    carps.filter { it.hasHero() }.forEach {
                        val hero = it.mHeroBean!!
                        var hl = it.getStarLevelDirect()
                        if (hl > hero.currentLevel) {
                            log("check star without card hero:${hero.heroName} changed,cur level is ${hl}")
                            changeCount++
                            hero.currentLevel = hl
                        }
                    }
                    //如果处理实时降星（比如大空翼）就不要按光球来考虑了
                    if (changeCount == 1) {//一个光球只有一个会变，太多的变了，可能是有些英雄的弹道颜色和星星一直识别错误了，这种就导致最终识别变的>1，就再使用弹窗检测
                        checkHasChanged = true
                    }
                    delay(100)//withTimeOut中如果没有挂起方法，就不会触发timeout。。
                }
            }
        } catch (e: Exception) {
            logOnly("check timeout")
            return false
        }
        sysDataToMain()
        return checkHasChanged
    }

    /**
     * 此方法是全部检测一遍星级，用于打败降星的boss后，检测一遍，不同于@see checkStars 是用完光之后check，遇到一个星级改变就结束了
     */
    suspend fun reCheckStars() {
        carps.forEach {
            it.checkStarLevelUseCard()
        }
        sysDataToMain()
    }

    suspend fun checkStars(): Boolean {
        var checkHasChanged = false

        carps.filter { it.hasHero() }.forEach {
            val hero = it.mHeroBean!!
            if (!hero.isFull() && !checkHasChanged) {//如果记录过满星了，就不用识别，因为合作没有降星
                checkHasChanged = hero.checkStarLevelUseCard(this)
            }
            log("${hero.heroName} level is ${hero.currentLevel}")

        }
        sysDataToMain()
        return checkHasChanged
    }

    fun reInitPositions(offset: Int = 0) {
        var r = 123//影响船长识别，不要乱调，目前为止123合适
        if (cheType == CheType_YangChe) {
            //209,428//未开格子时，特么不是226，226点不中
//            carps.get(0).reInitRectAndPoint(MRect.createPointR(MPoint(260 + offset, 430), r), MPoint(260 + offset, 450))
            carps.get(0).reInitRectAndPoint(MRect.createPointR(MPoint(209 + offset, 430), r), MPoint(209 + offset, 450))
            carps.get(1).reInitRectAndPoint(MRect.createPointR(MPoint(226 + offset, 430), r), MPoint(226 + offset, 450))
            carps.get(2).reInitRectAndPoint(MRect.createPointR(MPoint(260 + offset, 360), r), MPoint(260 + offset, 380))
            carps.get(3).reInitRectAndPoint(MRect.createPointR(MPoint(226 + offset, 360), r), MPoint(226 + offset, 380))
            carps.get(4).reInitRectAndPoint(MRect.createPointR(MPoint(260 + offset, 286), r), MPoint(260 + offset, 312))
            carps.get(5).reInitRectAndPoint(MRect.createPointR(MPoint(226 + offset, 286), r), MPoint(226 + offset, 311))
        } else if (cheType == CheType_MaChe) {
//            carps.get(0).reInitRectAndPoint(MRect.createPointR(MPoint(260 + offset, 425), r), MPoint(260 + offset, 448))
            carps.get(0).reInitRectAndPoint(MRect.createPointR(MPoint(209 + offset, 425), r), MPoint(209 + offset, 449))
            carps.get(1).reInitRectAndPoint(MRect.createPointR(MPoint(226 + offset, 425), r), MPoint(226 + offset, 449))
            carps.get(2).reInitRectAndPoint(MRect.createPointR(MPoint(260 + offset, 355), r), MPoint(260 + offset, 377))
            carps.get(3).reInitRectAndPoint(MRect.createPointR(MPoint(226 + offset, 355), r), MPoint(226 + offset, 378))
            carps.get(4).reInitRectAndPoint(MRect.createPointR(MPoint(260 + offset, 286), r), MPoint(260 + offset, 312))
            carps.get(5).reInitRectAndPoint(MRect.createPointR(MPoint(226 + offset, 286), r), MPoint(226 + offset, 313))
        }
        carps.get(6).reInitRectAndPoint(
            MRect.createPointR(Config.point7p_qianche.apply {
                x += offset
            }, r),
            MPoint(216 + offset, 236),
        )
    }

    /**
     * 合作时，前车第一个位置默认赋值成第二个位置，阔建或调用此方法重新修正成第一个位置。否做阔建前位置不对，无法下卡，比如合作上了娇女，刷了两手出了战将，要替换，但按pos 0来点击点不到娇女
     */
    fun reInitPos0ForQianche(offset: Int = 0) {
        var r = 123//影响船长识别，不要乱调，目前为止123合适
        if (chePosition == 1) {
            if (cheType == CheType_YangChe) {
                carps.get(0).reInitRectAndPoint(
                    MRect.createPointR(MPoint(260 + offset, 430), r),
                    MPoint(260 + offset, 450)
                )
            } else {
                carps.get(0).reInitRectAndPoint(
                    MRect.createPointR(MPoint(260 + offset, 425), r),
                    MPoint(260 + offset, 448)
                )
            }
        }
    }

    /**
     * 暂时不考虑工程位的中间态（车开格子不同，高度不同。工程位置是变化的，如果想处理，就要看不同层数的时候，工程位都记录下来，然后工程位单独管理，不放在这个list中）
     */
    fun initPositions(offset: Int = 0) {
        var r = 123//影响船长识别，不要乱调，目前为止123合适
        if (chePosition == -1) {//单车模式
            carps.apply {
                clear()
                //单车没采集过星星位置，目前逻辑用不到，随便写上先
                add(CarPosition(carps.size, MRect.createWH(70, 388, 37, 70), MPoint(88, 448), this@CarDoing))
                add(CarPosition(carps.size, MRect.createWH(28, 388, 37, 70), MPoint(46, 448), this@CarDoing))
                add(CarPosition(carps.size, MRect.createWH(70, 318, 37, 70), MPoint(46, 377), this@CarDoing))
                add(CarPosition(carps.size, MRect.createWH(28, 318, 37, 70), MPoint(88, 378), this@CarDoing))
                add(CarPosition(carps.size, MRect.createWH(70, 248, 37, 70), MPoint(46, 312), this@CarDoing))
                add(CarPosition(carps.size, MRect.createWH(28, 248, 37, 70), MPoint(88, 313), this@CarDoing))

                //工程位
                add(CarPosition(carps.size, MRect.createWH(28, 248, 37, 70), MPoint(88, 313), this@CarDoing))
            }
        } else {
            //101 138 226  260
            if (chePosition == 0) {
                carps.apply {
                    clear()
                    if (cheType == CheType_YangChe) {
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(MPoint(139 + offset, 430), r),
                                MPoint(138 + offset, 450),
                                this@CarDoing
                            )
                        )
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(MPoint(101 + offset, 430), r),
                                MPoint(100 + offset, 450),
                                this@CarDoing
                            )
                        )
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(MPoint(139 + offset, 360), r),
                                MPoint(138 + offset, 380),
                                this@CarDoing
                            )
                        )
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(MPoint(101 + offset, 360), r),
                                MPoint(100 + offset, 380),
                                this@CarDoing
                            )
                        )
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(MPoint(139 + offset, 286), r),
                                MPoint(138 + offset, 312),
                                this@CarDoing
                            )
                        )
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(MPoint(101 + offset, 286), r),
                                MPoint(100 + offset, 311),
                                this@CarDoing
                            )
                        )
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(Config.point7p_houche.apply {
                                    x += offset
                                }, r),
                                MPoint(118 + offset, 236),
                                this@CarDoing
                            )
                        )
                    } else if (cheType == CheType_MaChe) {
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(MPoint(138 + offset, 425), r),
                                MPoint(138 + offset, 448),
                                this@CarDoing
                            )
                        )
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(MPoint(101 + offset, 425), r),
                                MPoint(100 + offset, 449),
                                this@CarDoing
                            )
                        )
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(MPoint(138 + offset, 355), r),
                                MPoint(138 + offset, 377),
                                this@CarDoing
                            )
                        )
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(MPoint(101 + offset, 355), r),
                                MPoint(100 + offset, 378),
                                this@CarDoing
                            )
                        )
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(MPoint(138 + offset, 286), r),
                                MPoint(138 + offset, 312),
                                this@CarDoing
                            )
                        )
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(MPoint(101 + offset, 286), r),
                                MPoint(100 + offset, 313),
                                this@CarDoing
                            )
                        )

                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(Config.point7p_houche.apply {
                                    x += offset
                                }, r),
                                MPoint(118 + offset, 240),
                                this@CarDoing
                            )
                        )
                    }


                }
            } else {
                carps.apply {
                    clear()
                    if (cheType == CheType_YangChe) {
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(MPoint(260 + offset, 430), r),
                                MPoint(260 + offset, 450),
                                this@CarDoing
                            )
//                            CarPosition(
//                                carps.size,
//                                MRect.createPointR(MPoint(226 + offset, 430), r),
//                                MPoint(226 + offset, 450),
//                                this@CarDoing
//                            )
                        )
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(MPoint(226 + offset, 430), r),
                                MPoint(226 + offset, 450),
                                this@CarDoing
                            )
                        )
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(MPoint(260 + offset, 360), r),
                                MPoint(260 + offset, 380),
                                this@CarDoing
                            )
                        )
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(MPoint(226 + offset, 360), r),
                                MPoint(226 + offset, 380),
                                this@CarDoing
                            )
                        )
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(MPoint(260 + offset, 286), r),
                                MPoint(260 + offset, 312),
                                this@CarDoing
                            )
                        )
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(MPoint(226 + offset, 286), r),
                                MPoint(226 + offset, 311),
                                this@CarDoing
                            )
                        )
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(Config.point7p_qianche.apply {
                                    x += offset
                                }, r),
                                MPoint(216 + offset, 236),
                                this@CarDoing
                            )
                        )
                    } else if (cheType == CheType_MaChe) {
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(MPoint(260 + offset, 425), r),
                                MPoint(260 + offset, 448),
                                this@CarDoing
                            )
//                            CarPosition(
//                                carps.size,
//                                MRect.createPointR(MPoint(226 + offset, 425), r),
//                                MPoint(226 + offset, 449),
//                                this@CarDoing
//                            )
                        )
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(MPoint(226 + offset, 425), r),
                                MPoint(226 + offset, 449),
                                this@CarDoing
                            )
                        )
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(MPoint(260 + offset, 355), r),
                                MPoint(260 + offset, 377),
                                this@CarDoing
                            )
                        )
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(MPoint(226 + offset, 355), r),
                                MPoint(226 + offset, 378),
                                this@CarDoing
                            )
                        )
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(MPoint(260 + offset, 286), r),
                                MPoint(260 + offset, 312),
                                this@CarDoing
                            )
                        )
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(MPoint(226 + offset, 286), r),
                                MPoint(226 + offset, 313),
                                this@CarDoing
                            )
                        )
                        add(
                            CarPosition(
                                carps.size,
                                MRect.createPointR(Config.point7p_qianche.apply {
                                    x += offset
                                }, r),
                                MPoint(216 + offset, 240),
                                this@CarDoing
                            )
                        )
                    }


                }
            }
        }

    }

    //    var mainData: MutableState<ArrayList<CarPosition>>? = null
    fun attchToMain() {
//        mainData = MainData.carPositions
    }

    fun sysDataToMain() {
        MainData.carPositions.clear()
        MainData.carPositions.addAll(carps)
        log("sysDataToMain")
    }

    fun addHero(heroBean: HeroBean) {
        if (heroBean.needCar) {
            if (heroBean.position >= 0) {//已在车上
                heroBean.currentLevel++
                if (debug) {
                    log("${heroBean.heroName} is in car allReady")
                }
            } else {

                var carPosition = if (heroBean.isGongCheng) {
                    carps.get(6)
                } else {
                    carps.find {
                        it.mPos < 6 && it.mHeroBean == null
                    }
                }
                carPosition?.addHero(heroBean)
            }
            sysDataToMain()
        }
    }

    suspend fun downPosition(position: Int) {
        if(position<0||position>6)return
        carps.get(position).mHeroBean?.let {
            downHero(it)
        }
    }

    var downing = false
    suspend fun downHero(heroBean: HeroBean) {
        if (heroBean.isInCar()) {
            downing = true
            carps.get(heroBean.position).downHero()
            downing = false
            sysDataToMain()
        }
    }

    fun getChuanZhangMax(imgTest: BufferedImage? = null): Pair<Int, Float>? {
        var startTime = System.currentTimeMillis()
        val img = imgTest ?: getImage(App.rectWindow)
        var maxIndex = -1
        var maxRate = 0f
        var allNull = carps.all {
            !it.hasHero()
        }
        carps.forEachIndexed { index, carPosition ->
            if (carPosition.hasHero() || allNull) {//allNull是判断副车，不涉及下卡，所以每个位置都检测。如果是主车，null的位置就不检测，比如没有工程位，或者下掉的位置没补上
//                logOnly("车$chePosition 车位$index ")
                var rate = carPosition.rateSelectByChuanZhang(img)
//                logOnly("车$chePosition 车位$index 识别率：$rate")
                if (rate > maxRate) {
                    maxRate = rate
                    maxIndex = index
                }
            }
        }
        var coast = System.currentTimeMillis() - startTime
        if (maxRate > 0.1) {
            log("最符合的车位是$maxIndex 其比例为:$maxRate  coast:$coast")
//            log(img)
        }
        if (maxRate > 0.2) {

            return Pair(maxIndex, maxRate)
        }
//        log(img)
//        logOnly("未识别到合适的车位  coast:$coast")
        return null
    }


//    fun touziCheck(): Boolean {
//        var img = getImage(touziRect)
//
//        for (x in 0 until img.width) {
//            for (y in 0 until img.height) {
//                val color = Color(img.getRGB(x, y))
//                if (isColorWhite(color)) {
//                    if (x - 2 >= 0 && x + 7 < img.width && y + 9 < img.height) {
//                        var rect = MRect.createWH(x - 2, y, 10, 10)
//                        var white = 0
//                        rect.forEach { x1, y1 ->
//                            val color1 = Color(img.getRGB(x1, y1))
//                            if (isColorWhite(color1)) {
//                                white++
//                            }
//                        }
//                        if (white > 60) {
//                            log("识别到骰子")
//                            return true
//                        }
//                    }
//                }
//            }
//        }
//
//
//        return false
//    }
//
//    private fun isColorWhite(c1: Color, c2: Color = Color.white): Boolean {
//        return (abs(c1.red - c2.red) <= 20
//                && abs(c1.green - c2.green) <= 20
//                && abs(c1.blue - c2.blue) <= 20)
//    }


    fun testGetAy19Imgs(img: BufferedImage): List<BufferedImage> {
        val list = arrayListOf<BufferedImage>()
        carps.forEach {
            list.add(it.getAy19Img(img))
        }
        return list
    }

    fun getAY19Selected(imgTest: BufferedImage? = null): Int {
        var startTime = System.currentTimeMillis()
        val img = imgTest ?: getImage(App.rectWindow)
        var index = carps.indexOfFirst {
            it.isAy19SelectedV2(img)
        }
        logOnly(img)
        if (index > -1) {
            logOnly("che $chePosition ay selected at index :${index} time:${System.currentTimeMillis() - startTime}")
        }
        return index
    }
    fun getHB199Selected(imgTest: BufferedImage? = null): Int {
        var startTime = System.currentTimeMillis()
        val img = imgTest ?: getImage(App.rectWindow)
        var index = carps.indexOfFirst {
            it.isHB199Selected(img)
        }

        logOnly(img)
        if (index > -1) {
            logOnly("che $chePosition ay selected at index :${index} time:${System.currentTimeMillis() - startTime}")
        }
        return index
    }
}