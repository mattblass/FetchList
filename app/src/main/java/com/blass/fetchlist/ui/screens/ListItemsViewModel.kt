package com.blass.fetchlist.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blass.fetchlist.data.ListApiService
import com.blass.fetchlist.data.ListItem
import com.blass.fetchlist.data.ListItemNameFiltered
import com.blass.fetchlist.data.getNameParts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

enum class SortOrder {
    ASC,
    DESC
}

fun SortOrder.reverse(): SortOrder = if (this == SortOrder.ASC) SortOrder.DESC else SortOrder.ASC

sealed interface ListItemsUiState {
    data class Success(
        val listItems: List<ListItemNameFiltered>,
        val sortListId: SortOrder = SortOrder.ASC,
        val sortName: SortOrder = SortOrder.ASC
    ) : ListItemsUiState

    object Error : ListItemsUiState
    object Loading : ListItemsUiState
}

private fun nameFilter(listItems: List<ListItem>): List<ListItemNameFiltered> =
    listItems.mapNotNull {
        if (!it.name.isNullOrEmpty()) {
            val nameParts = getNameParts(it.name)
            ListItemNameFiltered(it.id, it.listId, it.name, nameParts.first, nameParts.second)
        } else {
            null
        }
    }

private fun sortListIdAscending(): Comparator<ListItemNameFiltered> =
    compareBy<ListItemNameFiltered> { it.listId }
        .thenBy {
            it.namePart
        }.thenBy {
            it.nameNumberPart
        }

private fun sortListIdDescending(): Comparator<ListItemNameFiltered> =
    compareByDescending<ListItemNameFiltered> { it.listId }
        .thenBy {
            it.namePart
        }.thenBy {
            it.nameNumberPart
        }

private fun sortListNameAscending(): Comparator<ListItemNameFiltered> =
    compareBy<ListItemNameFiltered> { it.listId }
        .thenBy {
            it.namePart
        }.thenBy {
            it.nameNumberPart
        }

private fun sortListNameDescending(): Comparator<ListItemNameFiltered> =
    compareBy<ListItemNameFiltered> { it.listId }
        .thenByDescending {
            it.namePart
        }.thenByDescending {
            it.nameNumberPart
        }

private fun getSortListIdOrder(sortOrder: SortOrder): Comparator<ListItemNameFiltered> =
    if (sortOrder == SortOrder.ASC) {
        sortListIdAscending()
    } else {
        sortListIdDescending()
    }

private fun getSortNameOrder(sortOrder: SortOrder): Comparator<ListItemNameFiltered> =
    if (sortOrder == SortOrder.ASC) {
        sortListNameAscending()
    } else {
        sortListNameDescending()
    }

@HiltViewModel
class ListItemsViewModel @Inject constructor(
    private val apiService: ListApiService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _listItems = MutableStateFlow<List<ListItemNameFiltered>>(listOf())
    private val _uiState = MutableStateFlow<ListItemsUiState>(ListItemsUiState.Loading)
    val uiState: StateFlow<ListItemsUiState> = _uiState.asStateFlow()

    init {
        fetchList()
    }

    fun fetchList() {
        viewModelScope.launch(dispatcher) {
            _uiState.value = try {
                _listItems.value = nameFilter(apiService.getList())
                    .sortedWith(getSortListIdOrder(SortOrder.ASC))
                ListItemsUiState.Success(
                    _listItems.value,
                    SortOrder.ASC,
                    SortOrder.ASC
                )
            } catch (_: IOException) {
                ListItemsUiState.Error
            } catch (_: HttpException) {
                ListItemsUiState.Error
            }
        }
    }

    fun sortListId() {
        viewModelScope.launch {
            val uiState = _uiState.value
            when (uiState) {
                is ListItemsUiState.Success -> {
                    uiState.sortListId.reverse().let { updatedOrder ->
                        val sorted = _listItems.value.sortedWith(getSortListIdOrder(updatedOrder))
                        _listItems.value = sorted
                        _uiState.value = ListItemsUiState.Success(
                            _listItems.value,
                            updatedOrder,
                            SortOrder.ASC
                        )
                    }
                }
                ListItemsUiState.Error -> Unit
                ListItemsUiState.Loading -> Unit
            }
        }
    }

    fun sortName() {
        viewModelScope.launch {
            val uiState = _uiState.value
            when (uiState) {
                is ListItemsUiState.Success -> {
                    uiState.sortName.reverse().let { updatedOrder ->
                        val sorted = _listItems.value.sortedWith(getSortNameOrder(updatedOrder))
                        _listItems.value = sorted
                        _uiState.value = ListItemsUiState.Success(
                            _listItems.value,
                            SortOrder.ASC,
                            updatedOrder
                        )
                    }
                }
                ListItemsUiState.Error -> Unit
                ListItemsUiState.Loading -> Unit
            }
        }
    }
}