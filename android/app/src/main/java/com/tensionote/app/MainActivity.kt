package com.tensionote.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import com.tensionote.R
import com.tensionote.core.repository.AppGraph
import com.tensionote.feature.history.HistoryScreen
import com.tensionote.feature.history.HistoryViewModel
import com.tensionote.feature.home.HomeScreen
import com.tensionote.feature.home.HomeViewModel
import com.tensionote.feature.record.DetailedRecordScreen
import com.tensionote.feature.record.EditRecordScreen
import com.tensionote.feature.record.RecordDetailScreen
import com.tensionote.feature.reminder.ReminderScreen
import com.tensionote.feature.reminder.ReminderViewModel
import com.tensionote.feature.report.ReportScreen
import com.tensionote.feature.report.ReportViewModel
import com.tensionote.feature.settings.SettingsScreen
import com.tensionote.feature.settings.SettingsDocument
import com.tensionote.feature.settings.SettingsDocumentScreen
import com.tensionote.feature.settings.SettingsDocuments
import com.tensionote.feature.settings.SettingsViewModel
import com.tensionote.feature.trend.TrendScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface {
                    TensionoteRoot()
                }
            }
        }
    }
}

private enum class RootTab {
    HOME, HISTORY, REMINDER, REPORT, SETTINGS, DETAIL_ENTRY, TREND, RECORD_DETAIL, RECORD_EDIT, SETTINGS_DOCUMENT
}

@Composable
private fun TensionoteRoot() {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.factory()
    )
    val historyViewModel: HistoryViewModel = viewModel(
        factory = HistoryViewModel.factory()
    )
    val reminderViewModel: ReminderViewModel = viewModel(
        factory = ReminderViewModel.factory(context)
    )
    val reportViewModel: ReportViewModel = viewModel(
        factory = ReportViewModel.factory(context)
    )
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.factory(context)
    )
    var selectedTab by remember { mutableStateOf(RootTab.HOME) }
    var selectedRecordId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedDocument by remember { mutableStateOf<SettingsDocument?>(null) }
    val selectedRecord = selectedRecordId?.let { recordId ->
        AppGraph.bloodPressureRepository.fetchAll().firstOrNull { it.id == recordId }
    }

    Scaffold(
        bottomBar = {
            if (
                selectedTab != RootTab.DETAIL_ENTRY &&
                selectedTab != RootTab.TREND &&
                selectedTab != RootTab.RECORD_DETAIL &&
                selectedTab != RootTab.RECORD_EDIT &&
                selectedTab != RootTab.SETTINGS_DOCUMENT
            ) {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == RootTab.HOME,
                        onClick = { selectedTab = RootTab.HOME },
                        icon = { Icon(Icons.Outlined.Home, contentDescription = null) },
                        label = { Text(stringResource(R.string.tab_home)) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == RootTab.HISTORY,
                        onClick = { selectedTab = RootTab.HISTORY },
                        icon = { Icon(Icons.Outlined.History, contentDescription = null) },
                        label = { Text(stringResource(R.string.tab_history)) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == RootTab.REMINDER,
                        onClick = { selectedTab = RootTab.REMINDER },
                        icon = { Icon(Icons.Outlined.Notifications, contentDescription = null) },
                        label = { Text(stringResource(R.string.tab_reminder)) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == RootTab.REPORT,
                        onClick = { selectedTab = RootTab.REPORT },
                        icon = { Icon(Icons.Outlined.Description, contentDescription = null) },
                        label = { Text(stringResource(R.string.tab_report)) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == RootTab.SETTINGS,
                        onClick = { selectedTab = RootTab.SETTINGS },
                        icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                        label = { Text(stringResource(R.string.tab_settings)) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                RootTab.HOME -> HomeScreen(
                    viewModel = homeViewModel,
                    onDetailedEntryClick = { selectedTab = RootTab.DETAIL_ENTRY },
                    onTrendClick = { selectedTab = RootTab.TREND }
                )

                RootTab.HISTORY -> HistoryScreen(
                    viewModel = historyViewModel,
                    onRecordClick = { record ->
                        selectedDocument = null
                        selectedRecordId = record.id
                        selectedTab = RootTab.RECORD_DETAIL
                    }
                )
                RootTab.REMINDER -> ReminderScreen(viewModel = reminderViewModel)
                RootTab.REPORT -> ReportScreen(viewModel = reportViewModel)
                RootTab.SETTINGS -> SettingsScreen(
                    viewModel = settingsViewModel,
                    onOpenDocument = { document ->
                        selectedRecordId = null
                        selectedDocument = document
                        selectedTab = RootTab.SETTINGS_DOCUMENT
                    }
                )
                RootTab.DETAIL_ENTRY -> DetailedRecordScreen(
                    initialStatus = homeViewModel.currentDraftStatus(),
                    onBack = {
                        selectedRecordId = null
                        selectedDocument = null
                        selectedTab = RootTab.HOME
                    }
                )
                RootTab.TREND -> TrendScreen(
                    viewModel = homeViewModel,
                    onBack = {
                        selectedRecordId = null
                        selectedDocument = null
                        selectedTab = RootTab.HOME
                    }
                )
                RootTab.RECORD_DETAIL -> if (selectedRecord != null) {
                    RecordDetailScreen(
                        record = selectedRecord,
                        onBack = {
                            selectedRecordId = null
                            selectedTab = RootTab.HISTORY
                        },
                        onEdit = { selectedTab = RootTab.RECORD_EDIT },
                        onDeleted = {
                            selectedRecordId = null
                            selectedTab = RootTab.HISTORY
                        }
                    )
                } else {
                    LaunchedEffect(selectedRecordId) {
                        selectedTab = RootTab.HISTORY
                    }
                }
                RootTab.RECORD_EDIT -> if (selectedRecord != null) {
                    EditRecordScreen(
                        record = selectedRecord,
                        onBack = {
                            selectedTab = RootTab.RECORD_DETAIL
                        }
                    )
                } else {
                    LaunchedEffect(selectedRecordId) {
                        selectedTab = RootTab.HISTORY
                    }
                }
                RootTab.SETTINGS_DOCUMENT -> if (selectedDocument != null) {
                    val document = selectedDocument ?: SettingsDocuments.disclaimer
                    SettingsDocumentScreen(
                        document = document,
                        onBack = {
                            selectedDocument = null
                            selectedTab = RootTab.SETTINGS
                        }
                    )
                } else {
                    LaunchedEffect(selectedDocument) {
                        selectedTab = RootTab.SETTINGS
                    }
                }
            }
        }
    }
}
