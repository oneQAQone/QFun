package me.yxp.qfun.hook.purify

import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.doNothing
import me.yxp.qfun.utils.hook.returnConstant
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.clazz
import me.yxp.qfun.utils.reflect.findMethodOrNull
import java.lang.reflect.Method

@HookItemAnnotation(
    "隐藏秋秋人",
    "屏蔽秋秋人（合养宠物）的聊天入口、浮动精灵与气泡",
    HookCategory.PURIFY
)
object HideQmate : BaseSwitchHookItem() {

    /**
     * 秋秋人能力接口 IQmateApi 的唯一实现。
     * QmateAIOHelper 与 QmateFloatVM 都以 invoke-interface 调用它，hook 实现类即可拦截。
     */
    private const val API_IMPL = "com.tencent.mobileqq.qmate.api.impl.QmateApiImpl"

    /** 数据层，getQmateInfo(uin, onSuccess, onError) 在 QQ 9.3.25 中的混淆名为 b */
    private const val REPOSITORY = "com.tencent.ntcompose.business.qmate.home.repo.QmateRepository"

    private lateinit var isQmateEnable: Method
    private lateinit var isQmateSwitchOn: Method
    private var getQmateInfo: Method? = null

    override fun onInit(): Boolean {

        if (!HostInfo.isQQ) return false

        val impl = API_IMPL.clazz ?: return false

        // 这两个开关决定 QmateAIOHelper 的入口图标与 QmateFloatVM 的浮动精灵是否展示
        val enable = impl.findMethodOrNull {
            name = "isQmateEnable"
            returnType = boolean
        }
        val switchOn = impl.findMethodOrNull {
            name = "isQmateSwitchOn"
            returnType = boolean
            paramTypes(long)
        }

        if (enable == null || switchOn == null) {
            LogUtils.e(
                this,
                NoSuchMethodException(
                    "QmateApiImpl 开关未命中: isQmateEnable=${enable != null}, isQmateSwitchOn=${switchOn != null}"
                )
            )
            return false
        }

        isQmateEnable = enable
        isQmateSwitchOn = switchOn

        // 数据层兜底，尽力而为：回调永不返回，入口图标也就无从刷新
        val function1 = "kotlin.jvm.functions.Function1".clazz
        if (function1 != null) {
            getQmateInfo = REPOSITORY.clazz?.findMethodOrNull {
                returnType = void
                paramTypes(long, function1, function1)
            }
        }

        return super.onInit()
    }

    override fun onHook() {
        isQmateEnable.returnConstant(this, false)
        isQmateSwitchOn.returnConstant(this, false)
        getQmateInfo?.doNothing(this)
    }

}
