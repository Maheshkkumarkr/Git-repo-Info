package com.mahikr.gitrepoinfo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mahikr.gitrepoinfo.data.paging.GitRepoPagingSource
//import com.mahikr.GitRepoInfo.data.paging.GitRepoPagingSourceFactory
import com.mahikr.gitrepoinfo.domain.model.GitRepo
import com.mahikr.gitrepoinfo.domain.usecase.db.ClearReposUseCase
import com.mahikr.gitrepoinfo.domain.usecase.db.GetAllGitReposUserCase
import com.mahikr.gitrepoinfo.domain.usecase.db.GetGitReposUseCase
import com.mahikr.gitrepoinfo.domain.usecase.db.InsertAllReposUseCase
import com.mahikr.gitrepoinfo.domain.usecase.network.GetRepositoriesUseCase
import com.mahikr.gitrepoinfo.util.constants.Constants.PER_PAGE_COUNT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomeViewModel_TAG"


/***** HomeViewModel
 * Fetches the repository list from the server with help of paging an retrofit use-cases
 */

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllGitRepos: GetAllGitReposUserCase,
    private val getAllGitReposUserCase: GetAllGitReposUserCase,
    private val getGitReposUseCase: GetGitReposUseCase,
    private val clearReposUseCase: ClearReposUseCase,
    private val insertAllReposUseCase: InsertAllReposUseCase,
    private val getRepositoriesUseCase: GetRepositoriesUseCase,
    //private val gitRepoPagingSourceFactory: GitRepoPagingSourceFactory
    ) : ViewModel() {

        //Maintains the previous query state to fetch the data from the db on network
    private var _previousQuery = MutableStateFlow<String?>(null)
    val previousQuery = _previousQuery.asStateFlow()

    init {
        viewModelScope.launch {
            getAllGitRepos()
                .firstOrNull() // Get the first emission from the flow
                ?.let { allRepos ->
                    _previousQuery.value = allRepos.firstOrNull()?.query
                    GitRepoPagingSource.previousQuery = _previousQuery.value
                }
        }

    }

    fun getGitRepos(query: String, fromCache: Boolean, pagingSize :Int = PER_PAGE_COUNT): Flow<PagingData<GitRepo>> {
        return Pager(
            config = PagingConfig(pageSize = pagingSize), // Default page size is 10
            pagingSourceFactory = {
                GitRepoPagingSource(
                    query = query,
                    fromCache = fromCache,
                    getAllGitReposUserCase = getAllGitReposUserCase,
                    getGitReposUseCase = getGitReposUseCase,
                    clearReposUseCase = clearReposUseCase,
                    insertAllReposUseCase =insertAllReposUseCase,
                    getRepositoriesUseCase = getRepositoriesUseCase
                )
            }
        ).flow
    }


}