import data.MPoint
import data.MRect
//import tasks.anyue.base.Ay99Test
import test.Utils
import utils.AYUtil
import utils.ImgUtil
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File

object TestUtil {
    suspend fun test(){
//        MRobot.moveFullScreen()

//        CaijiUtil.saveRectByFolder(App.caijiPath+"\\pukepai",Config.AY_Puke_rect)
//        WX59().caiji()
//        Ay99Test().shibiePai()

        val img = Utils.getWindowFolderImg("y001.png")

        val diffIcon = AYUtil.getDiffIcon(img)
        log("diffIcon is $diffIcon")

//        val img2 = Utils.getWindowFolderImg("y001.png").getSubImage(rect2)
//        val transparentImage = BufferedImage(
//            img.width,
//            img.height,
//            BufferedImage.TYPE_INT_ARGB  // 支持alpha通道
//        )
//        img.foreach { i, i2 ->
//            val rgb = Color(img.getRGB(i,i2))
//            val rgb2 = Color(img2.getRGB(i,i2))
//            if(rgb.red<200 && rgb.green<200 && rgb.blue<200){
//
//                if(rgb2.red<200 && rgb2.green<200 && rgb2.blue<200){
//
//                }else{
//                    transparentImage.setRGB(i,i2,Color.BLUE.rgb)
//                }
//
//            }else{
//                if(rgb2.red<200 && rgb2.green<200 && rgb2.blue<200){
//                    transparentImage.setRGB(i,i2,Color.RED.rgb)
//                }else{
//
//                }
//
//            }
//            false
//        }


//        val img = Utils.getWindowFolderImg("y001.png").getSubImage(rect1)
//        val transparentImage = BufferedImage(
//            img.width,
//            img.height,
//            BufferedImage.TYPE_INT_ARGB  // 支持alpha通道
//        )
//        img.foreach { i, i2 ->
//            val rgb = Color(img.getRGB(i,i2))
//            if(rgb.red<200 && rgb.green<200 && rgb.blue<200){
//
//            }else{
//                transparentImage.setRGB(i,i2,Color.RED.rgb)
//            }
//            false
//        }
//        transparentImage.saveTo(File(App.caijiPath,"t222222.png"))
//        val img = Utils.getWindowFolderImg("y001.png")
//        testTZ(img)
    }

}