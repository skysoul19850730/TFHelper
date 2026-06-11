package database

import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.TypeReference
import com.google.gson.Gson
import data.Config
import getImageFromFile
import resFile
import ui.utils.HerosPageData
import ui.utils.WashDataBean
import java.io.File

object DataManager {
    lateinit var dataConfig: DataConfig
        private set
    val dataDirPath = Config.data_main_path
    var wxGroups = arrayListOf<String>()
        get() {
            if (field.isEmpty()) {
                init()
            }
            return field
        }


    private fun initDataConfig(): DataConfig {
        var washFile = File(dataDirPath, "configs.txt")
        if (!washFile.exists()) {
            washFile.createNewFile()
        }
        val text = washFile.readText()
        val map =  Gson().fromJson<DataConfig>(text, DataConfig::class.java)?:DataConfig()
//        val map = JSONObject.parseObject(text,object : TypeReference<DataConfig>() {})
        return map
    }

    fun saveDataConfig(change:(DataConfig)->DataConfig) {
        var washFile = File(dataDirPath, "configs.txt")
        if (!washFile.exists()) {
            washFile.createNewFile()
        }
        dataConfig = change(dataConfig)
        washFile.writeText(JSONObject.toJSONString(dataConfig))
    }


    fun initWashData(): Map<String, WashDataBean> {
        var washFile = File(dataDirPath, "wash.txt")
        if (!washFile.exists()) {
            washFile.createNewFile()
        }
        val text = washFile.readText()

        val map = JSONObject.parseObject(text, object : TypeReference<Map<String, WashDataBean>>() {})
            ?: HashMap()
        return map
    }

    fun saveWashData() {
        var washFile = File(dataDirPath, "wash.txt")
        if (!washFile.exists()) {
            washFile.createNewFile()
        }
        washFile.writeText(JSONObject.toJSONString(HerosPageData.washMap))
    }


    fun init() {
        var dir = File(dataDirPath)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        var list = arrayListOf<String>()
        var file = File(dataDirPath, "groups.txt")
        if (!file.exists()) {
            list.add("中國同盟会")
            wxGroups = list
            saveGroups()
        } else {
            var text = file.readText()
            if (text.isNotEmpty()) {
                list.addAll(text.split(","))
                wxGroups = list
                saveGroups()
            }
            if (list.isEmpty()) {
                list.add("中國同盟会")
                wxGroups = list
                saveGroups()
            }
        }

        dataConfig = initDataConfig()
    }

    fun addGroup(text: String) {
        if (!wxGroups.contains(text)) {
            wxGroups.add(text)
            saveGroups()
        }
    }

    fun removeGroup(text: String) {
        if (wxGroups.contains(text)) {
            wxGroups.remove(text)
            saveGroups()
        }
    }

    fun saveGroups() {
        var text = ""
        wxGroups.forEach {
            text += it
            text += ","
        }
        text = text.dropLast(1)

        var file = File(dataDirPath, "groups.txt")
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()
        file.appendText(text)
    }


}