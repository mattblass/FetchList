package com.blass.fetchlist.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

private const val LIST_URL = "https://fetch-hiring.s3.amazonaws.com/"
private val retrofit = Retrofit.Builder()
    .baseUrl(LIST_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

object ListApi {
    val retrofitService: ListApiService by lazy {
        retrofit.create(ListApiService::class.java)
    }
}