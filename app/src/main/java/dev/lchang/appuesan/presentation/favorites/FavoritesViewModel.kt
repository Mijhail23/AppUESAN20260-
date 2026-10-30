package dev.lchang.appuesan.presentation.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.lchang.appuesan.data.local.AppDatabase
import dev.lchang.appuesan.data.local.FavoriteTeamEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).favoriteTeamDao()

    val favorites: StateFlow<List<FavoriteTeamEntity>> = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addFavorite(name: String, country: String, note: String) {
        viewModelScope.launch {
            dao.insert(
                FavoriteTeamEntity(
                    name = name.trim(),
                    country = country.trim(),
                    note = note.trim()
                )
            )
        }
    }

    fun updateNote(team: FavoriteTeamEntity, note: String) {
        viewModelScope.launch {
            dao.update(team.copy(note = note.trim()))
        }
    }

    fun deleteFavorite(team: FavoriteTeamEntity) {
        viewModelScope.launch {
            dao.delete(team)
        }
    }
}
