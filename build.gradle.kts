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
        implementation("org.projectlombok:lombok:1.18.24") // 使用最新版本
//        annotationProcessor 'org.projectlombok:lombok:1.18.24' // 使用最新版本

}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "16"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "塔防助手2"
            packageVersion = "1.0.0"
        }
    }
}