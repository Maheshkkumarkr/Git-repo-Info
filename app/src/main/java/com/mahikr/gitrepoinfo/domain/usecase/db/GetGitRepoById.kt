package com.mahikr.gitrepoinfo.domain.usecase.db

import com.mahikr.gitrepoinfo.domain.repo.IGitRepoDaoWrapper
import javax.inject.Inject

class GetGitRepoById  @Inject constructor(private val gitRepoDao: IGitRepoDaoWrapper) {

    suspend operator fun invoke(id:Int) = gitRepoDao.getGitRepoById(id = id)

}