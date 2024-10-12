package com.mahikr.gitrepoinfo.util.di

import android.content.Context
import androidx.room.Room
import com.mahikr.gitrepoinfo.data.local.dao.GitRepoDao
import com.mahikr.gitrepoinfo.data.local.db.GitRepoDatabase
import com.mahikr.gitrepoinfo.data.repo.GitRepoDaoWrapperImpl
import com.mahikr.gitrepoinfo.domain.repo.IGitRepoDaoWrapper
import com.mahikr.gitrepoinfo.util.constants.Constants.GIT_REPO_DATABASE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/***** DbModule
 * Provides the dependency injection to the Database related classes and Repositories
 * ex: Database,DAO, and Repository
 */

@InstallIn(SingletonComponent::class)
@Module
object DbModule {


    //Provide DI to the GitRepoDatabase and creates the instance of the GitRepoDatabase
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): GitRepoDatabase {
        return Room.databaseBuilder(
            context,
            GitRepoDatabase::class.java,
            GIT_REPO_DATABASE
        ).build()
    }

    //Provide DI to the GitRepoDao and creates the instance of the GitRepoDao
    @Provides
    @Singleton
    fun provideGitDao(
        gitRepoDatabase: GitRepoDatabase
    ): GitRepoDao = gitRepoDatabase.getGitRepoDao()

    //Provide DI to the IGitRepoDaoWrapper and creates the instance of the IGitRepoDaoWrapper
    @Provides
    @Singleton
    fun provideIGitRepoDaoWrapper(
        gitRepoDao: GitRepoDao
    ): IGitRepoDaoWrapper = GitRepoDaoWrapperImpl(gitRepoDao)




}