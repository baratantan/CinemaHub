package com.cinemahub.app.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cinemahub.app.ui.theme.PrimaryRed
import com.cinemahub.app.ui.theme.TextPrimary
import com.cinemahub.app.ui.theme.TextSecondary

@Composable
fun FeaturedMovieCard(
    title: String,
    description: String,
    backdropUrl: String,
    rating: Double,
    onClick: () -> Unit = {}
) {
    val interactions = remember { MutableInteractionSource() }
    val pressed by interactions.collectIsPressedAsState()
    val hovered by interactions.collectIsHoveredAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else if (hovered) 1.012f else 1f,
        animationSpec = spring(stiffness = 420f),
        label = "heroScale"
    )
    val elevation by animateDpAsState(
        targetValue = if (hovered) 14.dp else 4.dp,
        animationSpec = spring(stiffness = 420f),
        label = "heroElevation"
    )
    val shape = RoundedCornerShape(24.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(390.dp)
            .scale(scale)
            .shadow(elevation, shape)
            .clip(shape)
            .background(Color(0xFF20232D))
            .clickable(interactionSource = interactions, indication = null, onClick = onClick)
    ) {
        AsyncImage(
            model = backdropUrl,
            contentDescription = title,
            modifier = Modifier.fillMaxWidth().height(390.dp),
            contentScale = ContentScale.Crop
        )
        Box(
            Modifier.matchParentSize().background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to Color(0x26000000),
                        0.35f to Color(0x08000000),
                        0.62f to Color(0x66000000),
                        1f to Color(0xF2080A10)
                    )
                )
            )
        )
        Box(
            Modifier.matchParentSize().background(
                Brush.horizontalGradient(listOf(Color(0x40000000), Color.Transparent, Color(0x18000000)))
            )
        )
        Column(
            modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(horizontal = 22.dp, vertical = 24.dp)
        ) {
            Text("مقترح لك", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(
                title,
                color = TextPrimary,
                fontSize = 29.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(top = 5.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                description,
                color = Color(0xFFE0E0E0),
                modifier = Modifier.padding(top = 8.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
            Text("★  ${String.format("%.1f", rating)}", color = Color(0xFFFFC857), modifier = Modifier.padding(top = 8.dp))
        }
        Box(
            modifier = Modifier.align(Alignment.Center).background(PrimaryRed.copy(alpha = 0.92f), CircleShape).padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.PlayArrow, contentDescription = "تشغيل $title", tint = Color.White)
        }
    }
}
