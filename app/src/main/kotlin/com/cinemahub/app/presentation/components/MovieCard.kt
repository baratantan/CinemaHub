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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
    val interactions = remember { MutableInteractionSource() }
    val pressed by interactions.collectIsPressedAsState()
    val hovered by interactions.collectIsHoveredAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else if (hovered) 1.035f else 1f,
        animationSpec = spring(stiffness = 500f),
        label = "posterScale"
    )
    val elevation by animateDpAsState(
        targetValue = if (hovered) 12.dp else 2.dp,
        animationSpec = spring(stiffness = 500f),
        label = "posterElevation"
    )
    val posterShape = RoundedCornerShape(18.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .then(if (clickEnabled) Modifier.clickable(interactionSource = interactions, indication = null, onClick = onClick) else Modifier)
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .shadow(elevation, posterShape)
                .clip(posterShape)
                .background(Color(0xFF20232D))
        ) {
            AsyncImage(
                model = posterUrl,
                contentDescription = title,
                modifier = Modifier.fillMaxWidth().height(220.dp),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier.matchParentSize().background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to Color(0x16000000),
                            0.55f to Color.Transparent,
                            1f to Color(0xCC000000)
                        )
                    )
                )
            )
            IconButton(onClick = onFavoriteClick, modifier = Modifier.align(Alignment.TopEnd)) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "أضف للمفضلة",
                    tint = if (isFavorite) PrimaryRed else Color.White
                )
            }
        }
        Text(
            text = title,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 3.dp, top = 9.dp).fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(text = "★  ${String.format("%.1f", rating)}", color = TextSecondary, modifier = Modifier.padding(start = 3.dp, top = 4.dp))
    }
}
