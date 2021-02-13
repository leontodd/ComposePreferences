package de.schnettler.composepreferences

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun SwitchPreference(
    title: String,
    summary: String,
    key: String,
    singleLineTitle: Boolean,
    icon: ImageVector,
    defaultValue: Boolean = false,
    enabled: Boolean = true,
) {
    val preferences = LocalPreferences.current
    val onClicked: (Boolean) -> Unit = {
        preferences.sharedPreferences.edit().putBoolean(key, it).apply()
    }
    val state by preferences.getBoolean(key, defaultValue).asFlow().collectAsState(initial = defaultValue)

    Preference(
        title = title,
        summary = summary,
        singleLineTitle = singleLineTitle,
        icon = icon,
        enabled = enabled,
        onClick = { onClicked(!state) }
    ) {
        Switch(checked = state, onCheckedChange = { onClicked(it) }, enabled = enabled)
    }
}