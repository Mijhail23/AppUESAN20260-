package dev.lchang.appuesan.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTeamDao {

    @Query("SELECT * FROM favorite_teams ORDER BY name ASC")
    fun getAll(): Flow<List<FavoriteTeamEntity>>

    @Query("SELECT apiTeamId FROM favorite_teams WHERE apiTeamId IS NOT NULL")
    fun getFavoriteApiTeamIds(): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(team: FavoriteTeamEntity)

    @Update
    suspend fun update(team: FavoriteTeamEntity)

    @Delete
    suspend fun delete(team: FavoriteTeamEntity)

    @Query("DELETE FROM favorite_teams WHERE apiTeamId = :apiTeamId")
    suspend fun deleteByApiTeamId(apiTeamId: Int)
}
