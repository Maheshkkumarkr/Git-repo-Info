package com.mahikr.gitrepoinfo.domain.usecase

import com.mahikr.gitrepoinfo.data.connectivity.ConnectivityCallback
import javax.inject.Inject

/****GetConnectivityStatusUseCase
 * */
class GetConnectivityStatusUseCase @Inject constructor(
    private val connectivityCallback: ConnectivityCallback
) {
    operator fun invoke() = connectivityCallback.connectivityFlow
}