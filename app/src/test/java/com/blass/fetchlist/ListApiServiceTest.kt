package com.blass.fetchlist

import com.blass.fetchlist.data.ListItem
import com.blass.fetchlist.ui.screens.ListItemsUiState
import com.blass.fetchlist.ui.screens.ListItemsViewModel
import com.blass.fetchlist.ui.screens.SortOrder
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import app.cash.turbine.test
import kotlinx.coroutines.test.StandardTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class ListItemsViewModelTest {

    @Test
    fun `fetchList emits Success when data is loaded`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val fakeApi = FakeListApiService(
            itemsToReturn = listOf(
                ListItem(1, 100, "Item 1"),
                ListItem(2, 100, "Item 2")
            )
        )
        val viewModel = ListItemsViewModel(fakeApi, dispatcher)

        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assert(state is ListItemsUiState.Success)

            state as ListItemsUiState.Success
            assertEquals(2, state.listItems.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `fetchList emits Error on IOException`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val fakeApi = FakeListApiService(shouldThrow = true)
        val viewModel = ListItemsViewModel(fakeApi, dispatcher)

        advanceUntilIdle()

        viewModel.uiState.test {
            val error = awaitItem()
            assert(error is ListItemsUiState.Error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sortListId toggles to DESC and emits updated Success state`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val fakeApi = FakeListApiService(
            itemsToReturn = listOf(
                ListItem(1, 200, "Item 1"),
                ListItem(2, 100, "Item 2")
            )
        )
        val viewModel = ListItemsViewModel(fakeApi, dispatcher)

        viewModel.uiState.test {
            val loading = awaitItem()
            assert(loading is ListItemsUiState.Loading)

            val initial = awaitItem()
            assert(initial is ListItemsUiState.Success)
            initial as ListItemsUiState.Success
            assertEquals(SortOrder.ASC, initial.sortListId)

            viewModel.sortListId()

            val updated = awaitItem()
            assert(updated is ListItemsUiState.Success)
            updated as ListItemsUiState.Success
            assertEquals(SortOrder.DESC, updated.sortListId)

            val sortedIds = updated.listItems.map { it.listId }
            assertEquals(listOf(200, 100), sortedIds)

            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `sortName toggles to DESC and emits updated Success state`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val fakeApi = FakeListApiService(
            itemsToReturn = listOf(
                ListItem(1, 100, "Item 1"),
                ListItem(2, 100, "Item 2")
            )
        )
        val viewModel = ListItemsViewModel(fakeApi, dispatcher)

        viewModel.uiState.test {
            val loading = awaitItem()
            assert(loading is ListItemsUiState.Loading)

            val initial = awaitItem()
            assert(initial is ListItemsUiState.Success)
            initial as ListItemsUiState.Success
            assertEquals(SortOrder.ASC, initial.sortName)

            viewModel.sortName()

            val updated = awaitItem()
            assert(updated is ListItemsUiState.Success)
            updated as ListItemsUiState.Success
            assertEquals(SortOrder.DESC, updated.sortName)
            assertEquals(listOf("Item 2", "Item 1"), updated.listItems.map { it.name })

            cancelAndIgnoreRemainingEvents()
        }
    }

}

