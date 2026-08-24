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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
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

@Composable
fun FeaturedMovieCard(
    title: String,
    description: String,
    backdropUrl: String,
    rating: Double,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
            .background(
                color = Color(0xFF2A2A2A),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        AsyncImage(
            model = backdropUrl,
            contentDescription = title,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(120.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black)
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .fillMaxWidth(0.8f)
        ) {
            Text(
                text = title,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = description,
                color = Color(0xFFB0B0B0),
                modifier = Modifier.padding(top = 8.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "⭐ $rating",
                color = PrimaryRed,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Play button
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .background(color = PrimaryRed.copy(alpha = 0.8f), shape = RoundedCornerShape(50.dp))
                .padding(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "تشغيل",
                tint = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
