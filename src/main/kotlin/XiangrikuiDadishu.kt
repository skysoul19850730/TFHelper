import data.MPoint
import data.MRect
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import utils.ImgUtil.forEach
import utils.hasColorCount
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File

class XiangrikuiDadishu {

    val rect1 = MRect.createWH(284+10,335,90,120)
    val rect2 = MRect.createWH(161+10,398,90,100)
    val rect3 = MRect.createWH(412,398+10,90,100)
    val rect4 = MRect.createWH(132+10,527,90,100)
    val rect5 = MRect.createWH(284+10,527,90,120)
    val rect6 = MRect.createWH(448+10,527,90,100)
    val rect7 = MRect.createWH(161+10,650,90,120)
    val rect8 = MRect.createWH(403+10,650,90,120)
    val rect9 = MRect.createWH(284+10,727,90,120)

    val leiColor = Color(99,93,82)
    val leiColor2 = Color(66,60,57)
val nothingColor = Color(237,199,130)
val nothingColor2 = Color(247,199,99)
val nothingColor3 = Color(247,203,107)
    val pointLast = MPoint(336 ,584)

    val rectList = listOf(rect1,rect2,rect3,rect4,rect5,rect6
    ,rect7,rect8,rect9)

    var job:Job?=null

    companion object{
        var type = -1
    }
    fun needClickIndex(){
        if(job!=null){
            job?.cancel()
            job = null
        }else {
          job =  GlobalScope.launch {

              while (type>=0) {
                  while (type == 0) {

                      val img = getImage()

                      rectList.forEachIndexed { index, it ->
                          val count = when(index){
                              0->200
                              1->500
                              else-> 800
                          }
                          if (it.hasColorCount(nothingColor, 0, img) < count) {
                              //有东西出现70
                              if (it.hasColorCount(leiColor, 0, img) > 10
//                                  || it.hasColorCount(leiColor2, 0, img) > 200
                                  ) {
                                  //有雷
                              } else {
                                  it.clickPoint.click()
                                  delay(30)
                              }
//                              App.save()
                          }

                          //黄金
//                          val noC1 = it.hasColorCount(nothingColor2,0,img)
//                          val noC2 = it.hasColorCount(nothingColor3,0,img)
//                          if(noC1+noC2<1000){
//                              //有东西出现
//                              if (it.hasColorCount(leiColor, 0, img) > 70) {
//                                  //有雷
//                              } else {
//                                  it.clickPoint.click()
//                                  delay(30)
//                              }
//                          }

                      }
                      delay(30)
                  }

                  while (type == 1) {
                      pointLast.click()
                      delay(10)
                  }

              }

            }

        }

    }


    fun test(){
        val image = getImageFromFile(File(App.caijiPath,"09_12_39_676.png"))
//        val image = getImageFromFile(File(App.caijiPath,"08_37_47_037.png"))
        val c = image.hasColorCount(leiColor,0)
        log(c)

        rectList.forEach {
            var noCount0 = it.hasColorCount(nothingColor, 0, image)
            var noCount = it.hasColorCount(nothingColor2, 0, image)
            var noCount2 = it.hasColorCount(nothingColor3, 0, image)
            log("noCount ${noCount0} ${noCount} $noCount2")
            if (noCount< 1000) {
                //有东西出现
                val leiC = it.hasColorCount(leiColor, 0, image)
                log("leiC $leiC")
                if (leiC> 150) {
                    //有雷
                } else {
//                    it.clickPoint.click()
                }
            }
        }
//        rectList.forEach {
//           val c = it.hasColorCount(nothingColor,0,image)
//            log(c)
//        }
        rectList.forEach {
            it.forEach { i, i2 ->
//                if(image.getRGB(i,i2) == nothingColor.rgb)
                image.setRGB(i,i2,Color(255,0,255,25).rgb)
            }
        }
        image
    }
}
fun main(){
   XiangrikuiDadishu().test()
}