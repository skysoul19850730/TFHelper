package ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable

@Composable
fun TFTheme(content:@Composable ()->Unit){
    MaterialTheme(typography = Typography) {
        Surface(content=content)
    }
}