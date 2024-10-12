package com.mahikr.gitrepoinfo.presentation.component.detail

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mahikr.gitrepoinfo.domain.model.Contributors
import com.mahikr.gitrepoinfo.presentation.component.home.GitInfoItem

//Represents the Items for the contributors on the card view
@Composable
fun GitContributorItem(modifier: Modifier = Modifier, contributor: Contributors) {

    Card(
        modifier = modifier
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        GitInfoItem(
            name = contributor.name,
            imageUrl = contributor.imageUrl,
            moreInfoLink = contributor.gitBio,
            contribution = "Contributions :" + contributor.contributionsCount,
            details = contributor.type,
            detailTextNumberOfLines = 50,
            moreInfoLinkTextNumberOfLines = 5,
            nameTextNumberOfLines = 3
        )
    }
}
