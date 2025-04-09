package com.blass.fetchlist.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.blass.fetchlist.ui.theme.FetchListTheme

@Composable
fun ListViewHeader(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector?,
    iconDescription: String?,
    onHeaderClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.clickable(
            onClick = onHeaderClick
        )
    ) {
        Text(title)
        if (icon != null) {
            Icon(
                icon,
                iconDescription
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewListViewHeader() {
    FetchListTheme {
        ListViewHeader(
            "Header Title",
            Modifier,
            Icons.Default.KeyboardArrowUp,
            "Content Description"
        ) { }
    }
}