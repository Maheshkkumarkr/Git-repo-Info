package com.mahikr.gitrepoinfo.data.remote.model

import com.google.gson.annotations.SerializedName


//Represents the Repositories
data class GitReposDto(
    @SerializedName("items")
    val items: List<GitRepoDto>,
){
    data class GitRepoDto(
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String,
        @SerializedName("owner") val owner: Owner,
        @SerializedName("description") val description: String,
        @SerializedName("created_at") val createdAt: String,
        @SerializedName("html_url") val projectLink: String,
        @SerializedName("contributors_url")  val contributorsUrl: String,
    )

    data class Owner(
        @SerializedName("avatar_url") val imageUrl: String,
    )

}
