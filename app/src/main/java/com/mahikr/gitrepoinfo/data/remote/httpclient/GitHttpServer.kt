package com.mahikr.gitrepoinfo.data.remote.httpclient

import com.mahikr.gitrepoinfo.data.remote.model.ContributorsDto
import com.mahikr.gitrepoinfo.data.remote.model.GitReposDto
import com.mahikr.gitrepoinfo.util.constants.Constants.GIT_REPO_END_POINT
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface GitHttpServer {

    //end point to fetch data from the server: gets the repositories
    //?q=images&per_page=10&page=1
    @GET(GIT_REPO_END_POINT)
    suspend fun getRepositories(
        @Query("q") query: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int,
    ): GitReposDto


    //end point to fetch data from the server: gets the Contributors
    @GET
    suspend fun getContributors(@Url url: String): List<ContributorsDto>

}