package com.griffith.shakespeak

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// the name written on the envelope; both sides use this same name
const val EXTRA_PHRASE = "phrase"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen()
        }
    }
}

@Composable
fun HomeScreen() {
    val shakeOn = remember { mutableStateOf(true) }
    val activePhrase = "I need help"
    // the context is needed to open another screen
    val context = LocalContext.current

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("ShakeSpeak", fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Text("Active phrase:")
            Text("\"$activePhrase\"", fontSize = 24.sp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Shake detection")
                Switch(
                    checked = shakeOn.value,
                    onCheckedChange = { shakeOn.value = it }
                )
            }

            Button(onClick = { }) {
                Text("Speak now")
            }

            Button(onClick = {
                // explicit intent: names the exact screen to open
                val intent = Intent(context, PhrasesActivity::class.java)
                intent.putExtra(EXTRA_PHRASE, activePhrase)  // put the phrase in the envelope
                context.startActivity(intent)
            }) {
                Text("Phrases")
            }

            Button(onClick = {
                val intent = Intent(context, SettingsActivity::class.java)
                intent.putExtra(EXTRA_PHRASE, activePhrase)
                context.startActivity(intent)
            }) {
                Text("Settings")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}