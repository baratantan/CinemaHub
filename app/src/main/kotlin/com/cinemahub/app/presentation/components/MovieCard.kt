package com.cinemahub.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cinemahub.app.ui.theme.PrimaryRed
import com.cinemahub.app.ui.theme.TextPrimary
import com.cinemahub.app.ui.theme.TextSecondary

@Composable
fun MovieCard(
    title: String,
    posterUrl: String,
    rating: Double,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {},
    onClick: () -> Unit = {},
    clickEnabled: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (clickEnabled) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(color = Color(0xFF2A2A2A), shape = RoundedCornerShape(12.dp))
        ) {
            AsyncImage(
                model = posterUrl,
                contentDescription = title,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(60.dp)
                    .background(brush = Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black)))
            )
            IconButton(onClick = onFavoriteClick, modifier = Modifier.align(Alignment.TopEnd)) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "أضف للمفضلة",
                    tint = if (isFavorite) PrimaryRed else TextSecondary
                )
            }
        }
        Text(
            text = title,
            color = TextPrimary,
            modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(text = "⭐ ${String.format("%.1f", rating)}", color = TextSecondary, modifier = Modifier.padding(top = 4.dp))
    }
}
