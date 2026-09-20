package me.yxp.qfun.hook.purify

import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseSwitchHookItem
import me.yxp.qfun.utils.hook.doNothing
import me.yxp.qfun.utils.hook.returnConstant
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.reflect.clazz
import me.yxp.qfun.utils.reflect.findMethod
import me.yxp.qfun.utils.reflect.findMethodOrNull
import java.lang.reflect.Method

@HookItemAnnotation(
    "隐藏秋秋人",
    "屏蔽秋秋人（合养宠物）的聊天入口、浮动精灵与气泡",
    HookCategory.PURIFY
)
object HideQmate : BaseSwitchHookItem() {

    private const val API_IMPL = "com.tencent.mobileqq.qmate.api.impl.QmateApiImpl"
    private const val SERVICE_IMPL = "com.tencent.mobileqq.qmate.api.impl.QmateServiceImpl" // 9.3.65 开关下沉到此
    private const val SWITCHER = "com.tencent.mobileqq.qmate.api.QmateSwitcher" // 9.3.65 新增
    private const val REPOSITORY = "com.tencent.ntcompose.business.qmate.home.repo.QmateRepository"

    private lateinit var isQmateEnable: Method
    private lateinit var isQmateSwitchOn: Method
    private var isServiceEnable: Method? = null
    private var isConversationEnable: Method? = null
    private var switcherSwitch: Method? = null
    private var getQmateInfo: Method? = null

    override fun onInit(): Boolean {

        if (!HostInfo.isQQ) return false

        val impl = API_IMPL.clazz ?: return false

        isQmateEnable = impl.findMethod {
            name = "isQmateEnable"
            returnType = boolean
        }

        isQmateSwitchOn = impl.findMethod {
            name = "isQmateSwitchOn"
            returnType = boolean
            paramTypes(long)
        }

        // service 与 switcher 为 9.3.65 独有，取不到即跳过
        val service = SERVICE_IMPL.clazz
        isServiceEnable = service?.findMethodOrNull {
            name = "isQmateEnable"
            returnType = boolean
        }
        isConversationEnable = service?.findMethodOrNull {
            name = "isQmateConversationEnable"
            returnType = boolean
        }

        switcherSwitch = SWITCHER.clazz?.findMethodOrNull {
            name = "a"
            returnType = boolean
            paramCount = 0
        }

        // getQmateInfo(uin, onSuccess, onError)，9.3.25 中混淆名为 b
        val function1 = "kotlin.jvm.functions.Function1".clazz
        getQmateInfo = function1?.let {
            REPOSITORY.clazz?.findMethodOrNull {
                returnType = void
                paramTypes(long, it, it)
            }
        }

        return super.onInit()
    }

    override fun onHook() {
        isQmateEnable.returnConstant(this, false)
        isQmateSwitchOn.returnConstant(this, false)
        isServiceEnable?.returnConstant(this, false)
        isConversationEnable?.returnConstant(this, false)
        switcherSwitch?.returnConstant(this, false)
        getQmateInfo?.doNothing(this)
    }

}
