package utils

import data.Config
import getImageFromFile
import model.CarDoing
import saveTo
import utils.ImgUtil.copyWithColor
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File

object CaijiUtil {

    object Anyue{

        fun get19pics(imgNames:List<String>){

//            val carDoing = CarDoing(0,CarDoing.CheType_MaChe)
//            carDoing.initPositions()
            val carDoing2= CarDoing(1,CarDoing.CheType_MaChe)
            carDoing2.initPositions()

            val result = arrayListOf<BufferedImage>()
            imgNames.forEach {
                val img = getImageFromFile(File( Config.caiji_main_path+"\\tmp",it))
                val list = arrayListOf<BufferedImage>()
//                list.addAll(carDoing.testGetAy19Imgs(img))
                list.addAll(carDoing2.testGetAy19Imgs(img))

                list.forEach {

                    val tmp = it.copyWithColor(Color(255,9,9),10)
                    if(tmp!=null){
                        if(result.firstOrNull {
                                ImgUtil.isImageSim(it, tmp, 0.99)
                            } ==null){
                            result.add(tmp)
                            tmp.saveTo(File(App.caijiPath+"/anyue19","anyue19_${result.size}.png"))
                        }
                    }

                }


            }
        }
    }



}