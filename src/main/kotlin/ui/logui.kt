package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import utils.LogUtil
import java.awt.image.BufferedImage

@Composable
@Preview
fun logPage(scope: BoxScope) {
    scope.apply {
        var state = rememberLazyListState()
        Card(Modifier.fillMaxSize(), elevation = 4.dp, backgroundColor = Color(0xfffafafa)) {
            Box {
                LazyColumn(state = state, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(LogUtil.messages) { index, item ->
                        if (item is LogUtil.LogData) {
                            val time = item.time + ": "
                            Row {
                                Text(time, fontSize = 12.sp)

                                if (item.data is String) {
                                    Text(item.data as String, fontSize = 12.sp)
                                } else if (item.data is BufferedImage) {
                                    var item = item.data as BufferedImage
                                    var ww = item.width
                                    var hh = item.height
                                    var maxHeight = 200
                                    if (item.width > item.height && item.width > maxHeight) {
                                        ww = maxHeight
                                        hh = ((item.height * 1f * ww) / item.width).toInt()
                                    } else if (item.height > item.width && item.height > maxHeight) {
                                        hh = maxHeight
                                        ww = ((item.width * 1f / item.height) * hh).toInt()
                                    }
                                    Image(item.toPainter(), null, Modifier.width(ww.dp).height(hh.dp))
                                }
                            }
                        }
                    }
                }

                VerticalScrollbar(
                    rememberScrollbarAdapter(state),
                    Modifier.align(Alignment.CenterEnd).fillMaxHeight()
                )
            }
        }
    }
}
