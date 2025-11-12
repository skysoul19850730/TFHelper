package ui.zhandou

object UIKeyListenerManager {

    var keyListeners:MutableList<UIKeyListener> = mutableListOf()

    fun addKeyListener(keyListener: UIKeyListener) {
        keyListeners.add(keyListener)
    }

    fun removeKeyListener(keyListener: UIKeyListener) {
        keyListeners.remove(keyListener)
    }

    interface UIKeyListener {
        fun onWaitingClick()
//        fun onMuClick()
        fun onLongWangZsClick(){}
        fun onLongWangFsClick(){}

        fun onChuanzhangClick(position:Int){}

        /**
         * 键3,防止快捷键 失效时，可以点击面板上的按键3.
         */
        fun onKey3Down(){}

        fun onGuanFix(guan:Int){}
    }

    fun onWaitingClick() {
        keyListeners.forEach {
            it.onWaitingClick()
        }
    }
//    fun onMuClick() {
//        keyListeners.forEach {
//            it.onMuClick()
//        }
//    }

    fun onLongWangZsClick() {
        keyListeners.forEach {
            it.onLongWangZsClick()
        }
    }
    fun onLongWangFsClick() {
        keyListeners.forEach {
            it.onLongWangFsClick()
        }
    }
    fun onChuanzhangClick(position:Int) {
        keyListeners.forEach {
            it.onChuanzhangClick(position)
        }
    }

    fun onKey3Down(){
        keyListeners.forEach {
            it.onKey3Down()
        }
    }

    fun onGuanFix(guan:Int){
        keyListeners.forEach {
            it.onGuanFix(guan)
        }
    }
}