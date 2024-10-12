package com.mahikr.gitrepoinfo.domain.usecase.network

import com.mahikr.gitrepoinfo.domain.model.Contributors
import com.mahikr.gitrepoinfo.domain.repo.IGitRepoWrapper
import com.mahikr.gitrepoinfo.util.HttpClientResponse
import com.mahikr.gitrepoinfo.util.onSafeApiCall
import com.mahikr.gitrepoinfo.util.toContributors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetContributorsUseCase @Inject constructor(private val gitRepoWrapper: IGitRepoWrapper, ) {
    operator fun invoke(
        query: String
    ): Flow<HttpClientResponse<List<Contributors>>> = flow {
        emit(onSafeApiCall {
            gitRepoWrapper.getContributors(query = query).map { it.toContributors() }
        })
    }
}