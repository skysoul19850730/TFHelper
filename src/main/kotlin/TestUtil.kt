fun main() {
    try {
        Class.forName("nu.pattern.OpenCV")
        nu.pattern.OpenCV.loadLocally()
    } catch (e: Exception) {
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)
    }
    val start = System.currentTimeMillis()


    WX79Test.test()



    println("耗时:${System.currentTimeMillis() - start}毫秒")

}