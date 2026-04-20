import com.android.build.api.dsl.LibraryExtension

plugins {
    alias(libs.plugins.android.library)
}

extensions.configure<LibraryExtension> {
    namespace = "com.owo233.qqinterface"
    compileSdk = 36

    sourceSets {
        val main by getting
        main.apply {
            manifest.srcFile("src/main/AndroidManifest.xml")
            java.directories += "src/main/java"
        }
    }

    defaultConfig {
        minSdk = 27
        lint.targetSdk = 36
        buildToolsVersion = "36.1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    compileOnly(libs.androidx.core.ktx)
}
