package com.blass.fetchlist

import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blass.fetchlist.ui.screens.ListItemsUiState
import com.blass.fetchlist.ui.screens.ListItemsView
import com.blass.fetchlist.ui.screens.ListItemsViewModel
import com.blass.fetchlist.ui.theme.FetchListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FetchListTheme {
                HomeScreen()
            }
        }
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: ListItemsViewModel = viewModel(),
) {
    val uiState: ListItemsUiState by viewModel.uiState.collectAsStateWithLifecycle()
    Surface(modifier = Modifier.padding(top = 26.dp)) {
        LoadingScreen(
            uiState,
            modifier,
            { viewModel.sortListId() },
            { viewModel.sortName() }
        )
    }
}

@Composable
fun LoadingScreen(
    uiState: ListItemsUiState,
    modifier: Modifier = Modifier,
    onSortListId: () -> Unit,
    onSortName: () -> Unit,
) {
    when (uiState) {
        is ListItemsUiState.Error -> ErrorScreen()
        is ListItemsUiState.Loading -> LoadingScreen()
        is ListItemsUiState.Success -> ListItemsView(
            uiState.listItems,
            uiState.sortListId,
            uiState.sortName,
            modifier,
            onSortListId,
            onSortName
        )
    }
}

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.errorContainer)
        .wrapContentSize(Alignment.Center)
    ) {
        Text("Error", color = MaterialTheme.colorScheme.error)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewErrorScreen() {
    FetchListTheme {
        ErrorScreen()
    }
}

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.Center)
    ) {
        Text("Loading...")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoadingScreen() {
    FetchListTheme {
        LoadingScreen()
    }
}