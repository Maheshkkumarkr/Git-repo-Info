package com.mahikr.gitrepoinfo.data.repo

import com.mahikr.gitrepoinfo.data.local.dao.GitRepoDao
import com.mahikr.gitrepoinfo.data.local.model.GitRepoEntity
import com.mahikr.gitrepoinfo.domain.repo.IGitRepoDaoWrapper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

//act as wrapper to get the data from the database/dao
class GitRepoDaoWrapperImpl @Inject constructor(
    private val gitRepoDao: GitRepoDao
): IGitRepoDaoWrapper {

    override suspend fun insertAll(gitRepos: List<GitRepoEntity>)=
        gitRepoDao.insertAll(gitRepos = gitRepos)

    override fun getGitRepos(query: String): Flow<List<GitRepoEntity>> =
        gitRepoDao.getGitRepos(query=query)

    override fun getAllGitRepos(): Flow<List<GitRepoEntity>> =
        gitRepoDao.getAllGitRepos()

    override suspend fun clearRepos() =
        gitRepoDao.clearRepos()

    override fun  getGitRepoById(id:Int) = gitRepoDao.getGitRepoById(id = id)

}