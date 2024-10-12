package com.mahikr.gitrepoinfo.presentation.component.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.mahikr.gitrepoinfo.R.*
import com.mahikr.gitrepoinfo.presentation.component.CircularProgressWithDrawableImage
import java.util.Locale


private const val TAG = "GitInfoItem_TAG"

//Represents Info the Items for the Repository
@Composable
fun GitInfoItem(
    modifier: Modifier = Modifier,
    name: String,
    details: String,
    imageUrl: String?,
    moreInfoLink: String,
    contribution: String,
    nameTextNumberOfLines:Int = 2,
    moreInfoLinkTextNumberOfLines:Int = 2,
    detailTextNumberOfLines:Int = 6
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SubcomposeAsyncImage(
            model = imageUrl, contentDescription = "$name's image",
            modifier = Modifier
                .weight(1f)
                .height(150.dp)) {
            val state = painter.state
            if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                //CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Log.d(TAG, "PagedDataList: LoadState.SubcomposeAsyncImage showPressIndicator")
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(color = colorResource(color.orange)),
                    contentAlignment = Alignment.Center) {
                    CircularProgressWithDrawableImage(
                        drawable = drawable.git_image, // Pass the drawable resource
                        modifier = Modifier.padding(16.dp),
                        imageSize = 20.dp,
                        progressSize = 50.dp
                    )
                }
            } else {
                SubcomposeAsyncImageContent()
            }
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(3f), verticalArrangement = Arrangement.Center) {
            Text(
                text = name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = nameTextNumberOfLines,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = moreInfoLink,
                maxLines = moreInfoLinkTextNumberOfLines,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = details,
                maxLines = detailTextNumberOfLines,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = contribution,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

}
