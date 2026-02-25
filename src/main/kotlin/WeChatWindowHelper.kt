package com.skysoul.pdftool

import App
import com.sun.jna.Native
import com.sun.jna.Memory
import com.sun.jna.Pointer
import com.sun.jna.Structure
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinDef.HDC
import com.sun.jna.platform.win32.WinDef.RECT
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser
import com.sun.jna.platform.win32.WinGDI
import com.sun.jna.platform.win32.GDI32
import com.sun.jna.ptr.IntByReference
import data.MRect
import getSubImage
import log
import java.awt.image.BufferedImage
import java.awt.Robot
import java.awt.Rectangle

/**
 * MARGINS 结构 - 用于 DwmExtendFrameIntoClientArea
 */
@Structure.FieldOrder("cxLeftWidth", "cxRightWidth", "cyTopHeight", "cyBottomHeight")
class MARGINS : Structure() {
    @JvmField
    var cxLeftWidth: Int = 0

    @JvmField
    var cxRightWidth: Int = 0

    @JvmField
    var cyTopHeight: Int = 0

    @JvmField
    var cyBottomHeight: Int = 0
}

/**
 * 微信小程序窗口助手
 *
 * 解决微信小程序窗口阴影问题
 */
object WeChatWindowHelper {

    // DWM API 常量
    private const val DWMWA_NCRENDERING_POLICY = 2
    private const val DWMNCRP_DISABLED = 1
    private const val DWMWA_EXTENDED_FRAME_BOUNDS = 9

    var borderWidth = 0
    var finalWidth = 0
    var finalHeight = 0

    // 加载 dwmapi.dll
    private val dwmapi by lazy {
        try {
            Native.load("dwmapi", DwmApi::class.java)
        } catch (e: Exception) {
            log("警告: 无法加载 dwmapi.dll, 某些功能可能不可用")
            null
        }
    }

    fun initWindow() {
        val hwnd = findWeChatWindow("塔防精灵")?:return
        App.tfWindow = hwnd
        setWindowSize(hwnd,1000,607)

    }

    /**
     * 查找微信小程序窗口
     *
     * @param title 窗口标题（部分匹配）
     * @return 窗口句柄，未找到返回 null
     */
    private fun findWeChatWindow(title: String? = null): HWND? {
        val windows = mutableListOf<HWND>()

        User32.INSTANCE.EnumWindows({ hwnd, _ ->
            val windowTitle = getWindowTitle(hwnd)

            // 如果指定了标题，进行匹配
            if (title != null) {
                if (windowTitle.contains(title, ignoreCase = true)) {
                    windows.add(hwnd)
                }
            } else {
                // 查找所有可能的微信窗口
                if (windowTitle.contains("微信", ignoreCase = true) ||
                    windowTitle.contains("WeChat", ignoreCase = true)
                ) {
                    windows.add(hwnd)
                }
            }

            true
        }, null)

        return windows.firstOrNull()
    }

    /**
     * 获取窗口标题
     */
    private fun getWindowTitle(hwnd: HWND): String {
        val buffer = CharArray(512)
        User32.INSTANCE.GetWindowText(hwnd, buffer, buffer.size)
        return String(buffer).trim('\u0000')
    }

    /**
     * 获取窗口实际边界（不包含阴影）
     *
     * @param hwnd 窗口句柄
     * @return 窗口矩形，失败返回 null
     */
    private fun getWindowRealBounds(hwnd: HWND): RECT? {
        return try {
            if (dwmapi == null) {
                log("DWM API 不可用，返回普通窗口矩形")
                val rect = RECT()
                User32.INSTANCE.GetWindowRect(hwnd, rect)
                return rect
            }

            val rect = RECT()
            val result = dwmapi!!.DwmGetWindowAttribute(
                hwnd,
                DWMWA_EXTENDED_FRAME_BOUNDS,
                rect.pointer,
                rect.size()
            )

            if (result == 0) rect else null
        } catch (e: Exception) {
            log("获取窗口边界失败: ${e.message}")
            null
        }
    }

    /**
     * 移动窗口（自动处理阴影）
     *
     * @param hwnd 窗口句柄
     * @param x 左上角 X 坐标
     * @param y 左上角 Y 坐标
     * @param width 窗口宽度
     * @param height 窗口高度
     * @param removeShadow 是否移除阴影（默认 true）
     * @param removeCompletely 是否彻底移除阴影占位（默认 false）
     * @param useClientArea 是否使用客户区尺寸（默认 false）。如果为 true，则 x,y,width,height 表示客户区的位置和大小
     */
    private fun setWindowSize(
        hwnd: HWND,
        width: Int,
        height: Int,
    ) {
        log("移动窗口: ${getWindowTitle(hwnd)}")

        // 如果使用客户区尺寸，需要先获取当前边框信息
        var finalX = 0
        var finalY = 0


        // 获取当前窗口信息以计算边框
        val info = getWindowInfo(hwnd)
        borderWidth = info.getBorderWidth()
        val titleBarHeight = info.getTitleBarHeight()

        log("  边框宽度: ${borderWidth}px, 标题栏高度: ${titleBarHeight}px")

        // 调整窗口位置和大小以实现客户区目标
        finalX = info.realX - borderWidth
        finalY = info.realY - titleBarHeight
        finalWidth = width + borderWidth * 2
        finalHeight = height + titleBarHeight + borderWidth

        log("  实际窗口: 位置 ($finalX, $finalY), 大小 ${finalWidth}x${finalHeight}")


        // 等待一下让系统更新
        Thread.sleep(100)

        // 移动窗口
        val success = User32.INSTANCE.SetWindowPos(
            hwnd,
            null,
            finalX, finalY, finalWidth, finalHeight,
            WinUser.SWP_NOZORDER or WinUser.SWP_NOACTIVATE
        )
        // 等待一下让系统更新
        Thread.sleep(200)
        if (success) {
            log("✓ 窗口移动成功")
        } else {
            log("✗ 窗口移动失败")
        }
    }

    /**
     * 截取窗口图像（支持硬件加速窗口）
     *
     * @param hwnd 窗口句柄
     * @param useScreenCapture 是否使用屏幕截图方式（默认 false，优先使用 PrintWindow）
     * @return 窗口截图，失败返回 null
     */
    @Deprecated("废弃")
    fun captureWindow(hwnd: HWND, useScreenCapture: Boolean = false): BufferedImage? {
        return try {
            val info = getWindowInfo(hwnd)
            if(info.width != finalWidth || info.height != finalHeight || info.x != -borderWidth){
                setWindowSize(hwnd,1000,607)
            }
            if (useScreenCapture) {
                captureWindowByScreenshot(hwnd)
            } else {
                captureWindowByPrintWindow(hwnd) ?: captureWindowByScreenshot(hwnd)
            }
        } catch (e: Exception) {
            log("截图失败: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    /**
     * 使用 PrintWindow API 截取窗口（方法1 - 适用于大部分窗口）
     * 注意: 对于硬件加速窗口（如微信小程序），此方法可能返回黑屏，会自动降级到屏幕截图
     */
    private fun captureWindowByPrintWindow(hwnd: HWND): BufferedImage? {
        return try {
            log("captureWindowByPrintWindow start")
            val rect = RECT()
            User32.INSTANCE.GetWindowRect(hwnd, rect)
            val width = rect.right - rect.left
            val height = rect.bottom - rect.top

            if (width <= 0 || height <= 0) {
                log("窗口大小无效: ${width}x${height}")
                return null
            }
            log("captureWindowByPrintWindow getInfo")

            // 创建兼容DC和位图
            val hdcWindow = User32.INSTANCE.GetDC(hwnd)
            val hdcMem = GDI32.INSTANCE.CreateCompatibleDC(hdcWindow)
            val hBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdcWindow, width, height)
            val hOld = GDI32.INSTANCE.SelectObject(hdcMem, hBitmap)

            // 尝试 PrintWindow (包含 PW_RENDERFULLCONTENT 标志)
            val PW_RENDERFULLCONTENT = 0x00000002
            val result = User32.INSTANCE.PrintWindow(hwnd, hdcMem, PW_RENDERFULLCONTENT)

            val image = if (result) {
                try {
                    // 转换为 BufferedImage
                    val bmi = WinGDI.BITMAPINFO()
                    bmi.bmiHeader.biWidth = width
                    bmi.bmiHeader.biHeight = -height // 负数表示从上到下
                    bmi.bmiHeader.biPlanes = 1.toShort()
                    bmi.bmiHeader.biBitCount = 32.toShort()
                    bmi.bmiHeader.biCompression = WinGDI.BI_RGB

                    // 使用 JNA Memory 作为缓冲区
                    val bufferSize = (width * height * 4).toLong()
                    val memory = Memory(bufferSize)

                    val dibResult = GDI32.INSTANCE.GetDIBits(
                        hdcMem,
                        hBitmap,
                        0,
                        height,
                        memory,
                        bmi,
                        WinGDI.DIB_RGB_COLORS
                    )

                    if (dibResult == 0 || dibResult == WinGDI.BI_RGB) {
                        // GetDIBits 失败
                        log("GetDIBits 失败，降级到屏幕截图")
                        null
                    } else {
                        // 从 Memory 读取数据并创建 BufferedImage
                        val buffer = memory.getByteArray(0, bufferSize.toInt())
                        val img = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
                        var offset = 0
                        for (y in 0 until height) {
                            for (x in 0 until width) {
                                val b = buffer[offset++].toInt() and 0xFF
                                val g = buffer[offset++].toInt() and 0xFF
                                val r = buffer[offset++].toInt() and 0xFF
                                val a = buffer[offset++].toInt() and 0xFF
                                val argb = (a shl 24) or (r shl 16) or (g shl 8) or b
                                img.setRGB(x, y, argb)
                            }
                        }

                        log("✓ 使用 PrintWindow 截图成功: ${width}x${height}")
                        img.getSubImage(MRect.createWH(borderWidth,0,App.rectWindow.width,App.rectWindow.height))
                    }
                } catch (e: Exception) {
                    log("转换位图失败: ${e.message}, 降级到屏幕截图")
                    null
                }
            } else {
                log("PrintWindow 失败，尝试屏幕截图方式")
                null
            }

            // 清理资源
            GDI32.INSTANCE.SelectObject(hdcMem, hOld)
            GDI32.INSTANCE.DeleteObject(hBitmap)
            GDI32.INSTANCE.DeleteDC(hdcMem)
            User32.INSTANCE.ReleaseDC(hwnd, hdcWindow)

            image
        } catch (e: Exception) {
            log("PrintWindow 截图异常: ${e.message}")
            null
        }
    }

    /**
     * 使用屏幕截图方式（方法2 - 备用方案）
     */
    private fun captureWindowByScreenshot(hwnd: HWND): BufferedImage? {
        return try {
            val rect = RECT()
            User32.INSTANCE.GetWindowRect(hwnd, rect)

            val x = rect.left
            val y = rect.top
            val width = rect.right - rect.left
            val height = rect.bottom - rect.top

            if (width <= 0 || height <= 0) {
                log("窗口大小无效: ${width}x${height}")
                return null
            }

            // 使用 Robot 截取屏幕区域
            val robot = Robot()
            val image = robot.createScreenCapture(Rectangle(x, y, width, height))

            log("✓ 使用屏幕截图成功: ${width}x${height} at ($x, $y)")
            image
        } catch (e: Exception) {
            log("屏幕截图异常: ${e.message}")
            null
        }
    }


    /**
     * 获取客户区信息（内容区域，不包含边框）
     *
     * @return 包含客户区位置和大小的矩形，如果获取失败返回 null
     */
    fun getClientAreaBounds(hwnd: HWND): RECT? {
        return try {
            // 获取客户区矩形（相对于窗口左上角）
            val clientRect = RECT()
            User32.INSTANCE.GetClientRect(hwnd, clientRect)

            // 获取窗口矩形（屏幕坐标）
            val windowRect = RECT()
            User32.INSTANCE.GetWindowRect(hwnd, windowRect)

            // 计算客户区在屏幕上的位置
            // 客户区的左上角 = 窗口左上角 + 边框宽度
            val borderLeft = (windowRect.right - windowRect.left - clientRect.right) / 2
            val borderTop = windowRect.bottom - windowRect.top - clientRect.bottom - borderLeft

            val result = RECT()
            result.left = windowRect.left + borderLeft
            result.top = windowRect.top + borderTop
            result.right = result.left + clientRect.right
            result.bottom = result.top + clientRect.bottom

            result
        } catch (e: Exception) {
            log("获取客户区失败: ${e.message}")
            null
        }
    }

    /**
     * 截取客户区（内容区域，不包含边框和标题栏）
     */
    fun captureClientArea(hwnd: HWND): BufferedImage? {
        return try {
            val clientBounds = getClientAreaBounds(hwnd)
            if (clientBounds == null) {
                log("无法获取客户区边界")
                return null
            }

            val x = clientBounds.left
            val y = clientBounds.top
            val width = clientBounds.right - clientBounds.left
            val height = clientBounds.bottom - clientBounds.top

            if (width <= 0 || height <= 0) {
                log("客户区大小无效: ${width}x${height}")
                return null
            }

            // 使用 Robot 截取屏幕区域
            val robot = Robot()
            val image = robot.createScreenCapture(Rectangle(x, y, width, height))

            log("✓ 截取客户区成功: ${width}x${height} at ($x, $y)")
            image
        } catch (e: Exception) {
            log("截取客户区异常: ${e.message}")
            null
        }
    }

    /**
     * 获取窗口信息
     */
    private fun getWindowInfo(hwnd: HWND): WindowInfo {
        val title = getWindowTitle(hwnd)
        val rect = RECT()
        User32.INSTANCE.GetWindowRect(hwnd, rect)

        val realBounds = getWindowRealBounds(hwnd)
        val clientBounds = getClientAreaBounds(hwnd)

        return WindowInfo(
            hwnd = hwnd,
            title = title,
            x = rect.left,
            y = rect.top,
            width = rect.right - rect.left,
            height = rect.bottom - rect.top,
            realX = realBounds?.left ?: rect.left,
            realY = realBounds?.top ?: rect.top,
            realWidth = realBounds?.let { it.right - it.left } ?: (rect.right - rect.left),
            realHeight = realBounds?.let { it.bottom - it.top } ?: (rect.bottom - rect.top),
            clientX = clientBounds?.left ?: rect.left,
            clientY = clientBounds?.top ?: rect.top,
            clientWidth = clientBounds?.let { it.right - it.left } ?: (rect.right - rect.left),
            clientHeight = clientBounds?.let { it.bottom - it.top } ?: (rect.bottom - rect.top)
        )
    }

    /**
     * 窗口信息
     */
    data class WindowInfo(
        val hwnd: HWND,
        val title: String,
        val x: Int,
        val y: Int,
        val width: Int,
        val height: Int,
        val realX: Int,
        val realY: Int,
        val realWidth: Int,
        val realHeight: Int,
        val clientX: Int,
        val clientY: Int,
        val clientWidth: Int,
        val clientHeight: Int
    ) {
        /**
         * 判断是否有阴影（实际大小与窗口大小不一致）
         */
        fun hasShadow(): Boolean {
            val xDiff = Math.abs(x - realX)
            val yDiff = Math.abs(y - realY)
            val widthDiff = Math.abs(width - realWidth)
            val heightDiff = Math.abs(height - realHeight)

            return xDiff > 5 || yDiff > 5 || widthDiff > 5 || heightDiff > 5
        }

        /**
         * 获取边框宽度（左/右）
         */
        fun getBorderWidth(): Int {
            return (width - clientWidth) / 2
        }

        /**
         * 获取标题栏+上边框高度
         */
        fun getTitleBarHeight(): Int {
            return height - clientHeight - getBorderWidth()
        }

        override fun toString(): String {
            return """
                |窗口信息:
                |  标题: $title
                |  句柄: ${hwnd.pointer}
                |  外观位置: ($x, $y)
                |  外观大小: ${width}x${height}
                |  实际位置: ($realX, $realY)
                |  实际大小: ${realWidth}x${realHeight}
                |  客户区位置: ($clientX, $clientY)
                |  客户区大小: ${clientWidth}x${clientHeight}
                |  边框宽度: ${getBorderWidth()}px
                |  标题栏高度: ${getTitleBarHeight()}px
                |  有阴影: ${if (hasShadow()) "是" else "否"}
            """.trimMargin()
        }
    }
}

/**
 * DWM API 接口
 */
interface DwmApi : com.sun.jna.Library {
    fun DwmSetWindowAttribute(
        hwnd: HWND,
        dwAttribute: Int,
        pvAttribute: Pointer,
        cbAttribute: Int
    ): Int

    fun DwmGetWindowAttribute(
        hwnd: HWND,
        dwAttribute: Int,
        pvAttribute: Pointer,
        cbAttribute: Int
    ): Int

    fun DwmExtendFrameIntoClientArea(
        hwnd: HWND,
        margins: MARGINS
    ): Int
}

private operator fun String.times(count: Int) = repeat(count)