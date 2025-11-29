import data.Config
import utils.CaijiUtil
import utils.MRobot

object TestUtil {
    suspend fun test(){
        MRobot.moveFullScreen()

//        CaijiUtil.saveRectByFolder(App.caijiPath+"\\pukepai",Config.AY_Puke_rect)
    }
}