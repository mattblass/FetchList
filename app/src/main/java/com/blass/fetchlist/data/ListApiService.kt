package com.blass.fetchlist.data

import retrofit2.http.GET

data class ListItem(val id: Int, val listId: Int, val name: String?)
data class ListItemNameFiltered(
    val id: Int,
    val listId: Int,
    val name: String,
    val namePart: String,
    val nameNumberPart: Int
)

fun getNameParts(name: String): Pair<String, Int> {
    val parts = name.trim().split(" ")
    return Pair(parts[0], parts[1].toIntOrNull() ?: 0)
}

interface ListApiService {
    @GET("hiring.json")
    suspend fun getList(): List<ListItem>
}