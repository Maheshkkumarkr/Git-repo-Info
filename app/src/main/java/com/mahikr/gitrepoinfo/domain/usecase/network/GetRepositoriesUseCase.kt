package com.mahikr.gitrepoinfo.domain.usecase.network

import com.mahikr.gitrepoinfo.data.remote.model.GitReposDto
import com.mahikr.gitrepoinfo.domain.repo.IGitRepoWrapper
import javax.inject.Inject

class GetRepositoriesUseCase @Inject constructor(
    private val gitRepoWrapper: IGitRepoWrapper,
) {

    suspend operator fun invoke(
        query: String, perPage: Int, page: Int): GitReposDto =
        gitRepoWrapper.getRepositories(query = query, perPage = perPage, page = page)

}
