package com.blass.fetchlist.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blass.fetchlist.data.ListApiService
import com.blass.fetchlist.data.ListItem
import com.blass.fetchlist.data.ListItemNameFiltered
import com.blass.fetchlist.data.getNameParts
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val apiService: ListApiService
) : ViewModel() {

    private val _listItems = MutableStateFlow<List<ListItemNameFiltered>>(listOf())
    private val _uiState = MutableStateFlow<ListItemsUiState>(ListItemsUiState.Loading)
    val uiState: StateFlow<ListItemsUiState> = _uiState.asStateFlow()

    init {
        fetchList()
    }

    fun fetchList() {
        viewModelScope.launch {
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
            var listIdSortOrder = SortOrder.ASC
            var listNameSortOrder = SortOrder.ASC
            val uiState = _uiState.value
            when (uiState) {
                is ListItemsUiState.Success -> {
                    if (uiState.sortListId == SortOrder.ASC) {
                        listIdSortOrder = SortOrder.DESC
                        listNameSortOrder = SortOrder.ASC
                    } else {
                        listIdSortOrder = SortOrder.ASC
                        listNameSortOrder = SortOrder.ASC
                    }
                }
                ListItemsUiState.Error -> Unit
                ListItemsUiState.Loading -> Unit
            }

            _listItems.value =
                _listItems.value.sortedWith(getSortListIdOrder(listIdSortOrder))
            _uiState.value =
                ListItemsUiState.Success(
                    _listItems.value,
                    listIdSortOrder,
                    listNameSortOrder
                )
        }
    }

    fun sortName() {
        viewModelScope.launch {
            var listIdSortOrder = SortOrder.ASC
            var listNameSortOrder = SortOrder.ASC
            val uiState = _uiState.value
            when (uiState) {
                is ListItemsUiState.Success -> {
                    if (uiState.sortName == SortOrder.ASC) {
                        listIdSortOrder = SortOrder.ASC
                        listNameSortOrder = SortOrder.DESC
                    } else {
                        listIdSortOrder = SortOrder.ASC
                        listNameSortOrder = SortOrder.ASC
                    }
                }
                ListItemsUiState.Error -> Unit
                ListItemsUiState.Loading -> Unit
            }

            _listItems.value =
                _listItems.value.sortedWith(getSortNameOrder(listNameSortOrder))
            _uiState.value =
                ListItemsUiState.Success(
                    _listItems.value,
                    listIdSortOrder,
                    listNameSortOrder
                )
        }
    }
}