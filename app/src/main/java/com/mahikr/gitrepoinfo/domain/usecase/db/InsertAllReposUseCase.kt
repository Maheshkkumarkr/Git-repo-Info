package com.mahikr.gitrepoinfo.domain.usecase.db

import com.mahikr.gitrepoinfo.data.local.model.GitRepoEntity
import com.mahikr.gitrepoinfo.domain.repo.IGitRepoDaoWrapper
import javax.inject.Inject

class InsertAllReposUseCase @Inject constructor(private val gitRepoDao: IGitRepoDaoWrapper) {

    suspend operator fun invoke(gitRepos: List<GitRepoEntity>) = gitRepoDao.insertAll(gitRepos)

}



