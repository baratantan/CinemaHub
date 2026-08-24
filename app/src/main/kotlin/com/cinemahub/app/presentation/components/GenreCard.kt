package com.cinemahub.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cinemahub.app.ui.theme.BackgroundTertiary
import com.cinemahub.app.ui.theme.TextPrimary

@Composable
fun GenreCard(
    genre: String,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.45f)
            .background(
                color = BackgroundTertiary,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = genre,
            color = TextPrimary,
            fontSize = 16.sp
        )
    }
}

@Composable
fun GenreGrid(
    genres: List<String>,
    onGenreClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "التصنيفات",
            color = TextPrimary,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        for (i in genres.indices step 2) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
            ) {
                GenreCard(genres[i], onClick = { onGenreClick(genres[i]) })
                if (i + 1 < genres.size) {
                    GenreCard(genres[i + 1], onClick = { onGenreClick(genres[i + 1]) })
                }
            }
        }
    }
}
