package de.schnettler.datastore.compose.material3

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.accompanist.insets.statusBarsPadding
import de.schnettler.datastore.compose.material3.model.Preference
import de.schnettler.datastore.compose.material3.model.Preference.PreferenceGroup
import de.schnettler.datastore.compose.material3.model.Preference.PreferenceItem
import de.schnettler.datastore.compose.material3.widget.PreferenceGroupHeader
import de.schnettler.datastore.manager.DataStoreManager

/**
 * Preference Screen composable which contains a list of [Preference] items
 * @param items [Preference] items which should be displayed on the preference screen. An item can be a single [PreferenceItem] or a group ([PreferenceGroup])
 * @param dataStore a [DataStore] where the preferences will be saved
 * @param modifier [Modifier] to be applied to the preferenceScreen layout
 * @param statusBarPadding whether statusBar padding is needed. Set to true if your app is laid out edgeToEdge
 */
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
@Composable
fun PreferenceScreen(
    items: List<Preference>,
    dataStore: DataStore<Preferences>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    statusBarPadding: Boolean = false,
    groupDividers: Boolean = true,
) {
    val dataStoreManager = remember {
        DataStoreManager(dataStore)
    }

    PreferenceScreen(
        items = items,
        modifier = modifier,
        dataStoreManager = dataStoreManager,
        contentPadding = contentPadding,
        statusBarPadding = statusBarPadding,
        groupDividers = groupDividers
    )
}

/**
 * Preference Screen composable which contains a list of [Preference] items
 * @param items [Preference] items which should be displayed on the preference screen. An item can be a single [PreferenceItem] or a group ([PreferenceGroup])
 * @param dataStoreManager a [DataStoreManager] responsible for the dataStore backing the preference screen
 * @param modifier [Modifier] to be applied to the preferenceScreen layout
 * @param statusBarPadding whether statusBar padding is needed. Set to true if your app is laid out edgeToEdge
 */
@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun PreferenceScreen(
    items: List<Preference>,
    dataStoreManager: DataStoreManager,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    statusBarPadding: Boolean = false,
    groupDividers: Boolean = true
) {
    val prefs by dataStoreManager.preferenceFlow.collectAsState(initial = null)

    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        if (statusBarPadding) {
            item { Spacer(modifier = Modifier.statusBarsPadding()) }
        }

        items.forEachIndexed { index, preference ->
            when (preference) {
                // Create Preference Group
                is PreferenceGroup -> {
                    if (groupDividers) {
                        items.getOrNull(index - 1)?.let {
                            if (it !is PreferenceGroup) {
                                item {
                                    Divider()
                                }
                            }
                        }
                    }
                    item {
                        PreferenceGroupHeader(title = preference.title)
                    }
                    items(preference.preferenceItems) { item ->
                        CompositionLocalProvider(LocalPreferenceEnabledStatus provides preference.enabled) {
                            PreferenceItem(
                                item = item,
                                prefs = prefs,
                                dataStoreManager = dataStoreManager
                            )
                        }
                    }
                    if (groupDividers) {
                        item {
                            Divider()
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Create Preference Item
                is PreferenceItem<*> -> item {
                    PreferenceItem(
                        item = preference,
                        prefs = prefs,
                        dataStoreManager = dataStoreManager
                    )
                }
            }
        }
    }
}