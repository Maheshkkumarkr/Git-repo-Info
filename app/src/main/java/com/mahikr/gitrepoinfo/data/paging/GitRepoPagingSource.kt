package com.mahikr.gitrepoinfo.data.paging


import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mahikr.gitrepoinfo.domain.model.GitRepo
import com.mahikr.gitrepoinfo.domain.usecase.db.ClearReposUseCase
import com.mahikr.gitrepoinfo.domain.usecase.db.GetAllGitReposUserCase
import com.mahikr.gitrepoinfo.domain.usecase.db.GetGitReposUseCase
import com.mahikr.gitrepoinfo.domain.usecase.db.InsertAllReposUseCase
import com.mahikr.gitrepoinfo.domain.usecase.network.GetRepositoriesUseCase
import com.mahikr.gitrepoinfo.util.toGitRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch


private const val TAG = "GitRepoPagingSource_TAG"


class GitRepoPagingSource(
    private val query: String,
    private val fromCache: Boolean = false,
    private val getAllGitReposUserCase: GetAllGitReposUserCase,
    private val getGitReposUseCase: GetGitReposUseCase,
    private val clearReposUseCase: ClearReposUseCase,
    private val insertAllReposUseCase: InsertAllReposUseCase,
    private val getRepositoriesUseCase: GetRepositoriesUseCase,
) : PagingSource<Int, GitRepo>() {

    companion object {
        var previousQuery: String? = null // Store the previous query for comparison
    }

    override fun getRefreshKey(state: PagingState<Int, GitRepo>): Int? {
        // Get the refresh key to resume paging from the current position
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GitRepo> {
        val page = params.key ?: 1 // Current page number
        val perPage = params.loadSize // Number of items per page
        Log.d(TAG, "fromCache $fromCache $query ")

        return try {
            // Retry logic with incremental delay
            retryWithExponentialBackoff(
                times = 3,
                initialDelayMillis = 1000,
                maxDelayMillis = 5000
            ) {
                if (fromCache) {
                    //fetch data from db and checking the newQuery and existing repo query, if not matches throws exception
                    getAllGitReposUserCase()
                        .firstOrNull() // Get the first emission from the flow
                        ?.let { allRepos ->
                            val oldQuery = allRepos.firstOrNull()?.query
                            Log.d(TAG, "load: oldQuery $oldQuery vs new query $query")
                            previousQuery = oldQuery
                            if (previousQuery == null) {
                                throw Exception("No data found for the query $query " + if (previousQuery != null) "But yet,You can see the data on $previousQuery" else "")
                            }
                        }
                    // Load data from the database (cache) using the repository
                    getGitReposUseCase(previousQuery!!)
                        .first()
                        .let { allRepos ->
                            // Paginate the data from the database
                            val startIndex = (page - 1) * perPage
                            val endIndex = minOf(startIndex + perPage, allRepos.size)
                            val paginatedRepos = allRepos.subList(startIndex, endIndex)

                            // Handle empty data scenarios
                            if (paginatedRepos.isEmpty()) {
                                if (page == 1) {
                                    throw Exception("No data found for the query $query " + if (previousQuery != null) "But yet,You can see the data on $previousQuery" else "")
                                } else {
                                    return@retryWithExponentialBackoff LoadResult.Page(
                                        data = emptyList(),
                                        prevKey = if (page == 1) null else page - 1,
                                        nextKey = null
                                    )
                                }
                            }

                            // Return the paginated data
                            LoadResult.Page(
                                data = paginatedRepos.map { it.toGitRepo() },
                                prevKey = if (page == 1) null else page - 1,
                                nextKey = if (endIndex >= allRepos.size) null else page + 1
                            )
                        }
                } else {
                    // Load data from the network and update the database using the repository
                    val response = getRepositoriesUseCase(query, perPage, page)
                        .items.map { it.toGitRepo(query = query) }

                    // Handle empty data scenarios
                    if (response.isEmpty()) {
                        throw Exception("No data found for the query $query")
                    }

                    // Update database if query has changed and the response is successful
                    if (previousQuery != null && previousQuery != query && response.isNotEmpty()) {
                        clearReposUseCase() // Clear previous data from the database
                        previousQuery = query // Update previousQuery to the current query
                    }

                    // Insert the fetched data into the database
                    if (response.isNotEmpty()) {
                        insertAllReposUseCase(response.map { it.toGitRepo() })
                        Log.d(TAG, "response inserted into db....")
                    }

                    // Return the loaded data
                    LoadResult.Page(
                        data = response,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (response.isEmpty()) null else page + 1
                    )
                }
            }
        } catch (e: Exception) {
            LoadResult.Error(e) // Return an error if loading fails after retries
        }
    }
}

/**
 * Retries the given block of code with exponential backoff.
 *
 * @param times The number of times to retry.
 * @param initialDelayMillis The initial delay in milliseconds.
 * @param maxDelayMillis The maximum delay in milliseconds.
 * @param block The block of code to retry.
 * @return The result of the block of code, or null if it failed after all retries.
 */
suspend fun <T> retryWithExponentialBackoff(
    times: Int,
    initialDelayMillis: Long,
    maxDelayMillis: Long,
    block: suspend () -> T,
): T {
    var currentDelay = initialDelayMillis
    repeat(times - 1) { attempt ->
        try {
            return block()
        } catch (e: Exception) {
            // Log the exception or handle it as needed
            Log.e(TAG, "Retry attempt $attempt failed: ${e.message}", e)
            delay(currentDelay)
            currentDelay = (currentDelay * 2).coerceAtMost(maxDelayMillis) // Exponential backoff
        }
    }
    return block() // Last attempt without catching exceptions
}


/*
class GitRepoPagingSource(
    private val query: String,
    private val fromCache: Boolean = false,
    private val getAllGitReposUserCase: GetAllGitReposUserCase,
    private val getGitReposUseCase: GetGitReposUseCase,
    private val clearReposUseCase: ClearReposUseCase,
    private val insertAllReposUseCase: InsertAllReposUseCase,
    private val getRepositoriesUseCase: GetRepositoriesUseCase,
) : PagingSource<Int, GitRepo>() {

    companion object {
        var previousQuery: String? = null // Store the previous query for comparison
    }

    override fun getRefreshKey(state: PagingState<Int, GitRepo>): Int? {
        // Get the refresh key to resume paging from the current position
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GitRepo> {
        val page = params.key ?: 1 // Current page number
        val perPage = params.loadSize // Number of items per page
        Log.d(TAG, "fromCache $fromCache $query ")

        return if (fromCache) {
            // Load data from the database (cache)
            try {
                val response = getGitReposUseCase(query) // Get all repos for the query from DB
                    .first() // Get the first emitted list of repos
                    .let { allRepos ->
                        // Paginate the data from the database
                        val startIndex = (page - 1) * perPage
                        val endIndex = minOf(startIndex + perPage, allRepos.size)
                        val paginatedRepos = allRepos.subList(startIndex, endIndex)

                        // Handle empty data scenarios
                        if (paginatedRepos.isEmpty()) {
                            if (page == 1) {
                                // If the first page is empty, return an error
                                return LoadResult.Error(Exception("No data found for the query $query " + if (previousQuery != null) "But yet,You can see the data on $previousQuery" else ""))
                            } else {
                                // If not the first page and empty, signal end of data
                                return LoadResult.Page(
                                    data = emptyList(),
                                    prevKey = if (page == 1) null else page - 1,
                                    nextKey = null
                                )
                            }
                        }

                        // Return the paginated data
                        LoadResult.Page(
                            data = paginatedRepos.map { it.toGitRepo() },
                            prevKey = if (page == 1) null else page - 1,
                            nextKey = if (endIndex >= allRepos.size) null else page + 1
                        )
                    }
                response
            } catch (e: Exception) {
                LoadResult.Error(e) // Return an error if loading from cache fails
            }
        } else {
            // Load data from the network and update the database
            try {
                val response = getRepositoriesUseCase(
                    query = query,
                    perPage = perPage,
                    page = page
                ).items.map { it.toGitRepo(query = query) }

                // Handle empty data scenarios
                if (response.isEmpty()) {
                    return LoadResult.Error(Exception("No data found for the query $query"))
                }

                // Update database if query has changed and the response is successful
                if (previousQuery != null && previousQuery != query && response.isNotEmpty()) {
                    clearReposUseCase() // Clear previous data from the database
                    previousQuery = query // Update previousQuery to the current query
                }

                // Insert the fetched data into the database
                if (response.isNotEmpty()) {
                    insertAllReposUseCase(response.map { it.toGitRepo() })
                }

                // Return the loaded data
                LoadResult.Page(
                    data = response,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (response.isEmpty()) null else page + 1
                )
            } catch (e: Exception) {
                LoadResult.Error(e) // Return an error if loading from network fails
            }
        }
    }
}*/


/*class GitRepoPagingSource(
    private val query: String,
    private val fromCache: Boolean = false,
    private val getAllGitReposUserCase: GetAllGitReposUserCase,
    private val getGitReposUseCase: GetGitReposUseCase,
    private val clearReposUseCase: ClearReposUseCase,
    private val insertAllReposUseCase: InsertAllReposUseCase,
    private val getRepositoriesUseCase: GetRepositoriesUseCase,
) : PagingSource<Int, GitRepo>() {

    companion object {
        var previousQuery: String? = null // Store the previous query for comparison
    }

    override fun getRefreshKey(state: PagingState<Int, GitRepo>): Int? {
        // Get the refresh key to resume paging from the current position
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GitRepo> {
        val page = params.key ?: 1 // Current page number
        val perPage = params.loadSize // Number of items per page
        Log.d(TAG, "fromCache $fromCache $query ")

        return try {
            // Retry logic with incremental delay
            retryWithExponentialBackoff(times = 5, initialDelayMillis = 1000, maxDelayMillis = 5000) {
                if (fromCache) {
                    // Load data from the database (cache)
                    getGitReposUseCase(query)
                        .first()
                        .let { allRepos ->
                            // Paginate the data from the database
                            val startIndex = (page - 1) * perPage
                            val endIndex = minOf(startIndex + perPage, allRepos.size)
                            val paginatedRepos = allRepos.subList(startIndex, endIndex)

                            // Handle empty data scenarios
                            if (paginatedRepos.isEmpty()) {
                                if (page == 1) {
                                    throw Exception("No data found for the query $query " + if (previousQuery != null) "But yet,You can see the data on $previousQuery" else "")
                                } else {
                                    return@retryWithExponentialBackoff LoadResult.Page(
                                        data = emptyList(),
                                        prevKey = if (page == 1) null else page - 1,
                                        nextKey = null
                                    )
                                }
                            }

                            // Return the paginated data
                            LoadResult.Page(
                                data = paginatedRepos.map { it.toGitRepo() },
                                prevKey = if (page == 1) null else page - 1,
                                nextKey = if (endIndex >= allRepos.size) null else page + 1
                            )
                        }
                } else {
                    // Load data from the network and update the database
                    val response = getRepositoriesUseCase(
                        query = query,
                        perPage = perPage,
                        page = page
                    ).items.map { it.toGitRepo(query = query) }

                    // Handle empty data scenarios
                    if (response.isEmpty()) {
                        throw Exception("No data found for the query $query")
                    }

                    // Update database if query has changed and the response is successful
                    if (previousQuery != null && previousQuery != query && response.isNotEmpty()) {
                        clearReposUseCase() // Clear previous data from the database
                        previousQuery = query // Update previousQuery to the current query
                    }

                    // Insert the fetched data into the database
                    if (response.isNotEmpty()) {
                        insertAllReposUseCase(response.map { it.toGitRepo() })
                    }

                    // Return the loaded data
                    LoadResult.Page(
                        data = response,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (response.isEmpty()) null else page + 1
                    )
                }
            }
        } catch (e: Exception) {
            LoadResult.Error(e) // Return an error if loading fails after retries
        }
    }
}

*/
/**
 * Retries the given block of code with exponential backoff.
 *
 * @param times The number of times to retry.
 * @param initialDelayMillis The initial delay in milliseconds.
 * @param maxDelayMillis The maximum delay in milliseconds.
 * @param block The block of code to retry.
 * @return The result of the block of code, or null if it failed after all retries.
 *//*
suspend fun <T> retryWithExponentialBackoff(
    times: Int,
    initialDelayMillis: Long,
    maxDelayMillis: Long,
    block: suspend () -> T,
): T {
    var currentDelay = initialDelayMillis
    repeat(times - 1) { attempt ->
        try {
            return block()
        } catch (e: Exception) {
            // Log the exception or handle it as needed
            Log.e(TAG, "Retry attempt $attempt failed: ${e.message}", e)
            delay(currentDelay)
            currentDelay = (currentDelay * 2).coerceAtMost(maxDelayMillis) // Exponential backoff
        }
    }
    return block() // Last attempt without catching exceptions
}*/
