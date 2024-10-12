package com.mahikr.gitrepoinfo.domain.model

//Represents the diluted model wrt ContributorsDto
data class Contributors(
    val imageUrl: String,
    val contributionsCount: Int,
    val gitBio: String,
    val id: Int,
    val name: String,
    val isSiteAdmin: Boolean,
    val type: String,
    //val url: String //complete info on user
)

