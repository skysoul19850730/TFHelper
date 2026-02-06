package ui.caiji

import HSpace
import VSpace
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.awt.awtEvent
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import data.Config
import data.MPoint
import data.MRect
import data.toHSBFirst
import foreach
import getImageFromFile
import grayBtn
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import saveTo
import ui.MainUIData
import utils.ImgUtil.forEach
import utils.MRobot
import java.awt.Desktop
import java.awt.Point
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent
import java.awt.image.BufferedImage
import java.io.File
import javax.swing.JPanel
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun IconCreatorPage(show: MutableState<Boolean>) {

    if (!show.value) return

    var inputFile by remember { mutableStateOf<File?>(null) }

    var dragImage by remember { mutableStateOf<BufferedImage?>(null) }
    var startPoint: Point? = null
    val movePoint = remember { mutableStateOf<Point?>(null) }

    var onlyDrawRect by remember { mutableStateOf<MRect?>(null) }

    var colorsSelect = remember { mutableStateListOf<Int>() }

    var scale by remember { mutableStateOf(1f) }
    var offX by remember { mutableStateOf(0f) }
    var offY by remember { mutableStateOf(0f) }

    var newImg by remember { mutableStateOf<BufferedImage?>(null) }
    var canvasBounds by remember { mutableStateOf<Rect?>(null) }

    Box(Modifier.fillMaxSize().background(Color.Red)) {

        if (dragImage == null) {
            Box(
                Modifier.size(100.dp).background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                DropBoxPanel(
                    Modifier.fillMaxSize(), MainUIData.window
                ) {
                    GlobalScope.launch {
                        inputFile = it
                        dragImage = getImageFromFile(it)
                        newImg = BufferedImage(dragImage!!.width, dragImage!!.height, BufferedImage.TYPE_INT_ARGB)
                    }
                }
                Text("拖入图片")
            }
        } else {
            Row(Modifier.fillMaxWidth().height(200.dp).padding(20.dp).align(Alignment.CenterStart), verticalAlignment = Alignment.CenterVertically) {

                Canvas(
                    Modifier.fillMaxHeight().aspectRatio(1f).clip(RectangleShape).background(Color.White)
                        .onGloballyPositioned {
                            canvasBounds = it.boundsInWindow()
                            var i = 0
                        }
                        .onPointerEvent(PointerEventType.Press) {
                            val global = it.awtEvent.point!!
                            val local = Offset(
                                global.x - (canvasBounds?.left ?: 0f),
                                global.y - (canvasBounds?.top ?: 0f)
                            )
                            val trueEvP = Point(local.x.toInt(), local.y.toInt())
                            if (it.buttons.isSecondaryPressed) {//按右键
                                movePoint.value = null
                                if (startPoint != null) {
                                    startPoint = null
                                }
                            } else if (it.buttons.isPrimaryPressed) {
                                startPoint = trueEvP
                            }
                        }.onPointerEvent(PointerEventType.Release) {
                            val global = it.awtEvent.point!!
                            val local = Offset(
                                global.x - (canvasBounds?.left ?: 0f),
                                global.y - (canvasBounds?.top ?: 0f)
                            )
                            val trueEvP = Point(local.x.toInt(), local.y.toInt())
                            if (startPoint != null) {
                                val end = trueEvP
                                val left = min(end.x, startPoint!!.x)
                                val top = min(end.y, startPoint!!.y)
                                val right = max(end.x, startPoint!!.x)
                                val bottom = max(end.y, startPoint!!.y)
                                if (left >= 0 && top >= 0) {
                                    if (right > left && bottom > top) {//矩形
                                        onlyDrawRect = MRect.create4P(left, top, right, bottom)
                                        val imgRect = screenRect2BitmapRect(onlyDrawRect!!, scale, offX, offY)

                                        //画rect后，把其他
                                        newImg?.foreach { i, i2 ->
                                            if (i in imgRect.left..imgRect.right && i2 in imgRect.top..imgRect.bottom) {

                                            } else {
                                                newImg!!.setRGB(i, i2, Color.Transparent.toArgb())
                                            }
                                            false
                                        }

                                    } else if (right == left && bottom == top) {//生成point
                                        var imP = screenPosition2BitmapPosition(
                                            Offset(end.x.toFloat(), end.y.toFloat()),
                                            scale = scale,
                                            offX,
                                            offY
                                        )
                                        if (imP != null) {
                                            colorsSelect.add(
                                                dragImage!!.getRGB(imP.x.toInt(), imP.y.toInt()).toHSBFirst()
                                            )
                                        }
                                    }
                                }
                                startPoint = null
                                movePoint.value = null
                            }
                        }.onPointerEvent(PointerEventType.Move) {
//                            val global = it.awtEvent.point!!
//                            val local = Offset(
//                                global.x - (canvasBounds?.left ?: 0f),
//                                global.y - (canvasBounds?.top ?: 0f)
//                            )
//                            val trueEvP = Point(local.x.toInt(), local.y.toInt())
//                            movePoint.value = trueEvP
//                            try {
//                                dragImage!!.setRGB(movePoint.value!!.x, movePoint.value!!.y, Color.Red.toArgb())
//                            } catch (e: Exception) {
//
//                            }

//                    text = "x:${curPoint.value.x} y:${curPoint.value.y}"
                        }) {


                    val wr = dragImage!!.width.toFloat() / size.width
                    val hr = dragImage!!.height.toFloat() / size.height
                    if (hr > wr) {
                        scale = hr
                        val newW = dragImage!!.width.toFloat() / scale
//                        offX = (size.width - newW) / 2f
                    } else {
                        scale = wr
                        val newH = dragImage!!.height.toFloat() / scale
//                        offY = (size.height - newH) / 2f
                    }

                    withTransform({
                        scale(1/scale, Offset(0f, 0f))
                    }) {
                        drawImage(dragImage!!.toComposeImageBitmap())



                    }
                    if (onlyDrawRect != null) {
                        drawRect(
                            Color.Yellow,
                            Offset(onlyDrawRect!!.left.toFloat(), onlyDrawRect!!.top.toFloat()), Size(
                                onlyDrawRect!!.width.toFloat(),
                                onlyDrawRect!!.height.toFloat()
                            )
                        )
                    }

                }

                HSpace(20)


                Canvas(Modifier.fillMaxHeight().aspectRatio(1f).clip(RectangleShape).background(Color.Blue)) {
                    withTransform({
//                        translate(left = offX, top = offY)
                        scale(1/scale, Offset(0f, 0f))
                    }) {

                        var rect = MRect.createWH(0, 0, dragImage!!.width, dragImage!!.height)
                        if (onlyDrawRect != null) {
                            val startP = screenPosition2BitmapPosition(
                                Offset(
                                    onlyDrawRect!!.left.toFloat(),
                                    onlyDrawRect!!.top.toFloat()
                                ), scale = scale, offX, offY
                            )
                            rect = MRect.createWH(
                                startP.x.toInt(), startP.y.toInt(), (onlyDrawRect!!.width * scale).toInt(),
                                (onlyDrawRect!!.height * scale).toInt()
                            )
                        }

                        rect.forEach { i, i2 ->

                            val color = dragImage!!.getRGB(i, i2)
                            val chs = color.toHSBFirst()
                            if (colorsSelect.count {
                                    chs in it - 15..it + 15
                                } > 0) {
                                newImg!!.setRGB(i, i2, color)
                            }
                            false

                        }


                        drawImage(newImg!!.toComposeImageBitmap())

                    }
                }
//                Box(Modifier.fillMaxWidth().aspectRatio(1f)) {
//                    Image(
//                        painter = dragImage!!.toPainter(), contentDescription = null,
//                        modifier = Modifier.fillMaxWidth().aspectRatio(1f).onPointerEvent(PointerEventType.Press) {
//                            if (it.buttons.isSecondaryPressed) {//按右键
//                                movePoint.value = null
//                                if (startPoint != null) {
//                                    startPoint = null
//                                }
//                            } else if (it.buttons.isPrimaryPressed) {
//                                startPoint = it.awtEvent.point!!
//                            }
//                        }.onPointerEvent(PointerEventType.Release) {
//                            if (startPoint != null) {
//                                val end = it.awtEvent.point!!
//                                val left = min(end.x, startPoint!!.x)
//                                val top = min(end.y, startPoint!!.y)
//                                val right = max(end.x, startPoint!!.x)
//                                val bottom = max(end.y, startPoint!!.y)
//                                if (left >= 0 && top >= 0) {
//                                    if (right > left && bottom > top) {//矩形
//                                        onlyDrawRect = MRect.create4P(left, top, right, bottom)
//                                    } else if (right == left && bottom == top) {//生成point
//
//                                    }
//                                }
//                                startPoint = null
//                                movePoint.value = null
//                            }
//                        }.onPointerEvent(PointerEventType.Move) {
//                            movePoint.value = it.awtEvent.point!!
//                            try {
//                                dragImage!!.setRGB(movePoint.value!!.x, movePoint.value!!.y, Color.Red.toArgb())
//                            } catch (e: Exception) {
//
//                            }
//
////                    text = "x:${curPoint.value.x} y:${curPoint.value.y}"
//                        },
//                        contentScale = ContentScale.Fit
//                    )
//                }


            }
        }


        Row {
            grayBtn("关闭") {
                show.value = false
            }

            grayBtn("保存"){
                newImg?.saveTo(File(inputFile?.parent, "${inputFile?.nameWithoutExtension}_${System.currentTimeMillis()}.png"))
            }
        }

    }


}


fun screenPosition2BitmapPosition(screenOffset: Offset, scale: Float, offX: Float, offY: Float): Offset {
    val x = screenOffset.x * scale - offX
    val y = screenOffset.y * scale - offY
    return Offset(x, y)
}

fun screenRect2BitmapRect(screenRect: MRect, scale: Float, offX: Float, offY: Float): MRect {
    val left = screenRect.left * scale - offX
    val top = screenRect.top * scale - offY
    val right = left + screenRect.width * scale
    val bottom = top + screenRect.height * scale
    return MRect.create4P(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
}

class DropBean {
    var x: Int = 0
    var y: Int = 0
    var width = 0
    var height = 0

    override fun equals(other: Any?): Boolean {
        other ?: return false
        if (other is DropBean) {
            return x == other.x && y == other.y && width == other.width && height == other.height
        }
        return super.equals(other)
    }
}

@Composable
fun DropBoxPanel(
    modifier: Modifier,
    window: ComposeWindow,
    component: JPanel = JPanel(), onFileDrop: (File) -> Unit
) {

    var dropBounds = remember {
        mutableStateOf<DropBean>(DropBean())
    }
    Box(modifier = modifier.onGloballyPositioned {
        println("${System.currentTimeMillis()}      1")
        val bounds = DropBean().apply {
            x = it.boundsInWindow().left.toInt()
            y = it.boundsInWindow().top.toInt()
            width = it.boundsInWindow().width.toInt()
            height = it.boundsInWindow().height.toInt()
        }
        if (dropBounds.value != bounds) {
            dropBounds.value = bounds
        }

    }) {
        LaunchedEffect(dropBounds.value) {
            println("${System.currentTimeMillis()}      2")
            component.setBounds(
                dropBounds.value!!.x,
                dropBounds.value!!.y,
                dropBounds.value!!.width,
                dropBounds.value!!.height
            )
            window.contentPane.add(component)
            val target = object : DropTarget() {
                override fun drop(dtde: DropTargetDropEvent) {
                    try {
                        dtde.acceptDrop(DnDConstants.ACTION_REFERENCE)
                        val droppedFiles =
                            dtde.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>
                        droppedFiles.firstOrNull()?.let {
                            val desktop = Desktop.getDesktop()
                            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                                onFileDrop.invoke(it)
                            }
                        }
                    } catch (e: Exception) {

                    }
                }
            }

            component.dropTarget = target
        }

//        SideEffect {
//            component.setBounds(dropBounds.value.x,dropBounds.value.y,dropBounds.value.width,dropBounds.value.height)
//        }
        DisposableEffect(true) {
            onDispose {
                window.contentPane.remove(component)
            }
        }
    }


}