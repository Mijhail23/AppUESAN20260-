package dev.lchang.appuesan.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_teams",
    indices = [Index(value = ["apiTeamId"], unique = true)]
)
data class FavoriteTeamEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    //Id del equipo en API Football (null si se registró manualmente)
    val apiTeamId: Int? = null,
    val name: String,
    val country: String,
    val logo: String? = null,
    val founded: Int? = null,
    val note: String = ""
)
