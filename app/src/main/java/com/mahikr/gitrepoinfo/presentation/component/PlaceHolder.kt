package com.mahikr.gitrepoinfo.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun PlaceHolder(
    modifier: Modifier = Modifier,
    messageText: String,
    messageFontSize: TextUnit,
    messageFontWeight: FontWeight,
    drawable: Int,
    tint: Color,
    onClick: () -> Unit,
    enable: Boolean = false,
) {
    Box(
        modifier
            .fillMaxSize()
            .clickable(enabled = enable) {
                onClick()
            }
            .padding(50.dp), contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(drawable),
            contentDescription = "Git image",
            colorFilter = ColorFilter.tint(color = tint)
        )
        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
            Text(
                messageText,
                fontSize = messageFontSize,
                fontWeight = messageFontWeight
            )
        }
    }


}
