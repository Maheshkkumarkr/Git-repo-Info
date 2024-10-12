package com.mahikr.gitrepoinfo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahikr.gitrepoinfo.domain.usecase.GetConnectivityStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


/***** ConnectivityViewModel
 * @param getConnectivityStatus
 *
 * provides the network connectivity/Internet availability status using the GetConnectivityStatusUseCase
 */
@HiltViewModel
class ConnectivityViewModel @Inject constructor(
    getConnectivityStatus: GetConnectivityStatusUseCase,
) : ViewModel() {

    //on flow subscribe it'll start emission and terminates it on 5 seconds of collectors inactivity
    val connectivityStatus = getConnectivityStatus().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = true
    )

}