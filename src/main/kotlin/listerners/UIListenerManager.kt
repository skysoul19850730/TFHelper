package listerners

object UIListenerManager {
    private val mListeners = arrayListOf<UIListener>()


    public fun addUIListener(listener:UIListener){
        mListeners.add(listener)
    }

    fun removeUIListener(listener: UIListener){
        mListeners.remove(listener)
    }


    fun reCheckStars(){
        mListeners.forEach {
            it.onRecheckStars()
        }
    }

}