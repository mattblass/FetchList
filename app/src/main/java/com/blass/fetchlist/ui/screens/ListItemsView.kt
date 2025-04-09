package com.blass.fetchlist.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blass.fetchlist.R
import com.blass.fetchlist.data.ListItemNameFiltered
import com.blass.fetchlist.ui.theme.FetchListTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListItemsView(
    listItems: List<ListItemNameFiltered>,
    sortOrderListId: SortOrder,
    sortOrderName: SortOrder,
    modifier: Modifier = Modifier,
    onSortListId: () -> Unit,
    onSortName: () -> Unit,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        stickyHeader {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SortedListViewHeader(
                    stringResource(R.string.list_id_header),
                    sortOrderListId,
                    modifier
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .weight(1f)
                        .padding(4.dp),
                    onSortListId
                )
                SortedListViewHeader(
                    stringResource(R.string.name_header),
                    sortOrderName,
                    modifier
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .weight(1f)
                        .padding(4.dp),
                    onSortName
                )
            }
        }
        items(listItems) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(it.listId.toString(), modifier.weight(1f))
                Text(it.name, modifier.weight(1f))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewListItems() {
    val list = List(100) {
        ListItemNameFiltered(it,it,"Item $it", "Item", it)
    }

    FetchListTheme {
        ListItemsView(
            listItems = list,
            SortOrder.ASC,
            SortOrder.ASC,
            Modifier,
            {},
            {}
        )
    }
}
