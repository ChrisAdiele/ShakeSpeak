package com.griffith.shakespeak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val phrase = intent.getStringExtra(EXTRA_PHRASE) ?: "No phrase"
        setContent {
            SettingsScreen(phrase)
        }
    }
}

@Composable
fun SettingsScreen(phrase: String) {
    // sticky note for the slider position (starts in the middle)
    val sensitivity = remember { mutableStateOf(5f) }
    // sticky note for the checkbox (starts ticked off)
    val keepHistory = remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Settings", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("Current phrase: $phrase")

            HorizontalDivider()

            Text("Shake sensitivity")
            // slider from 1 to 10, moving in whole steps
            Slider(
                value = sensitivity.value,
                onValueChange = { sensitivity.value = it },
                valueRange = 1f..10f,
                steps = 8
            )
            Text("Value: ${sensitivity.value.toInt()}")

            HorizontalDivider()

            // checkbox and its label side by side
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = keepHistory.value,
                    onCheckedChange = { keepHistory.value = it }
                )
                Text("Keep a history")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen("I need help")
}