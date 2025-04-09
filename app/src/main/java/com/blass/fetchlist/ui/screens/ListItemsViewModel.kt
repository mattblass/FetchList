package com.blass.fetchlist.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blass.fetchlist.data.ListApi
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
class ListItemsViewModel @Inject constructor() : ViewModel() {

    private val _sortOrderListId = MutableStateFlow<SortOrder>(SortOrder.ASC)
    private var _sortOrderName = MutableStateFlow<SortOrder>(SortOrder.ASC)
    private val _listItems = MutableStateFlow<List<ListItemNameFiltered>>(listOf())
    private val _uiState = MutableStateFlow<ListItemsUiState>(ListItemsUiState.Loading)
    val uiState: StateFlow<ListItemsUiState> = _uiState.asStateFlow()

    init {
        getList()
    }

    fun getList() {
        viewModelScope.launch {
            _uiState.value = try {
                _listItems.value = nameFilter(ListApi.retrofitService.getList())
                    .sortedWith(getSortListIdOrder(_sortOrderListId.value))
                ListItemsUiState.Success(
                    _listItems.value,
                    _sortOrderListId.value,
                    _sortOrderName.value
                )
            } catch (e: IOException) {
                ListItemsUiState.Error
            } catch (e: HttpException) {
                ListItemsUiState.Error
            }
        }
    }

    fun sortListId() {
        viewModelScope.launch {
            if (_sortOrderListId.value == SortOrder.ASC) {
                _sortOrderListId.value = SortOrder.DESC
                _sortOrderName.value = SortOrder.ASC
            } else {
                _sortOrderListId.value = SortOrder.ASC
                _sortOrderName.value = SortOrder.ASC
            }

            _listItems.value =
                _listItems.value.sortedWith(getSortListIdOrder(_sortOrderListId.value))
            _uiState.value =
                ListItemsUiState.Success(
                    _listItems.value,
                    _sortOrderListId.value,
                    _sortOrderName.value
                )
        }
    }

    fun sortName() {
        viewModelScope.launch {
            if (_sortOrderName.value == SortOrder.ASC) {
                _sortOrderName.value = SortOrder.DESC
                _sortOrderListId.value = SortOrder.ASC
            } else {
                _sortOrderName.value = SortOrder.ASC
                _sortOrderListId.value = SortOrder.ASC
            }

            _listItems.value =
                _listItems.value.sortedWith(getSortNameOrder(_sortOrderName.value))
            _uiState.value =
                ListItemsUiState.Success(
                    _listItems.value,
                    _sortOrderListId.value,
                    _sortOrderName.value
                )
        }
    }
}