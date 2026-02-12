import data.*
import model.CarDoing
import opencv.*
import org.opencv.imgcodecs.Imgcodecs
import utils.ImgUtil.slidingPixelMatch
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File

object WX69Test {

    //    val user = "sqc"
    val user = "Administrator"
    val folder = "C:\\Users\\${user}\\Desktop\\debug"
    //77 324 206 59
    val checkRect = MRect.createWH(78, 326, 205, 55)
    fun test() {
        //146,106,106
        //177,82,101

        val img = getImageFromFile(File(folder,"w1.png")).getSubImage(checkRect)
        val img2 = getImageFromFile(File(folder,"w2.png")).getSubImage(checkRect)

        //220 20  20
        val imgModel = getImageFromFile(File(folder,"wx69.png"))

        if(img.hasImage(imgModel,true)){
            log("图1 有标识")
        }
        if(img2.hasImage(imgModel,true)){
            log("图2 有标识")
        }


        println("\n\n")
    }

}