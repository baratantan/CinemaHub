package com.cinemahub.app.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.cinemahub.app.domain.model.WatchHistory
import com.cinemahub.app.ui.theme.PrimaryRed
import com.cinemahub.app.ui.theme.TextPrimary
import com.cinemahub.app.ui.theme.TextSecondary

@Composable
fun ContinueWatchingSection(
    items: List<WatchHistory>,
    onItemClick: (WatchHistory) -> Unit
) {
    if (items.isEmpty()) return
    Column(Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text(
            "متابعة المشاهدة",
            color = TextPrimary,
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(items, key = { "continue_${it.itemType}_${it.itemId}" }) { item ->
                Card(
                    onClick = { onItemClick(item) },
                    modifier = Modifier.width(268.dp).padding(start = 8.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1E27))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.width(98.dp).height(118.dp)) {
                            AsyncImage(
                                model = item.posterUrl,
                                contentDescription = item.title,
                                modifier = Modifier.matchParentSize().clip(RoundedCornerShape(14.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Icon(
                                Icons.Filled.PlayArrow,
                                contentDescription = "متابعة",
                                tint = Color.White,
                                modifier = Modifier.align(Alignment.Center).clip(RoundedCornerShape(50)).padding(8.dp)
                            )
                        }
                        Column(Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 10.dp)) {
                            Text(item.title, color = TextPrimary, maxLines = 2, fontSize = 14.sp)
                            val minutes = item.progressInSeconds / 60
                            val seconds = item.progressInSeconds % 60
                            Text(
                                "متابعة من $minutes:${seconds.toString().padStart(2, '0')}",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                            LinearProgressIndicator(
                                progress = 0.35f,
                                modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(3.dp).clip(RoundedCornerShape(4.dp)),
                                color = PrimaryRed,
                                trackColor = Color(0xFF41444D)
                            )
                        }
                    }
                }
            }
        }
    }
}
