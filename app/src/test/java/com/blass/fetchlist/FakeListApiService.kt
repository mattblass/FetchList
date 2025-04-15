package com.blass.fetchlist

import com.blass.fetchlist.data.ListApiService
import com.blass.fetchlist.data.ListItem
import java.io.IOException

class FakeListApiService(
    private val itemsToReturn: List<ListItem> = emptyList(),
    private val shouldThrow: Boolean = false
) : ListApiService {
    override suspend fun getList(): List<ListItem> {
        if (shouldThrow) throw IOException("Simulated network error")
        return itemsToReturn
    }
}