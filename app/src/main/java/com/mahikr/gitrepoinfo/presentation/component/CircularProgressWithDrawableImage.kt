package com.mahikr.gitrepoinfo.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mahikr.gitrepoinfo.R

//Progress bar to indicate the user on loading
@Composable
fun CircularProgressWithDrawableImage(
    drawable: Int, // Pass the drawable image
    modifier: Modifier = Modifier,
    imageSize: Dp = 100.dp,
    progressSize: Dp = 300.dp,
) {
    Box(
        modifier = modifier
            .size(progressSize)
            .clip(CircleShape)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        // Image at the center (using Icon)
        Icon(
            painter = painterResource(drawable),
            contentDescription = "CircularProgress",
            modifier = Modifier
                .size(imageSize)
                .clip(CircleShape),
            tint = colorResource(R.color.orange) // Prevent tinting the drawable
        )
        // Circular progress indicator
        CircularProgressIndicator(
            modifier = Modifier
                .size(progressSize)
                .padding(imageSize / 2),
            strokeWidth = 8.dp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}