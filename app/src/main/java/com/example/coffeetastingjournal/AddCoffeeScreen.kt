package com.example.coffeetastingjournal

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun AddCoffeeScreen(onDone: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { CoffeeRepository() }

    var name by remember { mutableStateOf("") }
    var cafe by remember { mutableStateOf("") }
    var origin by remember { mutableStateOf("") }
    var roast by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var ratingError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Add Coffee", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it; nameError = null },
            label = { Text("Name") },
            isError = nameError != null,
            supportingText = { nameError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = cafe,
            onValueChange = { cafe = it },
            label = { Text("Cafe") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = origin,
            onValueChange = { origin = it },
            label = { Text("Origin") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = roast,
            onValueChange = { roast = it },
            label = { Text("Roast") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = rating,
            onValueChange = { rating = it; ratingError = null },
            label = { Text("Rating (1-5)") },
            isError = ratingError != null,
            supportingText = { ratingError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = date,
            onValueChange = { date = it; dateError = null },
            label = { Text("Date (yyyy-MM-dd)") },
            isError = dateError != null,
            supportingText = { dateError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            enabled = !saving,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val ratingValue = rating.trim().toIntOrNull()
                nameError = if (name.isBlank()) "Name is required" else null
                ratingError =
                    if (ratingValue == null || ratingValue !in 1..5) "Rating must be between 1 and 5" else null
                dateError =
                    if (!isValidDate(date.trim())) "Use a valid date (yyyy-MM-dd)" else null

                if (nameError != null || ratingError != null || dateError != null) return@Button

                saving = true
                repository.add(
                    Coffee(
                        name = name.trim(),
                        cafe = cafe.trim(),
                        origin = origin.trim(),
                        roast = roast.trim(),
                        rating = ratingValue!!,
                        notes = notes.trim(),
                        date = date.trim()
                    ),
                    onSuccess = {
                        Toast.makeText(context, "Coffee saved", Toast.LENGTH_SHORT).show()
                        saving = false
                        name = ""; cafe = ""; origin = ""; roast = ""
                        rating = ""; date = ""; notes = ""
                        onDone()
                    },
                    onError = { message ->
                        saving = false
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    }
                )
            }
        ) {
            Text("Save")
        }
    }
}

private fun isValidDate(text: String): Boolean {
    if (text.isEmpty()) return false
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        format.isLenient = false
        format.parse(text) != null
    } catch (e: Exception) {
        false
    }
}