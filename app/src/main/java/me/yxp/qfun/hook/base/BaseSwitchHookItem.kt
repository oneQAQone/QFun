package me.yxp.qfun.hook.base

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import me.yxp.qfun.annotation.HookCategory
import me.yxp.qfun.utils.log.LogUtils
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.qq.QQCurrentEnv
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Suppress("DEPRECATION")
abstract class BaseSwitchHookItem : BaseHookItem() {

    val tag: String get() = annotation?.tag ?: "Unknown"
    val desc: String get() = annotation?.desc ?: ""
    val category: String get() = annotation?.category ?: HookCategory.OTHER

    override var isEnable: Boolean by BooleanPreference(name, false)
    var isAvailable: Boolean = false

    fun init() {
        try {
            isAvailable = onInit()
            if (isAvailable && isInTargetProcess()) {
                if (this is BaseClickableHookItem<*>) initData()
                onHook()
            }
        } catch (t: Throwable) {
            LogUtils.e(this, t)
            isAvailable = false
        }
    }

    protected open fun onInit(): Boolean = true

    protected open fun onHook() {}

    class BooleanPreference(private val key: String, private val default: Boolean) :
        ReadWriteProperty<Any, Boolean> {


        override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
            return prefs.getBoolean(key, default)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
            prefs.edit { putBoolean(key, value) }
        }
    }

    companion object {
        val prefs: SharedPreferences
            get() = HostInfo.hostContext.getSharedPreferences(
                "QFun_Config_${QQCurrentEnv.currentUin}",
                Context.MODE_MULTI_PROCESS
            )

    }

}