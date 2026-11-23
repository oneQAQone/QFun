package me.yxp.qfun.hook.purify

import androidx.compose.runtime.Composable
import com.tencent.qqnt.kernel.nativeinterface.MsgRecord
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.conf.IceBreakWelcomeConfig
import me.yxp.qfun.conf.OthersJoinMode
import me.yxp.qfun.hook.base.BaseClickableHookItem
import me.yxp.qfun.ui.pages.configs.IceBreakWelcomePage
import me.yxp.qfun.utils.hook.hookReplace
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.findMethods
import me.yxp.qfun.utils.reflect.toClass
import java.lang.reflect.Method

@HookItemAnnotation(
    "入群欢迎净化",
    "自己入群不再弹「新人欢迎卡片」；他人入群的「欢迎」入口可彻底隐藏或仅禁止自动展开。点击设置",
    HookCategory.PURIFY
)
object DisableIceBreakWelcome : BaseClickableHookItem<IceBreakWelcomeConfig>(IceBreakWelcomeConfig.serializer()) {

    override val defaultConfig = IceBreakWelcomeConfig()

    private const val MIN_VERSION_CODE = 17048L

    private const val CLS_GRAY_TIP_MEMBERS =
        "com.tencent.mobileqq.aio.msglist.holder.component.graptips.extendcard.business.icebreak.IceBreakGrayTipsMembers"

    private const val CLS_GRAY_TIP_POLICY =
        "com.tencent.mobileqq.aio.msglist.holder.component.graptips.extendcard.business.icebreak.IceBreakGrayTipsPolicy"

    private const val CLS_NEWCOMER_ABILITY =
        "com.tencent.mobileqq.aio.msglist.msgrepo.NewcomerWelcomeMsgAbility"

    private const val PKG_NEWCOMER_CARD =
        "com.tencent.mobileqq.aio.msglist.holder.component.fakemsg.troop.newcomer."

    private lateinit var busiGate: Method

    private lateinit var autoExpand: Method

    private lateinit var cardCreator: Method

    override fun onInit(): Boolean {
        if (!HostInfo.isQQ || HostInfo.versionCode < MIN_VERSION_CODE) return false

        busiGate = CLS_GRAY_TIP_MEMBERS.toClass.findMethod {
            paramTypes(MsgRecord::class.java)
            returnType = boolean
        }

        autoExpand = CLS_GRAY_TIP_POLICY.toClass.findMethod {
            paramTypes(null, list)
            returnType = boolean
        }

        cardCreator = CLS_NEWCOMER_ABILITY.toClass.findMethods {
            paramTypes(string)
        }.single {
            it.returnType.name.startsWith(PKG_NEWCOMER_CARD)
        }

        return super.onInit()
    }

    override fun onHook() {
        busiGate.hookReplace(this) { chain ->
            if (config.othersJoinMode == OthersJoinMode.HIDE_ENTRY) false else chain.proceed()
        }

        autoExpand.hookReplace(this) { chain ->
            if (config.othersJoinMode == OthersJoinMode.KEEP_ALL) chain.proceed() else false
        }

        cardCreator.hookReplace(this) { chain ->
            if (config.hideSelfWelcomeCard) null else chain.proceed()
        }
    }

    @Composable
    override fun ConfigContent(onDismiss: () -> Unit) {
        IceBreakWelcomePage(
            currentConfig = config,
            onSave = ::updateConfig,
            onDismiss = onDismiss
        )
    }
}
