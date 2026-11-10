package com.griffith.shakespeak

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
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
    val typedText = remember { mutableStateOf("") }
    val phrases = remember { mutableStateListOf("I need help") }
    val context = LocalContext.current

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

            TextField(
                value = typedText.value,
                onValueChange = { typedText.value = it },
                label = { Text("Type a phrase") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = {
                if (typedText.value.isNotBlank()) {
                    phrases.add(typedText.value.trim())
                    typedText.value = ""
                }
            }) {
                Text("Add phrase")
            }

            Button(onClick = {
                // implicit intent: we say WHAT to do (send text), not WHICH app
                val sendIntent = Intent(Intent.ACTION_SEND)
                sendIntent.type = "text/plain"
                sendIntent.putExtra(Intent.EXTRA_TEXT, phrase)
                // the chooser shows the user a menu of apps that can send text
                val chooser = Intent.createChooser(sendIntent, "Share phrase with")
                try {
                    context.startActivity(chooser)
                } catch (e: ActivityNotFoundException) {
                    // no app could handle it, so show a short message instead of crashing
                    Toast.makeText(context, "No app found to share with", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Share phrase")
            }

            HorizontalDivider()

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