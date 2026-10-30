package dev.lchang.appuesan.presentation.apifootball

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.lchang.appuesan.data.local.AppDatabase
import dev.lchang.appuesan.data.local.FavoriteTeamEntity
import dev.lchang.appuesan.data.remote.apifootball.Country
import dev.lchang.appuesan.data.remote.apifootball.RetrofitInstance
import dev.lchang.appuesan.data.remote.apifootball.Team
import dev.lchang.appuesan.data.remote.apifootball.TeamWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ApiFootballViewModel(application: Application): AndroidViewModel(application){

    private val favoriteDao = AppDatabase.getInstance(application).favoriteTeamDao()
    val favoriteTeamIds: StateFlow<Set<Int>> = favoriteDao.getFavoriteApiTeamIds()
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    private val _countries = MutableStateFlow<List<Country>>(emptyList())
    val countries: StateFlow<List<Country>> = _countries
    private val _selectedCountry = MutableStateFlow<Country?>(null)
    val selectedCountry: StateFlow<Country?> = _selectedCountry
    private val _teams = MutableStateFlow<List<TeamWrapper>>(emptyList())
    val teams: StateFlow<List<TeamWrapper>> = _teams
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _errorMessages = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _errorMessages

    init {
        loadCountries()
    }

    fun loadCountries(){
        viewModelScope.launch {
            _isLoading.value  = true
            try {
                val response = RetrofitInstance.api.getCountries()
                _countries.value = response.response
            }catch (e: Exception){
                _errorMessages.value = e.message
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun loadTeamsByCountry(country: Country){
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.getTeamsByCountry(country.name)
                _teams.value = response.response
                _errorMessages.value = null
                _selectedCountry.value = country
            }catch (e: Exception){
                _errorMessages.value = e.message
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun onCountrySelected(country: Country){
        _selectedCountry.value = country
        loadTeamsByCountry(country)
    }

    fun toggleFavorite(team: Team){
        viewModelScope.launch {
            if (team.id in favoriteTeamIds.value){
                favoriteDao.deleteByApiTeamId(team.id)
            }else{
                favoriteDao.insert(
                    FavoriteTeamEntity(
                        apiTeamId = team.id,
                        name = team.name,
                        country = team.country,
                        logo = team.logo,
                        founded = team.founded
                    )
                )
            }
        }
    }
}
