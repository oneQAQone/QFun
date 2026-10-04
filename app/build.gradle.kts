@file:Suppress("UnstableApiUsage")

import com.android.build.api.dsl.ApplicationExtension
import java.io.FileInputStream
import java.util.Properties
import java.util.UUID

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

val keystorePropertiesFile = rootProject.file("local.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

val modVersionCode = 26
val modVersionName = "1.3.4"

val zygiskOutputDir = layout.buildDirectory.dir("generated/zygisk-resources")

val processZygiskTemplate = tasks.register<Sync>("processZygiskTemplate") {
    description = ""
    from(file("src/main/zygisk-template"))
    into(zygiskOutputDir)
    filteringCharset = "UTF-8"
    filesMatching("module.prop") {
        expand(
            mapOf(
                "version" to modVersionName,
                "versionName" to modVersionName,
                "versionCode" to modVersionCode,
                "uuid" to UUID.randomUUID().toString()
            )
        )
    }
}

tasks.named("preBuild").configure {
    dependsOn(processZygiskTemplate)
}

extensions.configure<ApplicationExtension> {
    namespace = "me.yxp.qfun"
    ndkVersion = "30.0.16248370"
    compileSdk = 37

    defaultConfig {
        applicationId = "me.yxp.qfun"
        minSdk = 26
        targetSdk = 37
        versionCode = modVersionCode
        versionName = modVersionName

        ndk {
            abiFilters.add("arm64-v8a")
            abiFilters.add("armeabi-v7a")
        }

        externalNativeBuild {
            cmake {
                arguments += listOf(
                    "-DANDROID_STL=none",
                    "-DLSPLANT_STANDALONE=ON",
                    "-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON"
                )
            }
        }
    }

    sourceSets {
        named("main") {
            resources.srcDir(zygiskOutputDir.get().asFile)
        }
    }

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties.getProperty("keyAlias") ?: ""
            keyPassword = keystoreProperties.getProperty("keyPassword") ?: ""
            storePassword = keystoreProperties.getProperty("storePassword") ?: ""
            val storeFileName = keystoreProperties.getProperty("storeFile") ?: ""
            if (storeFileName.isNotEmpty()) {
                storeFile = file(storeFileName)
            }
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
        prefab = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.28.0+"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    androidResources {
        additionalParameters += listOf(
            "--allow-reserved-package-id",
            "--package-id",
            "0x44",
        )
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF*.proto"
            )
            pickFirsts += setOf(
                "META-INF/xposed/**",
                "META-INF/services/**",
                "customize.sh",
                "action.sh",
                "uninstall.sh",
                "module.prop",
                "webroot/**",
                "META-INF/com/google/android/update-binary",
                "META-INF/com/google/android/updater-script"
            )
        }
    }
}

dependencies {
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.dalvik.dx)
    implementation(libs.libxposed.service)
    implementation(projects.annotation)
    implementation(libs.dexkit)
    implementation(libs.protobuf.java)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.activity)
    implementation(libs.compose.animation)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.kyant0.backdrop)
    implementation(libs.kyant0.shapes)

    implementation(libs.dobby)
    implementation(libs.libcxx)

    ksp(projects.processor)

    compileOnly(libs.libxposed.api)
    compileOnly(libs.xposed)
    compileOnly(projects.qqinterface)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.36.1"
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}

val adb: String = androidComponents.sdkComponents.adb.get().asFile.absolutePath
val packageName = "com.tencent.mobileqq"
// adb shell am force-stop com.tencent.mobileqq
val killQQ = tasks.register<Exec>("killQQ") {
    description = ""
    group = "qfun"
    commandLine(adb, "shell", "am", "force-stop", packageName)
    isIgnoreExitValue = true
}
