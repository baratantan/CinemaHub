package com.cinemahub.app.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cinemahub.app.domain.model.Movie

private val availableGenres = listOf(
    "الكل", "أكشن", "كوميديا", "دراما", "رعب", "رومانسية", "خيال علمي",
    "مغامرة", "إثارة", "أنمي", "وثائقي"
)

enum class MovieSortOrder(val label: String) {
    NEWEST("الأحدث"), TOP_RATED("الأعلى تقييمًا"), TITLE("الاسم")
}

@Composable
fun MovieFilterPanel(
    selectedGenre: String,
    onGenreSelected: (String) -> Unit,
    year: String,
    onYearChanged: (String) -> Unit,
    minimumRating: Float,
    onMinimumRatingChanged: (Float) -> Unit,
    sortOrder: MovieSortOrder,
    onSortOrderChanged: (MovieSortOrder) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("تصفية وفرز", modifier = Modifier.padding(bottom = 4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterDropdown(
                label = "النوع",
                value = selectedGenre,
                options = availableGenres,
                onSelect = onGenreSelected,
                modifier = Modifier.weight(1f)
            )
            FilterDropdown(
                label = "الترتيب",
                value = sortOrder.label,
                options = MovieSortOrder.values().map { it.label },
                onSelect = { label -> MovieSortOrder.values().firstOrNull { it.label == label }?.let(onSortOrderChanged) },
                modifier = Modifier.weight(1f)
            )
        }
        OutlinedTextField(
            value = year,
            onValueChange = { input -> onYearChanged(input.filter(Char::isDigit).take(4)) },
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            label = { Text("سنة الإنتاج") },
            placeholder = { Text("مثال: 2024") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Text("الحد الأدنى للتقييم: ${String.format("%.1f", minimumRating)} / 10")
        Slider(
            value = minimumRating,
            onValueChange = onMinimumRatingChanged,
            valueRange = 0f..10f,
            steps = 19
        )
        TextButton(onClick = onReset) { Text("مسح عوامل التصفية") }
    }
}

@Composable
private fun FilterDropdown(
    label: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        TextButton(onClick = { expanded = true }) {
            Text("$label: $value")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun filterAndSortMovies(
    movies: List<Movie>,
    genre: String,
    year: String,
    minimumRating: Float,
    sortOrder: MovieSortOrder
): List<Movie> {
    val requestedYear = year.toIntOrNull()?.takeIf { year.length == 4 }
    val filtered = movies.filter { movie ->
        val matchesGenre = genre == "الكل" || movie.genre.any { it.contains(genre, ignoreCase = true) }
        val movieYear = Regex("\\d{4}").find(movie.releaseDate)?.value?.toIntOrNull()
        val matchesYear = requestedYear == null || movieYear == requestedYear
        matchesGenre && matchesYear && movie.rating >= minimumRating
    }
    return when (sortOrder) {
        MovieSortOrder.NEWEST -> filtered.sortedWith(
            compareByDescending<Movie> { Regex("\\d{4}").find(it.releaseDate)?.value?.toIntOrNull() ?: 0 }
                .thenByDescending { it.releaseDate }
        )
        MovieSortOrder.TOP_RATED -> filtered.sortedByDescending { it.rating }
        MovieSortOrder.TITLE -> filtered.sortedBy { it.title.lowercase() }
    }
}
