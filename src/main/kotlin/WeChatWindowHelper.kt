import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinDef.RECT
import com.sun.jna.platform.win32.WinUser
import com.sun.jna.ptr.IntByReference

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
     * 移除窗口阴影
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
     */
    fun moveWindow(
        hwnd: HWND,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        removeShadow: Boolean = true
    ) {
        println("移动窗口: ${getWindowTitle(hwnd)}")
        println("  位置: ($x, $y)")
        println("  大小: ${width}x${height}")

        // 移除阴影
        if (removeShadow) {
            removeWindowShadow(hwnd)
        }

        // 移动窗口
        val success = User32.INSTANCE.SetWindowPos(
            hwnd,
            null,
            x, y, width, height,
            WinUser.SWP_NOZORDER or WinUser.SWP_NOACTIVATE
        )

        if (success) {
            println("✓ 窗口移动成功")

            // 验证实际位置
            val rect = RECT()
            User32.INSTANCE.GetWindowRect(hwnd, rect)
            println("  实际位置: (${rect.left}, ${rect.top})")
            println("  实际大小: ${rect.right - rect.left}x${rect.bottom - rect.top}")
        } else {
            println("✗ 窗口移动失败")
        }
    }

    fun removeWindowBorderCompletely(hwnd: HWND) {
        // 1. 禁用 DWM 渲染（移除阴影）
        val policy = IntByReference(1) // DWMNCRP_DISABLED
        dwmapi!!.DwmSetWindowAttribute(hwnd, 2, policy.pointer, 4)

        // 2. 移除导致阴影占位的扩展样式
        val exStyle = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE)
        val newExStyle = exStyle and (
                WinUser.WS_EX_COMPOSITED or   // 移除合成样式
                        WinUser.WS_EX_LAYERED          // 移除分层窗口
                ).inv()
        User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, newExStyle)

        // 3. 修改窗口样式
        val style = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_STYLE)
        val newStyle = style and WinUser.WS_THICKFRAME.inv() // 移除可调整边框
        User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_STYLE, newStyle)

        // 4. 强制刷新窗口
        User32.INSTANCE.SetWindowPos(
            hwnd, null, 0, 0, 0, 0,
            WinUser.SWP_NOMOVE or WinUser.SWP_NOSIZE or
                    WinUser.SWP_NOZORDER or WinUser.SWP_FRAMECHANGED
        )
    }



    /**
     * 获取窗口信息
     */
    fun getWindowInfo(hwnd: HWND): WindowInfo {
        val title = getWindowTitle(hwnd)
        val rect = RECT()
        User32.INSTANCE.GetWindowRect(hwnd, rect)

        val realBounds = getWindowRealBounds(hwnd)

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
            realHeight = realBounds?.let { it.bottom - it.top } ?: (rect.bottom - rect.top)
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
        val realHeight: Int
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

        override fun toString(): String {
            return """
                |窗口信息:
                |  标题: $title
                |  句柄: ${hwnd.pointer}
                |  外观位置: ($x, $y)
                |  外观大小: ${width}x${height}
                |  实际位置: ($realX, $realY)
                |  实际大小: ${realWidth}x${realHeight}
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