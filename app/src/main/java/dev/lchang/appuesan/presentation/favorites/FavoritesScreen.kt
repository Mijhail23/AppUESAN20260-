package dev.lchang.appuesan.presentation.favorites

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import dev.lchang.appuesan.data.local.FavoriteTeamEntity

@Composable
fun FavoritesScreen(viewModel: FavoritesViewModel = viewModel()) {
    val favorites by viewModel.favorites.collectAsState()

    var name by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var editingTeam by remember { mutableStateOf<FavoriteTeamEntity?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Equipos favoritos (Room)",
            style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        //Formulario para registrar un favorito manualmente
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre del equipo") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = country,
            onValueChange = { country = it },
            label = { Text("País") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Nota") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                viewModel.addFavorite(name, country, note)
                name = ""
                country = ""
                note = ""
            },
            enabled = name.isNotBlank() && country.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar favorito")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (favorites.isEmpty()) {
            Text("Aún no tienes equipos favoritos")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favorites, key = { it.id }) { team ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (team.logo != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(team.logo),
                                    contentDescription = "Logo del equipo",
                                    modifier = Modifier.size(48.dp),
                                    contentScale = ContentScale.Fit
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(team.name, style = MaterialTheme.typography.titleMedium)
                                Text(team.country)
                                team.founded?.let { Text("Fundado: $it") }
                                if (team.note.isNotBlank()) {
                                    Text(team.note, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            IconButton(onClick = { editingTeam = team }) {
                                Icon(Icons.Default.Edit, "Editar nota")
                            }
                            IconButton(onClick = { viewModel.deleteFavorite(team) }) {
                                Icon(Icons.Default.Delete, "Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }

    editingTeam?.let { team ->
        var editedNote by remember(team.id) { mutableStateOf(team.note) }
        AlertDialog(
            onDismissRequest = { editingTeam = null },
            title = { Text("Editar nota de ${team.name}") },
            text = {
                OutlinedTextField(
                    value = editedNote,
                    onValueChange = { editedNote = it },
                    label = { Text("Nota") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateNote(team, editedNote)
                    editingTeam = null
                }) { Text("Actualizar") }
            },
            dismissButton = {
                TextButton(onClick = { editingTeam = null }) { Text("Cancelar") }
            }
        )
    }
}
