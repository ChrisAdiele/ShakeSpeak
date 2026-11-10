package com.griffith.shakespeak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class PhrasesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val phrase = intent.getStringExtra(EXTRA_PHRASE) ?: "No phrase"
        setContent {
            PhrasesScreen(phrase)
        }
    }
}

@Composable
fun PhrasesScreen(phrase: String) {
    // sticky note for what the user is typing right now
    val typedText = remember { mutableStateOf("") }
    // sticky note for the whole list of saved phrases
    val phrases = remember { mutableStateListOf("I need help") }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Phrases", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("Current phrase: $phrase")

            // box where the user types a new phrase
            TextField(
                value = typedText.value,
                onValueChange = { typedText.value = it },
                label = { Text("Type a phrase") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = {
                // only add if something was typed
                if (typedText.value.isNotBlank()) {
                    phrases.add(typedText.value.trim())
                    typedText.value = ""   // clear the box
                }
            }) {
                Text("Add phrase")
            }

            HorizontalDivider()

            // one Text for every saved phrase
            for (item in phrases) {
                Text("- $item")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PhrasesScreenPreview() {
    PhrasesScreen("I need help")
}