package com.griffith.shakespeak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class PhrasesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // open the envelope; if it is empty (null), use a default
        val phrase = intent.getStringExtra(EXTRA_PHRASE) ?: "No phrase"
        setContent {
            PhrasesScreen(phrase)
        }
    }
}

@Composable
fun PhrasesScreen(phrase: String) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.statusBarsPadding().padding(24.dp)) {
            Text("Phrases screen")
            Text("Current phrase: $phrase")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PhrasesScreenPreview() {
    PhrasesScreen("I need help")
}