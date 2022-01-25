package com.codingnight.example.gallery

import android.content.Context
import android.util.Log
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson

class PixabayDataSource(private val context: Context) : PageKeyedDataSource<Int, PhotoItem>() {

    private val queryKey =
        arrayOf("cat", "dog", "car", "beauty", "phone", "computer", "flower", "animal").random()


    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, PhotoItem>
    ) {
        val url =
            "https://pixabay.com/api/?key=12472743-874dc01dadd26dc44e0801d61&q=${queryKey}&per_page=${50}&page=1"
        StringRequest(
            Request.Method.GET,
            url,
            { it ->
                callback.onResult(
                    Gson().fromJson(it, Pixabay::class.java).hits.toList(),
                    null,
                    2
                )
            },
            {
                Log.d("PixabayDataSource", "loadInitial: $it")
            }
        ).also {
            VolleySingleton.getInstance(context).requestQueue.add(it)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoItem>) {
        TODO("Not yet implemented")
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoItem>) {
        val url =
            "https://pixabay.com/api/?key=12472743-874dc01dadd26dc44e0801d61&q=${queryKey}&per_page=${50}&page=${params.key}"
        StringRequest(
            Request.Method.GET,
            url,
            {
                callback.onResult(
                    Gson().fromJson(it, Pixabay::class.java).hits.toList(),
                    params.key + 1
                )
            },
            {
                Log.d("PixabayDataSource", "loadInitial: $it")
            }
        ).also {
            VolleySingleton.getInstance(context).requestQueue.add(it)
        }
    }
}

class PixabayDataSourceFactory(private val context: Context): DataSource.Factory<Int, PhotoItem>() {
    override fun create(): DataSource<Int, PhotoItem> {
        return PixabayDataSource(context)
    }
}