package tasks.anyue.base

interface DoSomeThing3Steps {
    fun onPre():Int
    fun onDo():Int
    fun afterDo():Int
}