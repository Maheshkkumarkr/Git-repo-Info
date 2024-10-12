package com.mahikr.gitrepoinfo.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahikr.gitrepoinfo.domain.model.Contributors
import com.mahikr.gitrepoinfo.domain.model.GitRepo
import com.mahikr.gitrepoinfo.domain.usecase.db.GetGitRepoById
import com.mahikr.gitrepoinfo.domain.usecase.network.GetContributorsUseCase
import com.mahikr.gitrepoinfo.presentation.uistate.UIState
import com.mahikr.gitrepoinfo.util.toGitRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DetailRepoViewModel @Inject constructor(
    private val fetchContributors: GetContributorsUseCase,
    private val getGitRepoById: GetGitRepoById,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {


    init {
        val projectId = savedStateHandle.get<Int>("id")?:-1
        viewModelScope.launch {
            getGitRepoById(projectId).onEach {
                gitRepoEntity->
                _projectState.update {
                    gitRepoEntity.toGitRepo()
                }
            }.launchIn(this)
        }

        Log.d("TAG", "DetailRepoViewModel id: $projectId ")
    }


    private val _projectState = MutableStateFlow<GitRepo?>(null)
    val projectState = _projectState.asStateFlow()


    private val _searchState1 = MutableStateFlow<UIState<List<Contributors>>>(UIState.Idle)
    val searchState1 = _searchState1.asStateFlow()

    fun getContributors(query: String) {
        _searchState1.value = UIState.Loading
        Log.d("TAG", "getGitRepos: query $query")
        fetchContributors(query = query).onEach { clientResponse ->
            Log.d("TAG", "getGitRepos: clientResponse $clientResponse")
            clientResponse.onSuccess { contributors ->
                Log.d("TAG", "getGitRepos: gitRepoDto.items.size $contributors")
                _searchState1.value = UIState.Success(contributors)
            }.onFailure {
                // Handle error, e.g., show error message to the user
                Log.e("TAG", "Error fetching Git repos: ${it.message}", it)
                _searchState1.value = UIState.Error("Search failed: ${it.message}")
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }
}

