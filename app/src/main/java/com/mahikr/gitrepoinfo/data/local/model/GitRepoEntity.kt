package com.mahikr.gitrepoinfo.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

//represents the table
@Entity(tableName = "git_repos")
data class GitRepoEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String?,
    val description: String,
    val createdAt: String,
    val projectLink: String,
    val contributorsUrl: String?,
    val query: String?,
)
