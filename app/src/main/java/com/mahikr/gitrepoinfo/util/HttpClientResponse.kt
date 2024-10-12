package com.mahikr.gitrepoinfo.util

/***** HttpClientResponse
 * Wrapper to the HttpResponse to handle the Success and Failure case.
 *
 */

sealed interface HttpClientResponse<T> {

    class Success<T>(val data: T) : HttpClientResponse<T>
    class Failure<T>(val exception: Exception) : HttpClientResponse<T>

    //Builder pattern
    fun onSuccess(block: (T) -> Unit): HttpClientResponse<T> {
        if (this is Success) block(this.data)
        return this
    }

    fun onFailure(block: (Exception) -> Unit): HttpClientResponse<T> {
        if (this is Failure) block(this.exception)
        return this
    }

}

//Builder pattern to the HttpClientResponse
inline fun <T> onSafeApiCall(apiCall: () -> T): HttpClientResponse<T> {
    return try {
        HttpClientResponse.Success(apiCall())
    }catch (exception: Exception) {
        HttpClientResponse.Failure(exception)
    }
}