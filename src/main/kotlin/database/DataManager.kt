package database

import data.Config
import getImageFromFile
import resFile
import java.io.File

object DataManager {
    val dataDirPath = Config.data_main_path
    var wxGroups = arrayListOf<String>()
        get() {
            if(field.isEmpty()){
                init()
            }
            return field
        }


    fun init() {
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
    }

    fun addGroup(text:String){
        if(!wxGroups.contains(text)){
            wxGroups.add(text)
            saveGroups()
        }
    }

    fun removeGroup(text:String){
        if(wxGroups.contains(text)){
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