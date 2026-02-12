package com.skysoul.pdftool

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
import java.awt.image.BufferedImage
import java.awt.Robot
import java.awt.Rectangle

/**
 * MARGINS 结构 - 用于 DwmExtendFrameIntoClientArea
 */
@Structure.FieldOrder("cxLeftWidth", "cxRightWidth", "cyTopHeight", "cyBottomHeight")
class MARGINS : Structure() {
    @JvmField var cxLeftWidth: Int = 0
    @JvmField var cxRightWidth: Int = 0
    @JvmField var cyTopHeight: Int = 0
    @JvmField var cyBottomHeight: Int = 0
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

    // 加载 dwmapi.dll
    private val dwmapi by lazy {
        try {
            Native.load("dwmapi", DwmApi::class.java)
        } catch (e: Exception) {
            println("警告: 无法加载 dwmapi.dll, 某些功能可能不可用")
            null
        }
    }

    /**
     * 查找微信小程序窗口
     *
     * @param title 窗口标题（部分匹配）
     * @return 窗口句柄，未找到返回 null
     */
    fun findWeChatWindow(title: String? = null): HWND? {
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
    fun getWindowTitle(hwnd: HWND): String {
        val buffer = CharArray(512)
        User32.INSTANCE.GetWindowText(hwnd, buffer, buffer.size)
        return String(buffer).trim('\u0000')
    }

    /**
     * 移除窗口阴影（视觉效果）
     *
     * @param hwnd 窗口句柄
     * @return 是否成功
     */
    fun removeWindowShadow(hwnd: HWND): Boolean {
        return try {
            if (dwmapi == null) {
                println("DWM API 不可用，尝试使用样式方法")
                return removeWindowShadowByStyle(hwnd)
            }

            // 禁用 DWM 非客户区渲染（移除阴影）
            val policy = IntByReference(DWMNCRP_DISABLED)
            val result = dwmapi!!.DwmSetWindowAttribute(
                hwnd,
                DWMWA_NCRENDERING_POLICY,
                policy.pointer,
                4
            )

            if (result == 0) {
                println("✓ 成功移除窗口阴影")
                true
            } else {
                println("✗ 移除窗口阴影失败，错误码: $result")
                false
            }
        } catch (e: Exception) {
            println("移除阴影异常: ${e.message}")
            false
        }
    }

    /**
     * 彻底移除窗口阴影和占位空间
     * 使窗口真实大小 = 设置的大小（无任何阴影占位）
     *
     * @param hwnd 窗口句柄
     * @return 是否成功
     */
    fun removeWindowShadowCompletely(hwnd: HWND): Boolean {
        return try {
            if (dwmapi == null) {
                println("DWM API 不可用")
                return false
            }

            println("开始彻底移除窗口阴影和占位...")

            // 1. 禁用 DWM 阴影渲染
            val policy = IntByReference(DWMNCRP_DISABLED)
            val result1 = dwmapi!!.DwmSetWindowAttribute(
                hwnd,
                DWMWA_NCRENDERING_POLICY,
                policy.pointer,
                4
            )
            if (result1 == 0) {
                println("  ✓ 已禁用 DWM 阴影渲染")
            } else {
                println("  ✗ 禁用渲染失败，错误码: $result1")
            }

            // 2. 设置零边距（关键！移除阴影占位空间）
            val margins = MARGINS().apply {
                cxLeftWidth = 0
                cxRightWidth = 0
                cyTopHeight = 0
                cyBottomHeight = 0
            }
            val result2 = dwmapi!!.DwmExtendFrameIntoClientArea(hwnd, margins)
            if (result2 == 0) {
                println("  ✓ 已设置零边距，移除阴影占位")
            } else {
                println("  ✗ 设置边距失败，错误码: $result2")
            }

            // 3. 移除扩展样式
            val exStyle = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE)
            val newExStyle = exStyle and (WinUser.WS_EX_LAYERED or 0x02000000).inv() // WS_EX_COMPOSITED
            User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, newExStyle)
            println("  ✓ 已移除扩展样式")

            // 4. 强制刷新窗口
            User32.INSTANCE.SetWindowPos(
                hwnd, null, 0, 0, 0, 0,
                WinUser.SWP_NOMOVE or WinUser.SWP_NOSIZE or
                        WinUser.SWP_NOZORDER or WinUser.SWP_FRAMECHANGED
            )
            println("  ✓ 已刷新窗口")

            println("✓ 窗口阴影和占位已彻底移除")
            true
        } catch (e: Exception) {
            println("彻底移除阴影异常: ${e.message}")
            false
        }
    }

    /**
     * 通过修改窗口样式移除阴影（备用方法）
     */
    private fun removeWindowShadowByStyle(hwnd: HWND): Boolean {
        return try {
            // 获取当前扩展样式
            val exStyle = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE)

            // 移除可能导致阴影的样式
            val newExStyle = exStyle and WinUser.WS_EX_LAYERED.inv()

            // 设置新样式
            User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, newExStyle)

            // 强制刷新窗口
            User32.INSTANCE.SetWindowPos(
                hwnd, null, 0, 0, 0, 0,
                WinUser.SWP_NOMOVE or WinUser.SWP_NOSIZE or
                        WinUser.SWP_NOZORDER or WinUser.SWP_FRAMECHANGED
            )

            println("✓ 通过样式修改移除阴影")
            true
        } catch (e: Exception) {
            println("样式修改失败: ${e.message}")
            false
        }
    }

    /**
     * 获取窗口实际边界（不包含阴影）
     *
     * @param hwnd 窗口句柄
     * @return 窗口矩形，失败返回 null
     */
    fun getWindowRealBounds(hwnd: HWND): RECT? {
        return try {
            if (dwmapi == null) {
                println("DWM API 不可用，返回普通窗口矩形")
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
            println("获取窗口边界失败: ${e.message}")
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
    fun moveWindow(
        hwnd: HWND,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        removeShadow: Boolean = true,
        removeCompletely: Boolean = false,
        useClientArea: Boolean = false
    ) {
        println("移动窗口: ${getWindowTitle(hwnd)}")

        // 如果使用客户区尺寸，需要先获取当前边框信息
        var finalX = x
        var finalY = y
        var finalWidth = width
        var finalHeight = height

        if (useClientArea) {
            println("  客户区模式: 目标位置 ($x, $y), 目标大小 ${width}x${height}")

            // 获取当前窗口信息以计算边框
            val info = getWindowInfo(hwnd)
            val borderWidth = info.getBorderWidth()
            val titleBarHeight = info.getTitleBarHeight()

            println("  边框宽度: ${borderWidth}px, 标题栏高度: ${titleBarHeight}px")

            // 调整窗口位置和大小以实现客户区目标
            finalX = x - borderWidth
            finalY = y - titleBarHeight
            finalWidth = width + borderWidth * 2
            finalHeight = height + titleBarHeight + borderWidth

            println("  实际窗口: 位置 ($finalX, $finalY), 大小 ${finalWidth}x${finalHeight}")
        } else {
            println("  位置: ($x, $y)")
            println("  大小: ${width}x${height}")
        }

        println("  彻底移除: $removeCompletely")

        // 移除阴影
        if (removeShadow) {
            if (removeCompletely) {
                removeWindowShadowCompletely(hwnd)
            } else {
                removeWindowShadow(hwnd)
            }
        }

        // 等待一下让系统更新
        Thread.sleep(100)

        // 移动窗口
        val success = User32.INSTANCE.SetWindowPos(
            hwnd,
            null,
            finalX, finalY, finalWidth, finalHeight,
            WinUser.SWP_NOZORDER or WinUser.SWP_NOACTIVATE
        )

        if (success) {
            println("✓ 窗口移动成功")

            // 等待窗口更新
            Thread.sleep(100)

            if (useClientArea) {
                // 客户区模式：验证客户区位置和大小
                val updatedInfo = getWindowInfo(hwnd)
                println("  窗口位置: (${updatedInfo.x}, ${updatedInfo.y})")
                println("  窗口大小: ${updatedInfo.width}x${updatedInfo.height}")
                println("  客户区位置: (${updatedInfo.clientX}, ${updatedInfo.clientY})")
                println("  客户区大小: ${updatedInfo.clientWidth}x${updatedInfo.clientHeight}")

                val posMatch = updatedInfo.clientX == x && updatedInfo.clientY == y
                val sizeMatch = updatedInfo.clientWidth == width && updatedInfo.clientHeight == height

                if (posMatch && sizeMatch) {
                    println("✓ 客户区位置和大小完全匹配！")
                } else {
                    if (!posMatch) {
                        println("⚠ 客户区位置偏差: X ${updatedInfo.clientX - x}px, Y ${updatedInfo.clientY - y}px")
                    }
                    if (!sizeMatch) {
                        println("⚠ 客户区大小偏差: 宽 ${updatedInfo.clientWidth - width}px, 高 ${updatedInfo.clientHeight - height}px")
                    }
                }
            } else {
                // 窗口模式：验证窗口位置和大小
                val rect = RECT()
                User32.INSTANCE.GetWindowRect(hwnd, rect)
                val actualX = rect.left
                val actualY = rect.top
                val actualWidth = rect.right - rect.left
                val actualHeight = rect.bottom - rect.top

                println("  实际位置: ($actualX, $actualY)")
                println("  实际大小: ${actualWidth}x${actualHeight}")

                val posMatch = actualX == finalX && actualY == finalY
                val sizeMatch = actualWidth == finalWidth && actualHeight == finalHeight

                if (posMatch && sizeMatch) {
                    println("✓ 窗口位置和大小完全匹配！")
                } else {
                    if (!posMatch) {
                        println("⚠ 位置偏差: X ${actualX - finalX}px, Y ${actualY - finalY}px")
                    }
                    if (!sizeMatch) {
                        println("⚠ 大小偏差: 宽 ${actualWidth - finalWidth}px, 高 ${actualHeight - finalHeight}px")
                    }
                }
            }
        } else {
            println("✗ 窗口移动失败")
        }
    }

    /**
     * 截取窗口图像（支持硬件加速窗口）
     *
     * @param hwnd 窗口句柄
     * @param useScreenCapture 是否使用屏幕截图方式（默认 false，优先使用 PrintWindow）
     * @return 窗口截图，失败返回 null
     */
    fun captureWindow(hwnd: HWND, useScreenCapture: Boolean = false): BufferedImage? {
        return try {
            if (useScreenCapture) {
                captureWindowByScreenshot(hwnd)
            } else {
                captureWindowByPrintWindow(hwnd) ?: captureWindowByScreenshot(hwnd)
            }
        } catch (e: Exception) {
            println("截图失败: ${e.message}")
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
            val rect = RECT()
            User32.INSTANCE.GetWindowRect(hwnd, rect)
            val width = rect.right - rect.left
            val height = rect.bottom - rect.top

            if (width <= 0 || height <= 0) {
                println("窗口大小无效: ${width}x${height}")
                return null
            }

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
                        println("GetDIBits 失败，降级到屏幕截图")
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

                        println("✓ 使用 PrintWindow 截图成功: ${width}x${height}")
                        img
                    }
                } catch (e: Exception) {
                    println("转换位图失败: ${e.message}, 降级到屏幕截图")
                    null
                }
            } else {
                println("PrintWindow 失败，尝试屏幕截图方式")
                null
            }

            // 清理资源
            GDI32.INSTANCE.SelectObject(hdcMem, hOld)
            GDI32.INSTANCE.DeleteObject(hBitmap)
            GDI32.INSTANCE.DeleteDC(hdcMem)
            User32.INSTANCE.ReleaseDC(hwnd, hdcWindow)

            image
        } catch (e: Exception) {
            println("PrintWindow 截图异常: ${e.message}")
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
                println("窗口大小无效: ${width}x${height}")
                return null
            }

            // 使用 Robot 截取屏幕区域
            val robot = Robot()
            val image = robot.createScreenCapture(Rectangle(x, y, width, height))

            println("✓ 使用屏幕截图成功: ${width}x${height} at ($x, $y)")
            image
        } catch (e: Exception) {
            println("屏幕截图异常: ${e.message}")
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
            println("获取客户区失败: ${e.message}")
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
                println("无法获取客户区边界")
                return null
            }

            val x = clientBounds.left
            val y = clientBounds.top
            val width = clientBounds.right - clientBounds.left
            val height = clientBounds.bottom - clientBounds.top

            if (width <= 0 || height <= 0) {
                println("客户区大小无效: ${width}x${height}")
                return null
            }

            // 使用 Robot 截取屏幕区域
            val robot = Robot()
            val image = robot.createScreenCapture(Rectangle(x, y, width, height))

            println("✓ 截取客户区成功: ${width}x${height} at ($x, $y)")
            image
        } catch (e: Exception) {
            println("截取客户区异常: ${e.message}")
            null
        }
    }

    /**
     * 获取窗口信息
     */
    fun getWindowInfo(hwnd: HWND): WindowInfo {
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
     * 监控微信窗口（检测阴影重新出现）
     */
    fun monitorAndFixShadow(title: String, intervalMs: Long = 1000) {
        println("开始监控微信窗口: $title")
        println("检测间隔: ${intervalMs}ms")

        var lastHwnd: HWND? = null

        while (true) {
            try {
                val hwnd = findWeChatWindow(title)

                if (hwnd != null) {
                    // 检测是否是新窗口（重启后）
                    if (hwnd != lastHwnd) {
                        println("\n检测到窗口变化，重新移除阴影")
                        removeWindowShadow(hwnd)
                        lastHwnd = hwnd
                    }

                    // 定期检查并移除阴影
                    val info = getWindowInfo(hwnd)
                    if (info.hasShadow()) {
                        println("\n检测到阴影重新出现，移除中...")
                        removeWindowShadow(hwnd)
                    }
                }

                Thread.sleep(intervalMs)
            } catch (e: InterruptedException) {
                println("监控已停止")
                break
            } catch (e: Exception) {
                println("监控异常: ${e.message}")
            }
        }
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

/**
 * 主函数示例
 */
fun main() {
    println("=" * 60)
    println("微信小程序窗口助手")
    println("=" * 60)
    println()

    // 查找微信窗口
    print("请输入窗口标题（部分匹配，留空查找所有微信窗口）: ")
    val title = readLine()?.takeIf { it.isNotBlank() }

    val hwnd = WeChatWindowHelper.findWeChatWindow(title)

    if (hwnd == null) {
        println("未找到匹配的窗口")
        return
    }

    // 显示窗口信息
    val info = WeChatWindowHelper.getWindowInfo(hwnd)
    println(info)
    println()

    // 选择操作
    println("请选择操作:")
    println("1. 移除阴影")
    println("2. 移动窗口（移除阴影）")
    println("3. 监控窗口（自动移除阴影）")
    print("请选择 (1-3): ")

    when (readLine()) {
        "1" -> {
            WeChatWindowHelper.removeWindowShadow(hwnd)
        }
        "2" -> {
            print("X 坐标 (默认 0): ")
            val x = readLine()?.toIntOrNull() ?: 0
            print("Y 坐标 (默认 0): ")
            val y = readLine()?.toIntOrNull() ?: 0
            print("宽度 (默认 1000): ")
            val width = readLine()?.toIntOrNull() ?: 1000
            print("高度 (默认 670): ")
            val height = readLine()?.toIntOrNull() ?: 670

            WeChatWindowHelper.moveWindow(hwnd, x, y, width, height)
        }
        "3" -> {
            val windowTitle = WeChatWindowHelper.getWindowTitle(hwnd)
            WeChatWindowHelper.monitorAndFixShadow(windowTitle)
        }
    }
}

private operator fun String.times(count: Int) = repeat(count)