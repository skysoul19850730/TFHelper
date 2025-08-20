package tasks.hanbing.deprecated.mengyan

import data.Config
import getImage
import getImageFromFile
import getSubImage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import log
import loges
import model.CarDoing
import model.CarDoing.Companion.CheType_YangChe
import tasks.guankatask.GuankaTask
import java.io.File
import kotlin.math.abs

object ChuanZhangTest {


    fun startChuanZhangOberserver() {
        GlobalScope.launch {

            var car0 = CarDoing(0, CheType_YangChe).apply {
                initPositions()
            }
            var car1 = CarDoing(0, CheType_YangChe).apply {
                initPositions()
                chePosition=1
                reInitPositions()
            }
            File(App.caijiPath, "chuanzhang").listFiles().forEach {
//                if(it.name.startsWith("123123")) {
                    var img = getImageFromFile(it)
//                    var guan = img.getSubImage(Config.zhandou_hezuo_guankaRect)
//                    GuankaTask().testGuan(guan)

                    var index = car0.getChuanZhangMax(img)
                    var index2 = car1.getChuanZhangMax(img)
                log(img)
                    if (index != null && (index2 == null || index.second > index2.second)) {

                        loges("检测结果 车位 0  位置 ${index.first} rate  ${index.second}")
                    } else if (index2 != null && (index == null || index2.second > index.second)) {
                        loges("检测结果 车位 1 位置 ${index2.first} rate ${index2.second}")
                    }else{
                        loges("检测结果  未检测到点名")
                    }
//                }
            }

        }
    }

    private fun colorCompare(c1: java.awt.Color, c2: java.awt.Color, sim: Int = 10): Boolean {
        return (abs(c1.red - c2.red) <= sim
                && abs(c1.green - c2.green) <= sim
                && abs(c1.blue - c2.blue) <= sim)
    }
}