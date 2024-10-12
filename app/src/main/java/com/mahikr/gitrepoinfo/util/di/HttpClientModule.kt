package com.mahikr.gitrepoinfo.util.di

import com.mahikr.gitrepoinfo.data.remote.httpclient.GitHttpServer
import com.mahikr.gitrepoinfo.data.repo.GetRepositoriesImpl
import com.mahikr.gitrepoinfo.domain.repo.IGitRepoWrapper
import com.mahikr.gitrepoinfo.util.constants.Constants.GIT_API_BASE_URL
import com.mahikr.gitrepoinfo.util.constants.Constants.TEMP_TOKEN
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object HttpClientModule {

    //Provide DI to the HttpLoggingInterceptor and creates the instance of the HttpLoggingInterceptor
    @Provides
    @Singleton
    fun provideHttpLoggerInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().also {
        it.level = HttpLoggingInterceptor.Level.BODY
    }

    //Provide DI to the OkHttpClient and creates the instance of the it
    @Provides
    @Singleton
    fun provideOkHttp(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "token $TEMP_TOKEN") //valid till Expires on Wed, Dec 11 2024.
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(httpLoggingInterceptor).build()

    //Provide DI to the Retrofit and creates the instance of the it
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder().baseUrl(GIT_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    //Provide DI to the GitHttpServer and creates the instance of the GitHttpServer
    @Provides
    @Singleton
    fun providesRetrofitService(retrofit: Retrofit): GitHttpServer =
        retrofit.create(GitHttpServer::class.java)

    //Provide DI to the IGitRepoWrapper and creates the instance of the GetRepositoriesImpl
    @Provides
    @Singleton
    fun providesIGitRepoWrapper(gitHttpServer: GitHttpServer): IGitRepoWrapper =
        GetRepositoriesImpl(gitHttpServer)


}
