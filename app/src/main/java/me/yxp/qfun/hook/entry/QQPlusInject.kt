package me.yxp.qfun.hook.entry

import android.content.Intent
import com.tencent.widget.PopupMenuDialog
import com.tencent.widget.PopupMenuDialog.MenuItem
import com.tencent.widget.PopupMenuDialog.OnClickActionListener
import me.yxp.qfun.R
import me.yxp.qfun.activity.PluginActivity
import me.yxp.qfun.activity.SettingActivity
import me.yxp.qfun.activity.StorageCleanActivity
import me.yxp.qfun.annotation.HookItemAnnotation
import me.yxp.qfun.hook.base.BaseApiHookItem
import me.yxp.qfun.hook.base.Listener
import me.yxp.qfun.utils.hook.hookBefore
import me.yxp.qfun.utils.qq.HostInfo
import me.yxp.qfun.utils.qq.QQCurrentEnv
import me.yxp.qfun.utils.reflect.findMethod

@HookItemAnnotation("QQ加号入口")
object QQPlusInject : BaseApiHookItem<Listener>() {

    @Suppress("UNCHECKED_CAST")
    override fun loadHook() {
        val deleteIconRes = try {
            HostInfo.hostContext.resources.getIdentifier(
                "qui_delete_oversized",
                "drawable",
                HostInfo.packageName
            )
        } catch (e: Exception) {
            R.drawable.ic_launcher
        }

        PopupMenuDialog::class.java
            .findMethod {
                name = "conversationPlusBuild"
            }.hookBefore(this) { param ->

                val activity = QQCurrentEnv.activity ?: return@hookBefore
                val menuItemList = param.args[1] as MutableList<MenuItem>

                menuItemList.apply {
                    add(
                        0,
                        MenuItem(
                            R.string.plugin_name,
                            "JavaPlugin",
                            "JavaPlugin",
                            R.drawable.ic_float_ball
                        )
                    )
                    add(
                        0,
                        MenuItem(
                            R.string.app_name,
                            "QFun",
                            "QFun",
                            R.drawable.ic_launcher
                        )
                    )
                    add(
                        2,
                        MenuItem(
                            R.string.storage_clean,
                            "缓存清理",
                            "StorageClean",
                            deleteIconRes
                        )
                    )
                }

                val origin = param.args[2] as OnClickActionListener

                param.args[2] = OnClickActionListener { menuItem ->
                    when (menuItem.id) {
                        R.string.app_name -> activity.startActivity(
                            Intent(activity, SettingActivity::class.java)
                        )
                        R.string.plugin_name -> activity.startActivity(
                            Intent(activity, PluginActivity::class.java)
                        )
                        R.string.storage_clean -> activity.startActivity(
                            Intent(activity, StorageCleanActivity::class.java)
                        )
                        else -> origin.onClickAction(menuItem)
                    }
                }
            }
    }
}
