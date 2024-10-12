package com.mahikr.gitrepoinfo.data.remote.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

//Represents the contributors
@Serializable
data class ContributorsDto(
    @SerializedName("avatar_url") val imageUrl: String,
    @SerializedName("html_url") val gitBio: String,
    @SerializedName("id") val id: Int,
    @SerializedName("login") val login: String,
    @SerializedName("contributions") val contributions: Int,
    @SerializedName("site_admin") val isSiteAdmin: Boolean,
    @SerializedName("type") val type: String
    //@SerializedName("url") val url: String,
)
