package com.mahikr.gitrepoinfo.presentation.component

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.mahikr.gitrepoinfo.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private const val TAG = "PagedDataList_TAG"

@Composable
fun <T : Any> PagedDataList(
    pagedData: LazyPagingItems<T>, // The LazyPagingItems object to display paged data
    itemContent: @Composable (T) -> Unit, // Composable function to display a single item
) {
    var failureCount by rememberSaveable { mutableIntStateOf(0) } // Counter for retry attempts
    var showPressIndicator by rememberSaveable { mutableStateOf(true) } // Flag to show/hide loading indicator
    val scope = rememberCoroutineScope() // Coroutine scope for launching retry operations

    // LaunchedEffect to trigger initial data loading and retry
    LaunchedEffect(key1 = Unit) {
        delay(1000L) // Delay to show initial loading indicator
        showPressIndicator = false // Hide loading indicator after delay
        pagedData.retry() // Retry loading data in case of initial errors
    }

    LazyColumn {
        // Iterate through the paged data items
        items(pagedData) { item: T? ->
            if (item != null) {
                // If item is not null, display it using the itemContent composable
                showPressIndicator = false // Hide loading indicator
                failureCount = 0 // Reset retry counter
                itemContent(item)
            } else if (showPressIndicator) {
                // If item is null and showPressIndicator is true, display initial loading indicator
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = colorResource(R.color.orange)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressWithDrawableImage(
                        drawable = R.drawable.git_image, // Pass the drawable resource
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        // Handle LoadState.Error and endOfPaginationReached for refresh state
        when (val loadState = pagedData.loadState.refresh) {
            is LoadState.Error -> {
                // If there is an error during refresh, display an error message and retry button
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val errorMsg =
                            if (loadState.error.message != null && loadState.error.message!!.contains(
                                    "Unable to resolve host"
                                )
                            ) "" else "${loadState.error.message}}" // Customize error message
                        Text(errorMsg)
                        Button(onClick = {
                            pagedData.refresh() // Refresh data
                            showPressIndicator = true // Show loading indicator
                        }) {
                            Text("Retry")
                        }
                    }
                }
            }

            is LoadState.NotLoading -> {
                // If refresh is successful and not loading
                if (loadState.endOfPaginationReached) {
                    // If end of pagination is reached, display "End of list"
                    item { Text("End of list") }
                    failureCount = 0 // Reset retry counter
                    showPressIndicator = false // Hide loading indicator
                } else if (failureCount < 5 && pagedData.itemCount == 0) {
                    // If there are no items and retry count is less than 5, display retry content
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (showPressIndicator) {
                                // Show loading indicator during retry
                                CircularProgressWithDrawableImage(
                                    drawable = R.drawable.git_repo_image, // Pass the drawable resource
                                    modifier = Modifier.padding(16.dp),
                                    imageSize = 20.dp,
                                    progressSize = 50.dp
                                )
                            } else {
                                // Show error message and retry button
                                Text("Error: Seems no internet or rate limit exceeded.")
                                Button(onClick = {
                                    scope.launch {
                                        showPressIndicator = true // Show loading indicator
                                        pagedData.retry() // Retry loading data
                                        failureCount++ // Increment retry counter
                                        delay(1500L) // Delay to show indicator
                                        showPressIndicator = false // Hide loading indicator
                                    }
                                }) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }
            }

            LoadState.Loading -> {
                // If refresh is in progress, display loading indicator
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .background(color = colorResource(R.color.orange)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressWithDrawableImage(
                                drawable = R.drawable.git_image, // Pass the drawable resource
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Handle LoadState for append state
        when (val loadState = pagedData.loadState.append) {
            is LoadState.NotLoading -> {
                // If append is successful and not loading, log a message
                Log.d(TAG, "PagedDataList: pagedData.loadState.append NotLoading ${loadState.endOfPaginationReached}")
                //pagedData.retry()
            }

            LoadState.Loading -> {
                // If append is in progress, display loading indicator
                Log.d(
                    TAG,
                    "PagedDataList: pagedData.loadState.append Loading ${loadState.endOfPaginationReached}"
                )
                showPressIndicator = true // Show loading indicator
                item {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .background(
                            color = colorResource(
                                R.color.orange
                            ).copy(alpha = 0.5f)
                        ),
                        contentAlignment = Alignment.Center) {
                        CircularProgressWithDrawableImage(
                            drawable = R.drawable.git_image, // Pass the drawable resource
                            modifier = Modifier.padding(16.dp),
                            imageSize = 20.dp,
                            progressSize = 50.dp
                        )
                    }
                }
            }

            is LoadState.Error -> {
                // If there is an error during append, display an error message and toast
                Log.d(TAG, "PagedDataList: pagedData.loadState.append Error ${loadState.error.message}")
                showPressIndicator = false // Hide loading indicator
                Log.d(TAG, "PagedDataList: pagedData.loadState.append Error ${(pagedData.loadState.append as LoadState.Error).error.message}")
                showPressIndicator = false // Hide loading indicator
                item {

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        val errorMessage = if (loadState.error.message != null &&( loadState.error.message!!.contains("Caught all data")))
                            "You have caught all data"
                        else {
                            "Something went wrong!!!"
                        }
                        Text(errorMessage)
                        Toast.makeText(LocalContext.current, errorMessage, Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }
}
