package com.cinemahub.app.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cinemahub.app.ui.theme.BackgroundSecondary
import com.cinemahub.app.ui.theme.PrimaryRed
import com.cinemahub.app.ui.theme.TextPrimary
import com.cinemahub.app.ui.theme.TextSecondary

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun CinemaHubBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem("الرئيسية", Icons.Filled.Home, "home"),
        BottomNavItem("بحث", Icons.Filled.Search, "search"),
        BottomNavItem("المفضلة", Icons.Filled.Favorite, "favorites"),
        BottomNavItem("المشاهدة", Icons.Filled.History, "watch_history"),
        BottomNavItem("الإعدادات", Icons.Filled.Settings, "settings")
    )

    NavigationBar(
        modifier = Modifier.fillMaxWidth().height(68.dp),
        containerColor = BackgroundSecondary,
        contentColor = TextPrimary
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.height(22.dp)
                    )
                },
                label = { Text(text = item.label, fontSize = 9.sp, maxLines = 1) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryRed,
                    selectedTextColor = PrimaryRed,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
