package com.mahikr.gitrepoinfo.presentation.screen

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.mahikr.gitrepoinfo.R
import com.mahikr.gitrepoinfo.presentation.component.CircularProgressWithDrawableImage
import com.mahikr.gitrepoinfo.presentation.component.detail.GitContributorItem
import com.mahikr.gitrepoinfo.presentation.component.home.GitRepoItem
import com.mahikr.gitrepoinfo.presentation.uistate.UIState
import com.mahikr.gitrepoinfo.presentation.viewmodel.DetailRepoViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun DetailScreen(viewModel: DetailRepoViewModel = hiltViewModel()) {
    var showWebView by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val searchState1 by viewModel.searchState1.collectAsState()
    val repo by viewModel.projectState.collectAsState()



    LaunchedEffect(repo) {
        Log.d("TAG", "GitRepoFullScreen:LaunchedEffect ${repo?.contributorsUrl} ")
        repo?.contributorsUrl?.let { viewModel.getContributors(it) }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Button(
                onClick = {
                    showWebView = !showWebView
                    isLoading = true // Start loading when WebView is opened
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(if (!showWebView) "Know more about Project" else "Go back")
            }
        }
    ) { innerPadding ->

        if (showWebView) {
            //CustomWebView(repo.projectLink)
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true // Enable JavaScript
                        loadUrl(repo?.projectLink?:"")
                        // Set isLoading to false when the page is loaded
                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                Log.d("TAG", "onProgressChanged: newProgress")
                                if (newProgress == 100) {
                                    Log.d("TAG", "onProgressChanged: $newProgress")
                                    isLoading = false
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 60.dp, bottom = 80.dp)
            )

            if (isLoading) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(color = colorResource(R.color.orange)), contentAlignment = Alignment.Center) {
                    CircularProgressWithDrawableImage(
                        drawable = R.drawable.git_image, // Pass the drawable resource
                        modifier = Modifier.padding(16.dp),
                        imageSize = 150.dp,
                        progressSize = 300.dp
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Spacer(Modifier.height(10.dp))
                Text(
                    "Project: ",
                    fontStyle = MaterialTheme.typography.headlineMedium.fontStyle,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                    fontWeight = MaterialTheme.typography.headlineMedium.fontWeight
                )
                repo?.let { GitRepoItem(repo = it, enable = false) { }

                }
                Spacer(Modifier.height(10.dp))
                Text(
                    "Contributors: ",
                    fontStyle = MaterialTheme.typography.headlineMedium.fontStyle,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                    fontWeight = MaterialTheme.typography.headlineMedium.fontWeight
                )
                Spacer(Modifier.height(10.dp))
                //detailRepoViewModel
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                    //.padding(innerPadding)
                ) {
                    when (val state = searchState1) {
                        is UIState.Idle -> {
                            // Display initial state or placeholder
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .padding(50.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.git_repo_image),
                                    contentDescription = "Git image"
                                )
                            }
                        }

                        is UIState.Loading -> {
                            // Display loading indicator
                            //CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            Box(modifier = Modifier
                                .fillMaxSize()
                                .background(color = colorResource(R.color.orange)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressWithDrawableImage(
                                    drawable = R.drawable.git_image, // Pass the drawable resource
                                    modifier = Modifier.padding(16.dp),
                                    imageSize = 150.dp,
                                    progressSize = 300.dp
                                )
                            }
                        }

                        is UIState.Success -> {
                            // Display search results
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                items(state.data, key = { it.id }) { contributor ->
                                    GitContributorItem(contributor = contributor)
                                }
                            }
                        }

                        is UIState.Error -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
                            {// Display error message
                                Text("Error: ${state.message}", color = Color.Red, modifier = Modifier.align(
                                    Alignment.Center))
                            }
                        }
                    }
                }
            }
        }
    }
}
