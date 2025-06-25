package tasks.hezuo.zhannvsha

import kotlinx.coroutines.delay
import tasks.WxUtil

class WXRoomFind:IRoomFind() {

    override suspend fun beforeFind() {
        WxUtil.findWindowAndMove()
//        WxUtil.sendImg("hezuogonglue.jpg")
//        delay(100)
        WxUtil.sendText("发送 +0000(四位房号)+,上一星冰女一星猴子一星女王挂机")
//        WxUtil.sendText("?")
    }

    override suspend fun getText(): String {
        return WxUtil.getText()
    }

    override suspend fun onRoomFail(room:String) {
        WxUtil.sendText("房间${room} 进入失败")
    }
    override suspend fun onTextTry(text: String) {
        WxUtil.sendText("正在尝试进入房间$text")
    }
}