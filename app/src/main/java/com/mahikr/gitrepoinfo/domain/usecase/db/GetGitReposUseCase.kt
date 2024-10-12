package com.mahikr.gitrepoinfo.domain.usecase.db

import com.mahikr.gitrepoinfo.data.local.model.GitRepoEntity
import com.mahikr.gitrepoinfo.domain.repo.IGitRepoDaoWrapper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGitReposUseCase @Inject constructor(private val gitRepoDao: IGitRepoDaoWrapper) {

    suspend operator fun invoke(query: String): Flow<List<GitRepoEntity>> = gitRepoDao.getGitRepos(query)

}
