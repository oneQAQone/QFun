package me.yxp.qfun.hook.troop

import androidx.compose.runtime.Composable
import com.tencent.qqnt.kernel.nativeinterface.RecentContactInfo
import com.tencent.qqnt.notification.NotificationFacade
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.conf.TroopSetConfig
import me.yxp.qfun.hook.base.BaseClickableHookItem
import me.yxp.qfun.ui.pages.configs.TroopSelectorPage
import me.yxp.qfun.utils.dexkit.DexKitTask
import me.yxp.qfun.utils.hook.hookReplace
import me.yxp.qfun.utils.hook.invokeOriginal
import me.yxp.qfun.utils.json.ProtoData
import me.yxp.qfun.utils.json.str
import me.yxp.qfun.utils.json.walk
import me.yxp.qfun.utils.reflect.findMethod
import mqq.app.AppRuntime
import org.luckypray.dexkit.query.FindMethod
import org.luckypray.dexkit.query.base.BaseQuery
import java.lang.reflect.Method
import java.net.URI

@HookItemAnnotation(
    "屏蔽艾特全体",
    "屏蔽艾特全体和群待办，点击选择【不屏蔽】的群聊",
    HookCategory.GROUP
)
object AntiAtAll : BaseClickableHookItem<TroopSetConfig>(TroopSetConfig.serializer()), DexKitTask {

    override val defaultConfig: TroopSetConfig = TroopSetConfig()

    private lateinit var atAll: Method
    private lateinit var troopToDo: Method

    override fun onInit(): Boolean {
        atAll = NotificationFacade::class.java
            .findMethod {
                returnType = void
                paramTypes(
                    AppRuntime::class.java,
                    RecentContactInfo::class.java,
                    null,
                    boolean
                )
            }
        troopToDo = requireMethod("troopToDo")
        return super.onInit()
    }

    @Suppress("UNCHECKED_CAST")
    override fun onHook() {
        atAll.hookReplace(this) { param ->
            val recentContactInfo = param.args[1] as RecentContactInfo
            val atType = recentContactInfo.atType
            val peerUin = recentContactInfo.peerUin.toString()
            if (atType != 1 || config.selectedSet.contains(peerUin)) {
                return@hookReplace param.invokeOriginal()
            }
            return@hookReplace null
        }

        troopToDo.hookReplace(this) { param ->
            val byteList = param.args.last() as ArrayList<Byte>
            val data = ProtoData().apply {
                fromBytes(byteList.toByteArray())
            }
            val json = data.toJSON()
            val url = json.walk("3", "2", "7", "5", "1")?.str
            if (url != null && config.selectedSet.contains(extractUIN(url))) {
                return@hookReplace param.invokeOriginal()
            }
            return@hookReplace null
        }
    }

    @Composable
    override fun ConfigContent(onDismiss: () -> Unit) {
        TroopSelectorPage(
            title = "选择白名单群聊",
            currentConfig = config,
            onSave = ::updateConfig,
            onDismiss = onDismiss
        )
    }

    override fun getQueryMap(): Map<String, BaseQuery> = mapOf(
        "troopToDo" to FindMethod().apply {
            searchPackages("com.tencent.mobileqq.notification.modularize")
            matcher {
                usingStrings("TianShuOfflineMsgCenter", "deal0x135Msg online:")
            }
        }
    )

    private fun extractUIN(url: String): String {
        val fixedUrl = if (!url.startsWith("http")) "http://example.com/$url" else url
        val uri = URI(fixedUrl)
        val query = uri.query ?: throw IllegalArgumentException("No query")

        val params = query.split("&").associate {
            val parts = it.split("=")
            if (parts.size == 2) parts[0] to parts[1] else "" to ""
        }
        return params["uin"] ?: throw IllegalArgumentException("No uin param")
    }
}
