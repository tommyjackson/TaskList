package com.tjackapps.data.room

import androidx.room.*
import com.tjackapps.data.model.Group
import io.reactivex.Single

@Dao
interface GroupDao {

    @Query("SELECT * FROM `Group`")
    fun getGroups(): Single<List<Group>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addGroup(group: Group): Single<Long>

    @Update
    fun updateGroup(group: Group): Single<Int>

    @Delete
    fun deleteGroup(group: Group): Single<Int>
}