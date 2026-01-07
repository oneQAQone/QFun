package me.yxp.qfun.utils.qq

import android.widget.Toast
import com.tencent.util.QQToastUtil
import me.yxp.qfun.common.ModuleScope

object Toasts {

    fun toast(message: String) {
        ModuleScope.launchMain {
            QQCurrentEnv.activity?.let {
                Toast.makeText(it, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun qqToast(icon: Int, message: String?) {
        try {
            message?.let { QQToastUtil.showQQToastInUiThread(icon, it) }
        } catch (_: Exception) {
        }
    }
}
