package tasks.hezuo.zhannvsha

import data.Config
import data.Config.delayLong
import data.Config.delayNor
import data.MPoint
import data.Recognize
import getImage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import log
import tasks.HomePage
import tasks.WxUtil
import tesshelper.Tess
import utils.MRobot

class GWorldRoomFind : IRoomFind() {

    override suspend fun findAndJoinRoom() {
        log("findAndJoinRoom")
        findGameHezuo()
    }

    suspend fun findGameHezuo() {
        delay(3000)
        var finding = true
        HomePage.openWorldMsg()
        delay(300)
        GlobalScope.launch {
            while (finding){

                if (Recognize.NoAdvOk.isFit()) {
                    log("try in")
                    Recognize.NoAdvOk.click()
                    delay(300)
                }else{
                    log("clcik")
                    MPoint(765, 480).clickPc()
                }
            }
        }
        var checkCount = 0
        while (finding){

            if(!Recognize.NoAdvOk.isFit()&&!HomePage.pointMsgClose.isFit()){
                checkCount++
                if(checkCount>3) {
                    log("进了")
                    finding = false
                }
                delay(1000)
            }
        }
    }
}