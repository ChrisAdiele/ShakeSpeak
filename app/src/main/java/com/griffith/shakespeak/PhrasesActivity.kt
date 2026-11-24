package com.griffith.shakespeak

import com.griffith.shakespeak.ui.theme.ShakeSpeakTheme
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
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

//opens the envelope home sent
class PhrasesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //deals with null sends back 'no phrases'
        val phrase = intent.getStringExtra(EXTRA_PHRASE) ?: "No phrase"
        setContent {
            ShakeSpeakTheme {
                PhrasesScreen(phrase) { chosen ->
                    // Save the chosen phrase in the notebook under "active_phrase", because Home reads it in onResume.
                    getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                        .edit().putString("active_phrase", chosen).apply()
                }
            }
        }
    }
}

@Composable
fun PhrasesScreen(phrase: String, onUsePhrase: (String) -> Unit) {
    val typedText = remember { mutableStateOf("") } //for remebering what the user is typing

    val phrases = remember { mutableStateListOf("I need help") } //the list of phrases
    val context = LocalContext.current // this is to open share and 'No apps found'


    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ScreenTitle("Phrases")
            Text("Current phrase: $phrase")

            //Text box
            TextField(
                value = typedText.value,
                onValueChange = { typedText.value = it },// new text value is "it " and gets saved on typedText
                label = { Text("Type a phrase") },
                modifier = Modifier.fillMaxWidth()
            )

            //Add button
            Button(onClick = {
                val text = typedText.value.trim() // Remove extra spaces at the start and end, so a phrase of only spaces counts as empty.
                // Reject an empty or too long phrase with a short message, because bad input should never be saved.
                if (text.isEmpty()) {
                    Toast.makeText(context, "Type a phrase first", Toast.LENGTH_SHORT).show()
                } else if (text.length > 100) {
                    Toast.makeText(context, "Phrase is too long (max 100)", Toast.LENGTH_SHORT).show()
                } else {
                    // Only a valid phrase is added to the list, and then the box is cleared.
                    phrases.add(text)
                    typedText.value = ""
                }
            }) {
                Text("Add phrase")
            }

            //Share Button
            Button(onClick = {
                // implicit intent says what to do , not WHICH app (send text)
                val sendIntent = Intent(Intent.ACTION_SEND)
                sendIntent.type = "text/plain"
                sendIntent.putExtra(Intent.EXTRA_TEXT, phrase)
                // chooser shows the user a selection of apps that can send text to
                val chooser = Intent.createChooser(sendIntent, "Share phrase with")
                try {
                    context.startActivity(chooser)
                } catch (e: ActivityNotFoundException) {
                    // exception handling show a short message instead of crashing
                    Toast.makeText(context, "No app found to share with", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Share phrase")
            }
            //divider between sections
            HorizontalDivider()

            for (item in phrases) {
                // Make one row for every phrase, with the text on the left and a button on the right.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("- $item")
                    // The label for screen readers includes the phrase, so each button sounds different.
                    Button(onClick = {
                        // Tapping the button saves this phrase as the active one and shows a short message.
                        onUsePhrase(item)
                        Toast.makeText(context, "Active phrase set", Toast.LENGTH_SHORT).show()
                    }, modifier = Modifier.semantics { contentDescription = "Use $item as the active phrase" }) {
                        Text("Use this phrase")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PhrasesScreenPreview() {
    PhrasesScreen("I need help") {}
}