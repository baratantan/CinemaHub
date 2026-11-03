package com.cinemahub.app.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cinemahub.app.ui.theme.BackgroundTertiary
import com.cinemahub.app.ui.theme.TextPrimary
import com.cinemahub.app.ui.theme.TextSecondary

@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSearchClick: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp)
            .then(
                if (onSearchClick != null) Modifier.clickable(onClick = onSearchClick)
                else Modifier
            ),
        readOnly = onSearchClick != null,
        placeholder = {
            Text("ابحث عن أفلام ومسلسلات...", color = TextSecondary)
        },
        leadingIcon = {
            Icon(Icons.Filled.Search, contentDescription = "بحث", tint = TextSecondary)
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = BackgroundTertiary,
            focusedContainerColor = BackgroundTertiary,
            unfocusedTextColor = TextPrimary,
            focusedTextColor = TextPrimary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = TextPrimary
        ),
        shape = RoundedCornerShape(24.dp),
        singleLine = true
    )
}
