package com.mahikr.gitrepoinfo.data.repo

import com.mahikr.gitrepoinfo.data.remote.httpclient.GitHttpServer
import com.mahikr.gitrepoinfo.data.remote.model.ContributorsDto
import com.mahikr.gitrepoinfo.data.remote.model.GitReposDto
import com.mahikr.gitrepoinfo.domain.repo.IGitRepoWrapper
import javax.inject.Inject

//act as wrapper to get the data from the http-client
class GetRepositoriesImpl @Inject constructor(
    private val gitHttpServer: GitHttpServer
): IGitRepoWrapper {

    override suspend fun getRepositories(query: String, perPage: Int, page: Int): GitReposDto =
        gitHttpServer.getRepositories(query = query, perPage=perPage, page= page)

    override suspend fun getContributors(query: String): List<ContributorsDto> =
        gitHttpServer.getContributors(url =  query)

}