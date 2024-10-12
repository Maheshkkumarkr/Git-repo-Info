package com.mahikr.gitrepoinfo.util

import com.mahikr.gitrepoinfo.data.local.model.GitRepoEntity
import com.mahikr.gitrepoinfo.data.remote.model.ContributorsDto
import com.mahikr.gitrepoinfo.data.remote.model.GitReposDto
import com.mahikr.gitrepoinfo.domain.model.Contributors
import com.mahikr.gitrepoinfo.domain.model.GitRepo

/******Model mappers
 * Convert or Maps the DTO [Data transfer object] to Domain models or Entity and vice versa.
 */


fun GitReposDto.GitRepoDto.toGitRepo(query:String? = null) = GitRepo(
    id = id?:-1,
    name = name?:"",
    imageUrl = owner.imageUrl?:"",
    description = description?:"",
    createdAt = createdAt,
    projectLink = projectLink?:"",
    contributorsUrl = contributorsUrl?:"",
    query = query
)



fun GitReposDto.GitRepoDto.toGitRepoEntity(query:String? = null) = GitRepoEntity(
    id = id?:-1,
    name = name?:"",
    imageUrl = owner.imageUrl?:"",
    description = description?:"",
    createdAt = createdAt,
    projectLink = projectLink?:"",
    contributorsUrl = contributorsUrl?:"",
    query = query
)


fun GitRepoEntity.toGitRepo() = GitRepo(
    id = id?:-1,
    name = name?:"",
    imageUrl = imageUrl?:"",
    description = description?:"",
    createdAt = createdAt,
    projectLink = projectLink?:"",
    contributorsUrl = contributorsUrl?:"",
    query = query
)


fun GitRepo.toGitRepo() = GitRepoEntity(
    id = id?:-1,
    name = name?:"",
    imageUrl = imageUrl?:"",
    description = description?:"",
    createdAt = createdAt,
    projectLink = projectLink?:"",
    contributorsUrl = contributorsUrl?:"",
    query = query
)



fun ContributorsDto.toContributors() = Contributors(
    imageUrl = imageUrl,
    contributionsCount = contributions,
    gitBio = gitBio,
    id = id,
    name = login,
    type = type,
    isSiteAdmin = isSiteAdmin
)