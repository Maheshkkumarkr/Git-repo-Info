package com.mahikr.gitrepoinfo.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mahikr.gitrepoinfo.data.local.dao.GitRepoDao
import com.mahikr.gitrepoinfo.data.local.model.GitRepoEntity

//Represent the database
@Database(entities = [GitRepoEntity::class], version = 1)
abstract class GitRepoDatabase : RoomDatabase() {

    abstract fun getGitRepoDao(): GitRepoDao

}