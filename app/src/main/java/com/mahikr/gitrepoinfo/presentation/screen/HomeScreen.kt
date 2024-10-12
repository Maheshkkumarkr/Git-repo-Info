package com.mahikr.GitRepoInfo.presentation.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.mahikr.gitrepoinfo.R
import com.mahikr.gitrepoinfo.data.paging.GitRepoPagingSource
import com.mahikr.gitrepoinfo.domain.model.GitRepo
import com.mahikr.gitrepoinfo.presentation.component.PagedDataList
import com.mahikr.gitrepoinfo.presentation.component.PlaceHolder
import com.mahikr.gitrepoinfo.presentation.component.SearchBar
import com.mahikr.gitrepoinfo.presentation.component.home.GitRepoItem
import com.mahikr.gitrepoinfo.presentation.viewmodel.ConnectivityViewModel
import com.mahikr.gitrepoinfo.presentation.viewmodel.HomeViewModel

private const val TAG = "HomeScreen_TAG"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel<HomeViewModel>(),
    connectivityViewModel: ConnectivityViewModel = hiltViewModel(),
    onRepoClick: (Int) -> Unit,
) {
    val context = LocalContext.current
    val previousQuery by viewModel.previousQuery.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val isNetworkAvailable by connectivityViewModel.connectivityStatus.collectAsState()
    var query by remember { mutableStateOf("") } // Initial query is empty
    var searchQuery by remember { mutableStateOf("") }
    val gitRepos: LazyPagingItems<GitRepo>? = if (searchQuery.isNotEmpty())
        viewModel.getGitRepos(searchQuery, false).collectAsLazyPagingItems() else null

    var showSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    var showCachedData by remember { mutableStateOf(false) } // State to control showing cached data

    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            val result = snackbarHostState.showSnackbar(
                message = "Network is available. Tap to refresh data.",
                actionLabel = "Refresh",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                // Trigger network call (e.g., gitRepos?.refresh())
                /*gitRepos?.refresh()*/
            }
            showSnackbar = false // Hide Snackbar after it's shown or action is performed
        }
    }


    Scaffold(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp), bottomBar = {

    },
        snackbarHost = { SnackbarHost(snackbarHostState) } // Add SnackbarHost
        , topBar = {
            CenterAlignedTopAppBar(title = {
                Log.d(TAG, "HomeScreen: SearchBar $query")
                SearchBar(
                    isEnabled = isNetworkAvailable,
                    placeHolderText = if (isNetworkAvailable) "Search repositories..." else "No internet ⚠️",
                    query = query,
                    onQueryChange = { newQuery ->
                        if (searchQuery.isNotEmpty())
                            searchQuery = ""
                        query = newQuery // Update query state on text change
                    },
                    onSearch = {
                        Log.d(TAG, "HomeScreen:onSearch $query  $searchQuery")
                        if (isNetworkAvailable.not()) {
                            viewModel.getGitRepos(query = query, fromCache = true)
                        }

                        if (query.isNotEmpty()) { // Trigger API call on search if query is not empty
                            searchQuery = query // Update searchQuery to trigger recomposition
                            gitRepos?.refresh() // Refresh if gitRepos is already initialized
                            keyboardController?.hide()
                            showCachedData =
                                false // Reset showCachedData when performing a new search
                        }
                    }
                )
            })
        }) { paddingValue ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValue)
                .clickable(enabled = isNetworkAvailable.not() && previousQuery != null && !showCachedData) {
                    showCachedData = true // Show cached data on click
                }
        ) {

            if (isNetworkAvailable.not() && (searchQuery.isEmpty() || previousQuery == null) && !showCachedData) {
                Column {

                    if (isNetworkAvailable.not()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = "You are in Offline mode ⚠️",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.errorContainer)
                                    .padding(8.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }


                    PlaceHolder(

                        messageText = if (previousQuery != null) "You still check the data on ${GitRepoPagingSource.previousQuery} click to view or search on the search bar" else "No cached data available 😖",
                        messageFontSize = MaterialTheme.typography.labelLarge.fontSize,
                        messageFontWeight = FontWeight.Bold,
                        drawable = R.drawable.git_image,
                        tint = Color.Red.copy(alpha = 0.5f),
                        enable = (previousQuery != null),
                        onClick = {
                            Log.d(TAG, "previousQuery: getGitRepos fromCache $previousQuery")
                            showCachedData = true
                        }
                    )

                }
            } else if (showCachedData && previousQuery != null) { // Show cached data if enabled
                val cachedGitRepos =
                    viewModel.getGitRepos(previousQuery!!, true).collectAsLazyPagingItems()
                PagedDataList(pagedData = cachedGitRepos, itemContent = { repo: GitRepo ->
                    GitRepoItem(repo = repo, enable = false) {
                        Log.d(TAG, "GitRepoItem: $it")
                        /*Log.d(TAG, "HomeScreen:onRepoClick => searchQuery [$searchQuery] query [$query]")
                        onRepoClick(repo.id)*/
                        Toast.makeText(context, "Offline mode don't show the repo details", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                gitRepos?.let { pagingItems ->
                    PagedDataList(pagedData =pagingItems, itemContent = { repo: GitRepo ->
                        GitRepoItem(repo = repo) {
                            // Clear search query on item click
                            searchQuery = query
                            Log.d(TAG, "HomeScreen:onRepoClick => searchQuery [$searchQuery] query [$query]")
                            onRepoClick(repo.id)
                        }
                    })
                } ?: PlaceHolder(
                    enable = false,
                    messageText = "Enter keywords in the search bar to fetch the repositories using 'GitApi'",
                    messageFontSize = MaterialTheme.typography.labelLarge.fontSize,
                    messageFontWeight = FontWeight.Bold,
                    drawable = R.drawable.git_image,
                    tint = Color.Green.copy(alpha = 0.5f),
                    onClick = {}
                )
            }
        }

    }
}


