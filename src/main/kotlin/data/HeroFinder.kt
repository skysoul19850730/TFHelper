package data

import getImageFromFile
import getImageFromRes
import utils.ImgUtil
import java.awt.image.BufferedImage
import java.io.File

class HeroFinder(val mFile: File) {

    val imgList = arrayListOf<BufferedImage>()
    val imgList2 = arrayListOf<BufferedImage>()
    val imgList3 = arrayListOf<BufferedImage>()
    var is3Files = false

    fun init() {
        var childs = mFile.listFiles()

        if (childs.isNotEmpty()) {

            if (childs.get(0).isDirectory) {
                is3Files = true
                childs.forEachIndexed { index, file ->
                    var list = when (index) {
                        0 -> imgList
                        1 -> imgList2
                        else -> imgList3
                    }
                    file.listFiles().forEach {

//                        list.add(getImageFromRes("${mFile.absolutePath}${File.separator}${file.name}${File.separator}${it.name}"))
                        list.add(getImageFromFile(File(it.absolutePath)))
                    }

                }
            } else {
                childs.forEach {
                    imgList.add(getImageFromFile(File(it.absolutePath)))
                }
            }

        }
    }

    fun fitImage(oImg: BufferedImage, position: Int, compareRate: Double): Boolean {

        var list = if (!is3Files) imgList else when (position) {
            0 -> imgList
            1 -> imgList2
            else -> imgList3
        }

        list.forEach {
            if (ImgUtil.isImageSim(it, oImg, if (is3Files) 0.97 else compareRate, mFile.name)) {
//                log(it)
                return true
            }
        }
        return false
    }
}