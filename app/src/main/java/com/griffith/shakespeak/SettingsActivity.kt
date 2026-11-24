package com.griffith.shakespeak

import com.griffith.shakespeak.ui.theme.ShakeSpeakTheme
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import android.os.Bundle
import android.content.Context
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
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val saved = prefs.getFloat("sensitivity", 5f) // read once, outside the composable
        setContent {
            ShakeSpeakTheme {
                SettingsScreen(phrase, saved) { value ->
                    prefs.edit().putFloat("sensitivity", value).apply()
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(phrase: String, savedSensitivity: Float, onSensitivitySaved: (Float) -> Unit) {
    val sensitivity =
        remember { mutableStateOf(savedSensitivity) } //starts at the saved value and remembers where the slider is
    val keepHistory = remember { mutableStateOf(false) } //remebers if checkbox is checked

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ScreenTitle("Settings")
            Text("Current phrase: $phrase")

            HorizontalDivider()

            Text("Shake sensitivity")
            // slider moves in whole numbers and saved in sensitivity
            Slider(
                value = sensitivity.value,
                onValueChange = { sensitivity.value = it },
                onValueChangeFinished = { onSensitivitySaved(sensitivity.value) },
                valueRange = 1f..10f,
                steps = 8,
                modifier = Modifier.semantics { contentDescription = "Shake sensitivity slider" }
            )
            //shows slider number and turns decimal to whole number
            Text("Value: ${sensitivity.value.toInt()}")

            HorizontalDivider()

            Row(verticalAlignment = Alignment.CenterVertically) { //box and label side by side
                Checkbox(
                    checked = keepHistory.value,
                    onCheckedChange = {
                        keepHistory.value = it
                    }, //ticked = true and saved in keepHistory
                    modifier = Modifier.semantics { contentDescription = "Keep a history checkbox" }
                )
                Text("Keep a history")
            }
        }
    }
}
//In Milestone 2 slider will connect to shake sensor
//controls dont do anything yet
//In Milestone 3 'Keep a history' will use saved data
@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen("I need help", 5f) {}
}
