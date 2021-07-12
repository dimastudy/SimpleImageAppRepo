package com.example.simplephotoapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.simplephotoapp.data.models.Photo
import com.example.simplephotoapp.network.UnsplashApiService
import retrofit2.HttpException
import java.io.IOException

private const val START_INDEX = 1

class PhotoPagingSource(
    private val photoApi: UnsplashApiService,
    private val query: String
) : PagingSource<Int, Photo>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val position = params.key ?: START_INDEX

        return try {
            val response = photoApi.getPhotoResult(query, position, params.loadSize)
            val photos = response.results
            LoadResult.Page(
                data = photos,
                prevKey = if (position == START_INDEX) null else position - 1,
                nextKey = if (photos.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        val anchorPosition = state.anchorPosition?: return null
        val page = state.closestPageToPosition(anchorPosition)?: return null
        return page.prevKey?.plus(1)?: page.nextKey?.minus(1)
    }

}