import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import java.io.File
@Composable
fun String.toPainter():Painter{
    return painterResource("pres${File.separator}${this}")
}