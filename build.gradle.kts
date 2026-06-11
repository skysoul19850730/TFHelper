import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.0.1"
}

group = "me.administrator"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
    implementation(compose.desktop.currentOs)
//    implementation("org.bytedeco:javacv:1.5.7")
//    implementation("org.bytedeco:leptonica-platform:1.82.0-1.5.7")
//    implementation("org.bytedeco:tesseract-platform:5.0.1-1.5.7")
    implementation("net.java.dev.jna:jna-platform:5.13.0")
    implementation("net.sourceforge.tess4j:tess4j:5.7.0")
    implementation("com.android.tools.ddms:ddmlib:26.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")

    implementation("org.apache.poi:poi-ooxml:5.2.3")

    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-io:commons-io:2.19.0")
    implementation("commons-codec:commons-codec:1.18.0")
    implementation("org.openpnp:opencv:4.6.0-0")
    implementation("com.alibaba:fastjson:1.2.76")
    implementation("com.google.code.gson:gson:2.10.1")
        implementation("org.projectlombok:lombok:1.18.24") // 使用最新版本
//        annotationProcessor 'org.projectlombok:lombok:1.18.24' // 使用最新版本
    implementation("org.usb4java:usb4java:1.3.0")


}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "16"
}

// 打包前把 src/main/resources 复制到 build/appResources/common/
val aaprepareAppResources by tasks.registering(Copy::class) {
    from("src/main/resources")
    into(layout.buildDirectory.dir("appResources/common"))
}

tasks.named("aaprepareAppResources") {
    // 确保资源先编译
}
// 打包前把 src/main/resources 复制到 build/appResources/common/
val aaprepareAppResources2 by tasks.registering(Copy::class) {

    from("tools")  // 你保存的位置
    into(layout.buildDirectory.dir("wixToolset"))
}

tasks.named("aaprepareAppResources2") {
    // 确保资源先编译
}
//
//// 把本地 WiX 复制到 build/wixtoolset，避免重新下载
//val prepareWix by tasks.registering(Copy::class) {
//    from("tools/wix311.zip")  // 你保存的位置
//    into(layout.buildDirectory.dir("build/wixToolset/wix311.zip"))
//}
//
//afterEvaluate {
//    tasks.findByName("aaprepareAppResources")?.dependsOn(prepareWix)
//}


compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "TFHelper2"
            packageVersion = "1.0.0"

            // 包含所有 JVM 模块，确保不会缺少
            includeAllModules = true

            javaHome="C:\\Users\\Administrator\\.jdks\\ms-17.0.19"

            // 指定资源根目录
            appResourcesRootDir.set(layout.buildDirectory.dir("appResources"))

            // 可选：配置 Windows 特有选项
            windows {
                menuGroup = "塔防助手2"
                shortcut = true
                dirChooser = true

                // 指定本地 WiX 路径
//                wixToolsetPath = file("C:\\Users\\Administrator\\IdeaProjects\\intellij-sdk-code-samples\\untitled1\\tools\\wix311")
            }
        }
    }
}

// 让打包任务依赖资源复制
afterEvaluate {
    tasks.findByName("createDistributable")?.dependsOn(aaprepareAppResources)
    tasks.findByName("packageMsi")?.dependsOn(aaprepareAppResources)
    tasks.findByName("createDistributable")?.dependsOn(aaprepareAppResources2)
    tasks.findByName("packageMsi")?.dependsOn(aaprepareAppResources2)
}