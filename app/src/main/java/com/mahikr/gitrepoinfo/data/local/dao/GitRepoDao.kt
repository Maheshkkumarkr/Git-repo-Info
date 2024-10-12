package com.mahikr.gitrepoinfo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mahikr.gitrepoinfo.data.local.model.GitRepoEntity

import kotlinx.coroutines.flow.Flow


/***** GitRepoDao
 * to interact with the local cached data
 */
@Dao
interface GitRepoDao {
    //insert all data into DB
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(gitRepos: List<GitRepoEntity>)

    //fetches all data into DB which matches the query
    @Query("SELECT * FROM git_repos WHERE `query` IS NOT NULL AND `query`= :query ORDER BY name ASC")
    fun getGitRepos(query: String): Flow<List<GitRepoEntity>>

    //fetches all data into DB
    @Query("SELECT * FROM GIT_REPOS")
    fun getAllGitRepos(): Flow<List<GitRepoEntity>>

    //delete all data in DB
    @Query("DELETE FROM git_repos")
    suspend fun clearRepos()


    @Query("SELECT * FROM git_repos WHERE id = :id")
    fun getGitRepoById(id: Int): Flow<GitRepoEntity>



}