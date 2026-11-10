package com.griffith.shakespeak

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Show the Home screen when the app starts
            HomeScreen()
        }
    }
}

@Composable
fun HomeScreen() {
    // Sticky note: remembers if shake detection is ON (true) or OFF (false)
    val shakeOn = remember { mutableStateOf(true) }

    // Surface = the background of the whole screen
    Surface(modifier = Modifier.fillMaxSize()) {
        // Column = stack everything from top to bottom
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()   // keeps text below the clock/battery bar
                .padding(24.dp),       // empty space around the edges
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)  // gap between pieces
        ) {
            // Title
            Text("ShakeSpeak", fontSize = 32.sp, fontWeight = FontWeight.Bold)

            // Label and the phrase
            Text("Active phrase:")
            Text("\"I need help\"", fontSize = 24.sp)

            // Row = label and Switch side by side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Shake detection")
                // When tapped, "it" is the new ON/OFF value, and we save it in the sticky note
                Switch(
                    checked = shakeOn.value,
                    onCheckedChange = { shakeOn.value = it }
                )
            }

            // Backup button. onClick is empty for now, as the step says
            Button(onClick = { }) {
                Text("Speak now")
            }
        }
    }
}

// Lets us see the screen in the Design/Split view without running the emulator
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}