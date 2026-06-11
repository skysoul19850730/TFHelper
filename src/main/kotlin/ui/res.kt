import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.res.painterResource
import org.jetbrains.skia.Image
import java.io.File
@Composable
fun String.toPainter():Painter{
//    return painterResource("pres/${this}")
    val stream = Thread.currentThread().contextClassLoader.getResourceAsStream("pres/$this")!!
    val bytes = stream.readBytes()
    val img = Image.makeFromEncoded(bytes)
    return BitmapPainter(img.toComposeImageBitmap())
}