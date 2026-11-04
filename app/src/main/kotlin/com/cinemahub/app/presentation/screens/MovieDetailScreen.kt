package com.cinemahub.app.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.cinemahub.app.data.network.ApprovedStream
import com.cinemahub.app.presentation.viewmodel.MovieDetailViewModel
import com.cinemahub.app.ui.theme.BackgroundPrimary
import com.cinemahub.app.ui.theme.BackgroundSecondary
import com.cinemahub.app.ui.theme.BackgroundTertiary
import com.cinemahub.app.ui.theme.ErrorColor
import com.cinemahub.app.ui.theme.PrimaryRed
import com.cinemahub.app.ui.theme.TextPrimary
import com.cinemahub.app.ui.theme.TextSecondary

@Composable
fun MovieDetailScreen(
    movieId: Int,
    viewModel: MovieDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onPlayClick: (String, String) -> Unit = { _, _ -> }
) {
    LaunchedEffect(movieId) {
        viewModel.loadMovie(movieId)
        viewModel.loadApprovedStreams(movieId)
    }

    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingVideo by viewModel.isLoadingVideo.collectAsState()
    val movie by viewModel.movie.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val videoLinks by viewModel.videoLinks.collectAsState()
    val selectedVideoLink by viewModel.selectedVideoLink.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val reviewRating by viewModel.reviewRating.collectAsState()
    val reviewAuthorName by viewModel.reviewAuthorName.collectAsState()
    val reviewComment by viewModel.reviewComment.collectAsState()
    val isSubmittingReview by viewModel.isSubmittingReview.collectAsState()
    val reviewError by viewModel.reviewError.collectAsState()
    val reviewSubmitted by viewModel.reviewSubmitted.collectAsState()

    if (isLoading) {
        Box(Modifier.fillMaxSize().background(BackgroundPrimary), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryRed)
        }
    } else if (movie != null) {
        LazyColumn(Modifier.fillMaxSize().background(BackgroundPrimary)) {
            item {
                Box(Modifier.fillMaxWidth().height(250.dp)) {
                    AsyncImage(
                        model = movie?.backdropUrl,
                        contentDescription = movie?.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.TopStart).padding(8.dp)) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                }
            }
            item {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(movie?.title.orEmpty(), color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Text(movie?.releaseDate.orEmpty(), color = TextSecondary, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                        IconButton(onClick = viewModel::toggleFavorite) {
                            Icon(
                                if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (isFavorite) "إزالة من المفضلة" else "إضافة للمفضلة",
                                tint = if (isFavorite) PrimaryRed else TextSecondary
                            )
                        }
                    }
                    Row(Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                        Text("⭐ ${movie?.rating ?: 0.0}", color = PrimaryRed, fontSize = 14.sp)
                        Text(" • ${movie?.duration ?: 0} دقيقة", color = TextSecondary, fontSize = 14.sp)
                    }
                    Text(movie?.genre?.joinToString(", ").orEmpty(), color = TextSecondary, fontSize = 14.sp, modifier = Modifier.padding(bottom = 16.dp))
                    Divider(color = BackgroundTertiary, thickness = 1.dp)
                    Text("الوصف", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp))
                    Text(movie?.description.orEmpty(), color = TextSecondary, fontSize = 14.sp, modifier = Modifier.padding(bottom = 24.dp))
                }
            }
            item {
                MovieReviewsSection(
                    reviews = reviews,
                    rating = reviewRating,
                    authorName = reviewAuthorName,
                    comment = reviewComment,
                    isSubmitting = isSubmittingReview,
                    errorMessage = reviewError,
                    submitted = reviewSubmitted,
                    onRatingChange = viewModel::setReviewRating,
                    onAuthorNameChange = viewModel::setReviewAuthorName,
                    onCommentChange = viewModel::setReviewComment,
                    onSubmit = viewModel::submitReview
                )
            }
            item {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Text("مصادر التشغيل المعتمدة", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                    if (isLoadingVideo) {
                        Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = PrimaryRed)
                        }
                    }
                    errorMessage?.let { message ->
                        Card(
                            Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = ErrorColor.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(8.dp)
                        ) { Text(message, color = ErrorColor, modifier = Modifier.padding(12.dp), fontSize = 12.sp) }
                    }
                    videoLinks.forEach { stream ->
                        ApprovedStreamCard(
                            stream = stream,
                            isSelected = selectedVideoLink?.url == stream.url,
                            onSelect = { viewModel.selectVideoLink(stream) },
                            onPlay = { onPlayClick(stream.url, movie?.title.orEmpty()) }
                        )
                    }
                }
            }
            item {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
                        onClick = { selectedVideoLink?.let { onPlayClick(it.url, movie?.title.orEmpty()) } },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryRed,
                            disabledContainerColor = PrimaryRed.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = selectedVideoLink != null
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "تشغيل", tint = Color.White, modifier = Modifier.padding(end = 8.dp))
                        Text("مشاهدة الآن", color = Color.White)
                    }
                }
            }
            item { Box(Modifier.padding(bottom = 24.dp)) }
        }
    } else {
        Box(Modifier.fillMaxSize().background(BackgroundPrimary), contentAlignment = Alignment.Center) {
            Text("لم يتم العثور على الفيلم", color = TextSecondary)
        }
    }
}

@Composable
private fun ApprovedStreamCard(
    stream: ApprovedStream,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onPlay: () -> Unit
) {
    Card(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) BackgroundTertiary else BackgroundSecondary),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(stream.serverName, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("الجودة: ${stream.quality}", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }
                if (isSelected) Text("✓", color = PrimaryRed, fontSize = 20.sp)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onSelect, modifier = Modifier.weight(1f).height(36.dp), shape = RoundedCornerShape(6.dp)) {
                    Text(if (isSelected) "مختار" else "اختيار", fontSize = 12.sp)
                }
                Button(
                    onClick = onPlay,
                    modifier = Modifier.weight(1f).height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = "تشغيل", tint = Color.White, modifier = Modifier.padding(end = 4.dp))
                    Text("تشغيل", fontSize = 12.sp)
                }
            }
        }
    }
}
