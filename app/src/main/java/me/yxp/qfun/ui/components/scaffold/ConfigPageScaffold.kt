package me.yxp.qfun.ui.components.scaffold

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.yxp.qfun.ui.components.dialogs.QFunScaffold

@Composable
fun <T> ConfigPageScaffold(
    title: String,
    configData: T,
    onSave: (T) -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = "确定",
    content: @Composable ColumnScope.(T) -> Unit
) {
    QFunScaffold(
        title = title,
        onDismiss = onDismiss,
        onConfirm = { onSave(configData); onDismiss() },
        confirmText = confirmText
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            content(configData)
        }
    }
}
