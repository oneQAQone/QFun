package me.yxp.qfun.utils.qq

import com.tencent.qqnt.kernel.api.IKernelService
import com.tencent.qqnt.kernel.api.impl.KernelServiceImpl
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.reflect.ClassUtils
import me.yxp.qfun.utils.reflect.callMethod
import java.lang.reflect.Proxy

object SignatureTool {

    fun setSignature(text: String) {
        try {
            val kernelService = runtime<IKernelService>() as KernelServiceImpl
            val profileService = kernelService.profileService?.service ?: return
            
            val infoClass = ClassUtils.loadClassOrNull("com.tencent.qqnt.kernel.nativeinterface.ProfileModifyInfo")
            val callbackClass = ClassUtils.loadClassOrNull("com.tencent.qqnt.kernel.nativeinterface.IOperateCallback")
            
            val callback = callbackClass?.let {
                Proxy.newProxyInstance(ClassUtils.hostClassLoader, arrayOf(it)) { _, _, _ -> 0 }
            }

            if (infoClass != null) {
                val info = infoClass.newInstance()
                infoClass.getField("longNick").set(info, text)
                profileService.callMethod("modifyProfile", info, callback)
            } else {
                profileService.callMethod("setLongNick", text, callback)
            }
        } catch (t: Throwable) {
            LogUtils.e("SignatureTool", t)
        }
    }
}