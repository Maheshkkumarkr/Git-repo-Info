package com.mahikr.gitrepoinfo.presentation.component.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mahikr.gitrepoinfo.domain.model.GitRepo
import com.mahikr.gitrepoinfo.presentation.component.home.GitInfoItem

//Represents Info the Items for the Repository
@Composable
fun GitRepoItem(enable:Boolean = true, modifier: Modifier = Modifier, repo: GitRepo, onClick: (GitRepo) -> Unit) {
    Card(
        modifier = modifier
            .clickable (enabled = enable){ onClick(repo) }
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        GitInfoItem(
            name = repo.name,
            imageUrl = repo.imageUrl,
            moreInfoLink = repo.projectLink,
            contribution = "Created @ " + repo.createdAt,
            details = repo.description
        )

    }
}
