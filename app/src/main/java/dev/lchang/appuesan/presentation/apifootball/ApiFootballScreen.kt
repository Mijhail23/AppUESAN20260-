package dev.lchang.appuesan.presentation.apifootball

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter

@Composable
fun ApiFootballScreen(viewModel: ApiFootballViewModel = viewModel()){
    val countries by viewModel.countries.collectAsState()
    val selectedCountry by viewModel.selectedCountry.collectAsState()
    val teams by viewModel.teams.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.error.collectAsState()
    val favoriteTeamIds by viewModel.favoriteTeamIds.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ){
        Text("Equipos de fútbol por país",
            style = MaterialTheme.typography.titleLarge)
        Box{
            OutlinedButton(
                onClick = {expanded = true},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedCountry?.name ?: "Selecciona un país")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {expanded = false},
                modifier = Modifier.fillMaxWidth()){
                countries.forEach { country ->
                    DropdownMenuItem(
                        text = {Text(country.name)},
                        onClick = {
                            viewModel.onCountrySelected(country)
                            expanded = false
                        }
                    )
                }
            }

        }//close box
        Spacer(modifier = Modifier.height(16.dp))
        if(isLoading){
            CircularProgressIndicator()
        }else if(errorMessage != null){
            Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
        }else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(teams){wrapper ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(4.dp)
                    ){
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(wrapper.team.logo),
                                contentDescription = "Logo del equipo",
                                modifier = Modifier.height(50.dp),
                                contentScale = ContentScale.Crop
                            )
                           Spacer(modifier = Modifier.height(16.dp))
                            Column {
                                Text(wrapper.team.name, style = MaterialTheme.typography.titleMedium)
                                Text(wrapper.team.country)
                                Text(wrapper.team.founded?.toString() ?: "")
                            }
                            val isFavorite = wrapper.team.id in favoriteTeamIds
                            IconButton(onClick = { viewModel.toggleFavorite(wrapper.team) }) {
                                Icon(
                                    Icons.Default.Favorite,
                                    contentDescription = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                                    tint = if (isFavorite) MaterialTheme.colorScheme.error
                                           else MaterialTheme.colorScheme.outlineVariant
                                )
                            }

                        }
                    }
                }
            }
        }

    }

}