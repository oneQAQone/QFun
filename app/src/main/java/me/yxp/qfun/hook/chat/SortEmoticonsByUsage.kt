package me.yxp.qfun.hook.chat

import android.view.View
import com.tencent.mobileqq.AIODepend.IPanelInteractionListener
import com.tencent.mobileqq.app.FavEmoRoamingHandler
import com.tencent.mobileqq.emosm.api.IFavroamingDBManagerService
import com.tencent.mobileqq.emoticonview.FavoriteEmoticonInfo
import com.tencent.mobileqq.emoticonview.FavoriteEmotionAdapter
import com.tencent.mobileqq.emoticonview.api.IEmosmService
import com.tencent.qphone.base.remote.ToServiceMsg
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.dexkit.DexKitTask
import me.yxp.qfun.utils.hook.hookAfter
import me.yxp.qfun.utils.hook.hookBefore
import me.yxp.qfun.utils.qq.api
import me.yxp.qfun.utils.qq.handler
import me.yxp.qfun.utils.qq.runtime
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.getObject
import org.luckypray.dexkit.query.FindMethod
import org.luckypray.dexkit.query.base.BaseQuery
import java.lang.reflect.Method

@HookItemAnnotation(
    "收藏表情按使用排序",
    "发送收藏表情后，自动将其排在列表最前面",
    HookCategory.CHAT
)
object SortEmoticonsByUsage : BaseSwitchHookItem(), DexKitTask {

    private lateinit var onClick: Method
    private lateinit var roamReportMethod: Method

    override fun onInit(): Boolean {
        onClick = FavoriteEmotionAdapter::class.java.getDeclaredMethod("onClick", View::class.java)
        roamReportMethod = requireMethod("roamReportMethod")
        return super.onInit()
    }

    override fun onHook() {
        FavEmoRoamingHandler::class.java
            .findMethod {
                name = "onReceive"
            }
            .hookBefore(this) { param ->
                val msg = param.args[0] as ToServiceMsg
                if (msg.extraData.getInt("cmd_fav_subcmd") == 6) param.returnEarly(null)
            }
        onClick.hookAfter(this) { param ->
            val adapter = param.thisObject as FavoriteEmotionAdapter
            val view = param.args[0] as View
            val tag = view.tag as? FavoriteEmoticonInfo ?: return@hookAfter
            val emoId = tag.emoId

            if (emoId == 0) return@hookAfter

            val dbService = runtime<IFavroamingDBManagerService>()
            val emoticonDataList = dbService.emoticonDataList ?: return@hookAfter

            val targetData = emoticonDataList.find { it.emoId == emoId } ?: return@hookAfter
            val maxEmoId = emoticonDataList.maxOfOrNull { it.emoId } ?: 1

            if (emoId == maxEmoId) return@hookAfter

            val resid = targetData.resid
            if (!resid.isNullOrEmpty()) {
                roamReportMethod.invoke(handler<FavEmoRoamingHandler>(), arrayListOf(resid))
            }

            dbService.deleteCustomEmotion(targetData)
            val cloneData = targetData.cloneEmotionData(targetData)
            cloneData.emoId = maxEmoId + 1
            dbService.insertCustomEmotion(cloneData)
            dbService.trimCache()

            val listener = adapter.getObject("mInteractionListener") as IPanelInteractionListener
            api<IEmosmService>().tryGetEmoticonMainPanel(listener).updateFavEmoticonPanel()

        }
    }

    override fun getQueryMap(): Map<String, BaseQuery> = mapOf(
        "roamReportMethod" to FindMethod().apply {
            matcher {
                declaredClass(FavEmoRoamingHandler::class.java)
                usingStrings("moveEmotion exception =")
            }
        }
    )
}