package com.mahikr.gitrepoinfo.domain.model

//Represents the diluted model wrt GitRepoDto
data class GitRepo(
    val id: Int,
    val name: String,
    val imageUrl: String? = null,
    val description: String,
    val createdAt: String,
    val projectLink: String,
    val contributorsUrl: String? = null,
    val query: String? = null,
)