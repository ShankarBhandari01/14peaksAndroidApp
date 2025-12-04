package com.example.restro.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.restro.data.model.ApiResponse

class ApiPagingSource<T : Any>(
    private val fetchData: suspend (page: Int, limit: Int) -> ApiResponse<T>
) : PagingSource<Int, T>() {

    // refresh key
    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1) ?: state.closestPageToPosition(
                position
            )?.nextKey?.minus(1)
        }
    }

    // load data
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val page = params.key ?: 1
            val limit = params.loadSize

            val response = fetchData(page, limit)

            val pagination = response.pagination
            val payload = response.data
            val nextKey = if (page < pagination.totalPages) page + 1 else null

            LoadResult.Page(
                data = payload,
                prevKey = if (page == 1) null else page - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}