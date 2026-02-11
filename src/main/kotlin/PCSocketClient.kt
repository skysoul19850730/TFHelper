import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.net.Socket
import javax.imageio.ImageIO
import kotlin.concurrent.thread

/**
 * PC端完整Socket客户端 (Kotlin版本)
 * 支持：单次截图、连续传输、双向通信
 *
 * 使用方法：
 * 1. Android 端启动服务器: MSocketManager.getInstance().startServer()
 * 2. 执行 ADB 端口转发: adb forward tcp:8888 tcp:8888
 * 3. 运行此程序连接到 localhost:8888
 * 4. 发送命令控制 Android
 */
object PCSocketClient {

    private const val HOST = "127.0.0.1"  // 使用 ADB 转发，连接本地
    private const val PORT = 8888
    private var imageCount = 0

    // 命令常量（与 Android 端保持一致）
    private const val CMD_SCREENSHOT = "SCREENSHOT"
    private const val CMD_START_STREAM = "START_STREAM"
    private const val CMD_STOP_STREAM = "STOP_STREAM"
    private const val CMD_PING = "PING"

    @JvmStatic
    fun main(args: Array<String>) {
        println("=== PC端Socket客户端 (Kotlin) ===")
        println()
        println("使用前请确保：")
        println("1. Android 端已启动服务器")
        println("2. 已执行: adb forward tcp:8888 tcp:8888")
        println()

        try {
            println("正在连接到 $HOST:$PORT...")
            Socket(HOST, PORT).use { socket ->
                println("✓ 连接成功！")
                println()

                val output = DataOutputStream(socket.getOutputStream())
                val input = DataInputStream(socket.getInputStream())

                // 启动接收线程
                startReceiveThread(input)

                // 交互式命令行
                showMenu()
                while (true) {
                    print("\n请输入命令 (h=帮助): ")
                    val command = readLine()?.trim() ?: continue

                    when (command.lowercase()) {
                        "1", "s" -> {
                            println("→ 请求单次截图...")
                            sendCommand(CMD_SCREENSHOT, output)
                        }
                        "2", "start" -> {
                            println("→ 开始连续传输...")
                            sendCommand(CMD_START_STREAM, output)
                        }
                        "3", "stop" -> {
                            println("→ 停止连续传输...")
                            sendCommand(CMD_STOP_STREAM, output)
                        }
                        "4", "ping" -> {
                            println("→ 发送心跳...")
                            sendCommand(CMD_PING, output)
                        }
                        "h", "help" -> {
                            showMenu()
                        }
                        "q", "quit", "exit" -> {
                            println("退出程序")
                            break
                        }
                        else -> {
                            println("未知命令，输入 h 查看帮助")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            println("✗ 连接失败: ${e.message}")
            println()
            println("请检查：")
            println("1. Android 端是否已启动服务器")
            println("2. 是否执行了 adb forward tcp:8888 tcp:8888")
            println("3. ADB 是否正常连接")
        }
    }

    private fun showMenu() {
        println("==================== 命令菜单 ====================")
        println("1 或 s      - 请求单次截图")
        println("2 或 start  - 开始连续传输（实时屏幕镜像）")
        println("3 或 stop   - 停止连续传输")
        println("4 或 ping   - 发送心跳测试")
        println("h 或 help   - 显示此帮助")
        println("q 或 quit   - 退出程序")
        println("================================================")
    }

    private fun sendCommand(command: String, output: DataOutputStream) {
        try {
            val cmdBytes = command.toByteArray()
            output.writeInt(cmdBytes.size)
            output.write(cmdBytes)
            output.flush()
        } catch (e: Exception) {
            println("✗ 发送命令失败: ${e.message}")
        }
    }

    private fun startReceiveThread(input: DataInputStream) {
        thread(name = "ReceiveThread", isDaemon = true) {
            try {
                while (true) {
                    try {
                        // 读取数据长度
                        val length = input.readInt()

                        // 判断是命令还是图片
                        if (length < 1024) {
                            // 可能是命令
                            val data = ByteArray(length)
                            input.readFully(data)
                            val message = String(data)
                            if (message == "PONG") {
                                println("← 收到心跳响应: PONG")
                            } else {
                                println("← 收到消息: $message")
                            }
                        } else {
                            // 是图片数据
                            println("← 接收图片，大小: ${length / 1024} KB")

                            val imageData = ByteArray(length)
                            input.readFully(imageData)

                            // 保存图片
                            imageCount++
                            val filename = "screenshot_$imageCount.jpg"
                            File(filename).writeBytes(imageData)
                            println("✓ 图片已保存: $filename")

                            // 可选：显示图片信息
                            try {
                                val image = ImageIO.read(ByteArrayInputStream(imageData))
                                println("  尺寸: ${image.width} x ${image.height}")
                            } catch (e: Exception) {
                                // 忽略
                            }
                        }
                    } catch (e: Exception) {
                        println("✗ 接收数据出错: ${e.message}")
                        break
                    }
                }
            } catch (e: Exception) {
                println("接收线程退出: ${e.message}")
            }
        }
    }
}