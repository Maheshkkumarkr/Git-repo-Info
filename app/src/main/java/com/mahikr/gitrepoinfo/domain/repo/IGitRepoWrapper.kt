package com.mahikr.gitrepoinfo.domain.repo

import com.mahikr.gitrepoinfo.data.remote.model.ContributorsDto
import com.mahikr.gitrepoinfo.data.remote.model.GitReposDto

////Abstraction to access the GitRepo Http client
interface IGitRepoWrapper {

    suspend fun getRepositories(
        query: String,
        perPage: Int,
        page: Int,
    ): GitReposDto

    suspend fun getContributors(query: String): List<ContributorsDto>

}