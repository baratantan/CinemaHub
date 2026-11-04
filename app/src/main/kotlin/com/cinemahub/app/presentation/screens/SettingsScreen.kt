package com.cinemahub.app.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.compose.runtime.rememberCoroutineScope
import com.cinemahub.app.data.preferences.AppSettings
import com.cinemahub.app.data.preferences.CinemaHubPreferences
import com.cinemahub.app.ui.theme.BackgroundPrimary
import com.cinemahub.app.ui.theme.BackgroundSecondary
import com.cinemahub.app.ui.theme.BackgroundTertiary
import com.cinemahub.app.ui.theme.PrimaryRed
import com.cinemahub.app.ui.theme.TextPrimary
import com.cinemahub.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val settings by CinemaHubPreferences.observe(context).collectAsState(initial = AppSettings())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(color = PrimaryRed.copy(alpha = 0.14f), shape = RoundedCornerShape(16.dp)) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = null,
                    tint = PrimaryRed,
                    modifier = Modifier.padding(14.dp).size(28.dp)
                )
            }
            Column(Modifier.padding(start = 14.dp)) {
                Text("الإعدادات", color = TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text("خصّص تجربتك في CinemaHub", color = TextSecondary, fontSize = 14.sp)
            }
        }

        Spacer(Modifier.height(28.dp))
        SectionTitle("المظهر والتجربة")
        SettingsCard {
            PreferenceRow(
                icon = Icons.Filled.Palette,
                title = "المظهر الداكن",
                description = "استخدم ألواناً مريحة للمشاهدة ليلاً",
                checked = settings.isDarkTheme,
                onCheckedChange = { enabled -> scope.launch { CinemaHubPreferences.setDarkTheme(context, enabled) } }
            )
        }

        Spacer(Modifier.height(24.dp))
        SectionTitle("التفضيلات")
        SettingsCard {
            PreferenceRow(
                icon = Icons.Filled.Notifications,
                title = "التنبيهات",
                description = "تفضيل التنبيهات عند توفر محتوى جديد",
                checked = settings.notificationsEnabled,
                onCheckedChange = { enabled -> scope.launch { CinemaHubPreferences.setNotificationsEnabled(context, enabled) } }
            )
            PreferenceDivider()
            PreferenceRow(
                icon = Icons.Filled.Wifi,
                title = "التنزيل عبر Wi-Fi فقط",
                description = "حفظ بيانات الهاتف عند تنزيل المحتوى",
                checked = settings.wifiOnlyDownloads,
                onCheckedChange = { enabled -> scope.launch { CinemaHubPreferences.setWifiOnlyDownloads(context, enabled) } }
            )
        }

        Spacer(Modifier.height(24.dp))
        SectionTitle("حول التطبيق")
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = BackgroundSecondary,
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(24.dp))
                Column(Modifier.weight(1f).padding(start = 14.dp)) {
                    Text("CinemaHub", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    Text("الإصدار 1.0.0", color = TextSecondary, fontSize = 13.sp)
                }
                Text("استمتع بالمشاهدة", color = PrimaryRed, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        title,
        color = TextSecondary,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 10.dp, start = 4.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BackgroundSecondary,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(content = content)
    }
}

@Composable
private fun PreferenceRow(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(color = BackgroundTertiary, shape = CircleShape) {
            Icon(icon, contentDescription = null, tint = PrimaryRed, modifier = Modifier.padding(10.dp).size(20.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(description, color = TextSecondary, fontSize = 12.sp, lineHeight = 17.sp)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun PreferenceDivider() {
    androidx.compose.material3.HorizontalDivider(
        modifier = Modifier.padding(start = 60.dp, end = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}
