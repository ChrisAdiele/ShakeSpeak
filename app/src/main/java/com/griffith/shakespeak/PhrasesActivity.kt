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

//opens the envelope home sent
class PhrasesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //deals with null sends back 'no phrases'
        val phrase = intent.getStringExtra(EXTRA_PHRASE) ?: "No phrase"
        setContent {
            PhrasesScreen(phrase)
        }
    }
}

@Composable
fun PhrasesScreen(phrase: String) {
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
            Text("Phrases", fontSize = 28.sp, fontWeight = FontWeight.Bold)
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
                if (typedText.value.isNotBlank()) { //only adds if something is typed
                    phrases.add(typedText.value.trim())//adds and removes any extra space at the end
                    typedText.value = "" //clears the box after adding
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

            for (item in phrases) { //a line of text for every phrase
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