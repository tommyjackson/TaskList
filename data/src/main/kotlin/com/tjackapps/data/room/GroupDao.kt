package com.tjackapps.data.room

import androidx.room.*
import com.tjackapps.data.model.Group
import io.reactivex.Single

@Dao
interface GroupDao {

    /**
     * Gets all groups in the database
     */
    @Query("SELECT * FROM `Group`")
    fun getGroups(): Single<List<Group>>

    /**
     * Adds a group to the database
     *
     * @param group: The group to add
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addGroup(group: Group): Single<Long>

    /**
     * Updates a group in the database
     *
     * @param group: The group to update
     */
    @Update
    fun updateGroup(group: Group): Single<Int>

    /**
     * Deletes a group from the database
     *
     * @param group: The group to delete
     */
    @Delete
    fun deleteGroup(group: Group): Single<Int>
}