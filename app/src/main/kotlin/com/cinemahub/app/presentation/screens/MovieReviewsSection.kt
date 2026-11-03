package com.cinemahub.app.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cinemahub.app.domain.model.MovieReview
import com.cinemahub.app.ui.theme.AccentGold
import com.cinemahub.app.ui.theme.BackgroundSecondary
import com.cinemahub.app.ui.theme.BackgroundTertiary
import com.cinemahub.app.ui.theme.ErrorColor
import com.cinemahub.app.ui.theme.PrimaryRed
import com.cinemahub.app.ui.theme.SuccessColor
import com.cinemahub.app.ui.theme.TextPrimary
import com.cinemahub.app.ui.theme.TextSecondary

@Composable
fun MovieReviewsSection(
    reviews: List<MovieReview>,
    rating: Int,
    authorName: String,
    comment: String,
    isSubmitting: Boolean,
    errorMessage: String?,
    submitted: Boolean,
    onRatingChange: (Int) -> Unit,
    onAuthorNameChange: (String) -> Unit,
    onCommentChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Text("تقييمات ومراجعات المستخدمين", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        if (reviews.isNotEmpty()) {
            val average = reviews.map(MovieReview::rating).average()
            Text(
                "متوسط التقييم ${"%.1f".format(java.util.Locale.ROOT, average)} من 5 • ${reviews.size} مراجعة",
                color = AccentGold,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
            )
        } else {
            Text("لا توجد مراجعات بعد. كن أول من يقيّم هذا الفيلم.", color = TextSecondary, fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp, bottom = 12.dp))
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BackgroundSecondary),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(Modifier.padding(12.dp)) {
                Text("تقييمك", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    (1..5).forEach { value ->
                        val selected = value <= rating
                        Text(
                            text = if (selected) "★" else "☆",
                            color = if (selected) AccentGold else TextSecondary,
                            fontSize = 30.sp,
                            modifier = Modifier
                                .clickable { onRatingChange(value) }
                                .semantics { contentDescription = "تقييم $value من 5 نجوم" }
                                .padding(horizontal = 3.dp)
                        )
                    }
                }
                OutlinedTextField(
                    value = authorName,
                    onValueChange = onAuthorNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("اسم العرض") },
                    singleLine = true,
                    colors = reviewTextFieldColors()
                )
                OutlinedTextField(
                    value = comment,
                    onValueChange = onCommentChange,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    label = { Text("اكتب مراجعتك") },
                    placeholder = { Text("ما رأيك في الفيلم؟") },
                    minLines = 3,
                    maxLines = 5,
                    supportingText = { Text("${comment.length}/1000") },
                    colors = reviewTextFieldColors()
                )
                errorMessage?.let {
                    Text(it, color = ErrorColor, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                }
                if (submitted) {
                    Text("تم حفظ مراجعتك سحابياً.", color = SuccessColor, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp))
                }
                Button(
                    onClick = onSubmit,
                    enabled = !isSubmitting,
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isSubmitting) CircularProgressIndicator()
                    else Text("إرسال التقييم والمراجعة", color = TextPrimary)
                }
            }
        }

        reviews.forEach { review ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundSecondary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(review.authorName.ifBlank { "مستخدم" }, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        Text("${"★".repeat(review.rating.coerceIn(0, 5))}  ${review.rating}/5", color = AccentGold, fontSize = 13.sp)
                    }
                    if (review.comment.isNotBlank()) {
                        Text(review.comment, color = TextSecondary, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
                    }
                    if (review.createdAt > 0L) {
                        Text(
                            java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM, java.util.Locale("ar")).format(java.util.Date(review.createdAt)),
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun reviewTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedBorderColor = PrimaryRed,
    unfocusedBorderColor = BackgroundTertiary,
    focusedLabelColor = PrimaryRed,
    unfocusedLabelColor = TextSecondary,
    cursorColor = PrimaryRed
)
