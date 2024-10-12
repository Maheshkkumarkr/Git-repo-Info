package com.mahikr.gitrepoinfo.domain.repo

import com.mahikr.gitrepoinfo.data.local.model.GitRepoEntity
import kotlinx.coroutines.flow.Flow

//Abstraction to access the GitRepoDao
interface IGitRepoDaoWrapper {

    suspend fun insertAll(gitRepos: List<GitRepoEntity>)

    fun getGitRepos(query: String): Flow<List<GitRepoEntity>>

    fun getAllGitRepos(): Flow<List<GitRepoEntity>>

    suspend fun clearRepos()

    fun getGitRepoById(id:Int): Flow<GitRepoEntity>

}
