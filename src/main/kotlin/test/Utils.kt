package test

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import getImageFromFile
import org.apache.commons.codec.binary.Hex
import org.apache.commons.lang3.StringUtils
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.*
import java.net.URLEncoder
import java.security.MessageDigest
import java.util.*
import java.util.zip.CRC32
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object Utils {
    fun mapToPairList(params: Map<String?, String?>?): List<NameValuePair> {
        val res: MutableList<NameValuePair> = ArrayList()
        if (params != null) {
            for (key in params.keys) {
                res.add(BasicNameValuePair(key, params[key]))
            }
        }
        return res
    }

    @Throws(Exception::class)
    fun hashSHA256(content: ByteArray?): String {
        return try {
            val md = MessageDigest.getInstance("SHA-256")
            Hex.encodeHexString(md.digest(content))
        } catch (e: Exception) {
            throw Exception(
                "Unable to compute hash while signing request: "
                        + e.message, e
            )
        }
    }

    @Throws(Exception::class)
    fun hmacSHA256(key: ByteArray?, content: String): ByteArray {
        return try {
            val mac = Mac.getInstance("HmacSHA256")
            mac.init(SecretKeySpec(key, "HmacSHA256"))
            mac.doFinal(content.toByteArray())
        } catch (e: Exception) {
            throw Exception(
                "Unable to calculate a request signature: "
                        + e.message, e
            )
        }
    }

    @Throws(Exception::class)
    fun hmacSHA1(key: ByteArray?, content: String): ByteArray {
        return try {
            val mac = Mac.getInstance("HmacSHA1")
            mac.init(SecretKeySpec(key, "HmacSHA1"))
            mac.doFinal(content.toByteArray())
        } catch (e: Exception) {
            throw Exception(
                "Unable to calculate a request signature: "
                        + e.message, e
            )
        }
    }

    fun randWeights(weightsMap: Map<String, Int>, excludeDomain: String): String {
        var weightSum = 0
        for ((key, value) in weightsMap) {
            if (key == excludeDomain) {
                continue
            }
            weightSum += value
        }
        if (weightSum <= 0) {
            return ""
        }
        val random = Random()
        var r = random.nextInt(weightSum) + 1
        for ((key, value) in weightsMap) {
            if (key == excludeDomain) {
                continue
            }
            r -= value
            if (r <= 0) {
                return key
            }
        }
        return ""
    }

    fun encode(v: Map<String, List<String>>?): String {
        if (v == null) {
            return ""
        }
        val stringBuilder = StringBuilder()
        val treeMap = TreeMap<String, List<String>>()
        treeMap.putAll(v)
        try {
            for ((key, value) in treeMap) {
                val keyEscaped = URLEncoder.encode(key, "UTF-8")
                for (s in value) {
                    if (stringBuilder.length > 0) {
                        stringBuilder.append("&")
                    }
                    stringBuilder.append(keyEscaped)
                    stringBuilder.append("=")
                    stringBuilder.append(URLEncoder.encode(s, "UTF-8"))
                }
            }
        } catch (e: UnsupportedEncodingException) {
            return ""
        }
        return stringBuilder.toString()
    }

    @Throws(Exception::class)
    fun crc32(filePath: String?): Long {
        return try {
            val inputStream: InputStream = BufferedInputStream(FileInputStream(filePath))
            val crc = CRC32()
            val bytes = ByteArray(1024)
            var cnt: Int
            while (inputStream.read(bytes).also { cnt = it } != -1) {
                crc.update(bytes, 0, cnt)
            }
            inputStream.close()
            crc.value
        } catch (e: Exception) {
            throw Exception(
                "Unable to calculate crc32: "
                        + e.message, e
            )
        }
    }

    fun getWindowFolderImg(name:String):BufferedImage{
        //12_33_43_618
       return getImageFromFile(File(App.caijiPath,name))
    }


    //第二个图 方形的话 ，左上圆点477 173
    //判断边框，方形的一条边的内边基本都是白色，可以以方形为基准，看其左侧一定范围内有没有一个数条超过多少白色的，如上，第二个图片是方形时，左上角坐标477,173一直向下都是白色
    //为了防止每个位置的图的边的x不准，可以拟定检测宽度为5，以 上面为例，就是检测 477-2 到477+2

    //填色，整体扫描下白色超过50%的就是填充的，一大片白色呢

    //图案的检测


}

