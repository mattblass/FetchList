package com.blass.fetchlist.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.blass.fetchlist.R
import com.blass.fetchlist.ui.theme.FetchListTheme

@Composable
fun SortedListViewHeader(
    title: String,
    sortOrder: SortOrder,
    modifier: Modifier = Modifier,
    onSort: () -> Unit
) {
    ListViewHeader(
        title,
        modifier,
        if (sortOrder == SortOrder.ASC) {
            Icons.Default.KeyboardArrowDown
        } else {
            Icons.Default.KeyboardArrowUp
        },
        if (sortOrder == SortOrder.ASC) {
            stringResource(R.string.sorted_asc_description)
        } else {
            stringResource(R.string.sorted_des_description)
        },
        onSort
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSortedListViewHeader() {
    FetchListTheme {
        SortedListViewHeader(
            "Header Title",
            SortOrder.ASC,
            Modifier
        ) { }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSortedListViewHeaderDesc() {
    FetchListTheme {
        SortedListViewHeader(
            "Header Title",
            SortOrder.DESC,
            Modifier
        ) { }
    }
}