package me.yxp.qfun.utils.log

import me.yxp.qfun.utils.io.FileUtils
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.qq.QQCurrentEnv
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CrashAnalysisResult(
    val zipFile: File,
    val blamedModule: String,
    val exceptionType: String,
    val summary: String,
    val stackTrace: String
)

object CrashReporter {

    private val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    private val moduleNameCache = mutableMapOf<String, String>()

    fun generateReport(t: Thread, e: Throwable): CrashAnalysisResult {
        val baseDir = try {
            QQCurrentEnv.currentDir
        } catch (_: Exception) {
            if (HostInfo.moduleDataPath.isNotEmpty()) {
                HostInfo.moduleDataPath + "global/"
            } else {
                "/storage/emulated/0/Android/data/${HostInfo.packageName}/QFun/global/"
            }
        }

        val crashRootDir = File(baseDir, "crash")
        val timestamp = dateFormat.format(Date()) + "_" + System.currentTimeMillis() % 1000
        val tempDir = File(crashRootDir, "temp_$timestamp")
        val finalZipFile = File(crashRootDir, "crash_$timestamp.zip")

        FileUtils.ensureDir(tempDir)

        val stackTraceStr = getStackTraceString(e)
        val (blamedModule, analysisReport) = analyzeStackTrace(e)

        try {
            writeFile(tempDir, "crash_hit_flag", "1")
            writeFile(tempDir, "summary", "${e.javaClass.name}: ${e.message}")
            writeFile(tempDir, "java_trace", "Thread:${t.name}\n$stackTraceStr")
            writeFile(tempDir, "java_trace_analysis", analysisReport)
            writeFile(tempDir, "trace_module", "Blamed Module: $blamedModule")

            writeFile(tempDir, "logcat", getLogcat())

            val envInfo = LogUtils.getEnvironmentInfo()
            writeFile(tempDir, "env_info", envInfo)

            writeJavaThreads(tempDir)
            copySystemFile("/proc/self/maps", File(tempDir, "maps"))
            copySystemFile("/proc/self/status", File(tempDir, "status"))

            if (FileUtils.zip(tempDir, finalZipFile)) {
                FileUtils.delete(tempDir)
            } else {
                finalZipFile.delete()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        val resultFile = if (finalZipFile.exists()) finalZipFile else tempDir

        return CrashAnalysisResult(
            zipFile = resultFile,
            blamedModule = blamedModule,
            exceptionType = e.javaClass.simpleName,
            summary = e.message ?: "No error message",
            stackTrace = stackTraceStr
        )
    }

    private fun getLogcat(): String {
        return try {
            val process =
                Runtime.getRuntime().exec(arrayOf("logcat", "-d", "-v", "threadtime", "-t", "3000"))
            process.inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            "Logcat capture failed: ${e.message}"
        }
    }

    private fun analyzeStackTrace(e: Throwable): Pair<String, String> {
        val sbAnalysis = StringBuilder()
        sbAnalysis.append("----Traces Analysis-----\n\n")

        var blamedModule = "Unknown"
        val stackTrace = e.stackTrace

        for (element in stackTrace) {
            val className = element.className
            val clazz = try {
                val ctxLoader = Thread.currentThread().contextClassLoader
                Class.forName(className, false, ctxLoader)
            } catch (_: ClassNotFoundException) {
                null
            }

            val classLoader = clazz?.classLoader
            val loaderStr = classLoader?.toString() ?: "BootClassLoader/Unknown"

            sbAnalysis.append("at $className.${element.methodName}(${element.fileName}:${element.lineNumber})\n")
            sbAnalysis.append("   Loader: $loaderStr\n")

            if (blamedModule == "Unknown" && classLoader != null) {
                if (className.startsWith("me.yxp.qfun")) {
                    blamedModule = "QFun"
                } else if (loaderStr.contains("/data/app/") && !loaderStr.contains(HostInfo.packageName)) {
                    blamedModule = resolveModuleNameFromLoader(loaderStr)
                }
            }
        }

        if (blamedModule == "Unknown") {
            blamedModule = HostInfo.hostName
        }

        return Pair(blamedModule, sbAnalysis.toString())
    }

    private fun resolveModuleNameFromLoader(loaderStr: String): String {
        val regex = Regex("/data/app/(?:~~[^/]+/)?([^/-]+)-")
        val match = regex.find(loaderStr)
        val packageName = match?.groupValues?.get(1)

        if (packageName != null) {
            if (moduleNameCache.containsKey(packageName)) {
                return moduleNameCache[packageName]!!
            }
            val appName = getAppName(packageName)
            moduleNameCache[packageName] = appName
            return appName
        }
        return "Unknown Module"
    }

    private fun getAppName(packageName: String): String {
        return try {
            val pm = HostInfo.hostContext.packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            val label = pm.getApplicationLabel(appInfo).toString()
            label.ifEmpty { packageName }
        } catch (_: Exception) {
            packageName
        }
    }

    private fun writeJavaThreads(dir: File) {
        val sb = StringBuilder()
        val allStackTraces = Thread.getAllStackTraces()
        for ((thread, stack) in allStackTraces) {
            sb.append("Thread:${thread.name} (State: ${thread.state})\n")
            for (element in stack) {
                sb.append(" at $element\n")
            }
            sb.append("\n")
        }
        writeFile(dir, "java_threads", sb.toString())
    }

    private fun writeFile(dir: File, name: String, content: String) {
        FileUtils.writeText(File(dir, name), content)
    }

    private fun getStackTraceString(e: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        return sw.toString()
    }

    private fun copySystemFile(srcPath: String, destFile: File) {
        try {
            val srcFile = File(srcPath)
            if (srcFile.canRead()) {
                val text = srcFile.readText()
                FileUtils.writeText(destFile, text)
            }
        } catch (_: Exception) {
        }
    }
}