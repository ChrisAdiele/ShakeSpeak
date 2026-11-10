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

// named the label on the envelope Phrases
//home screen and other screens use the same name so phrases doesnt get lost
const val EXTRA_PHRASE = "phrase"

//the class runs the home and says what to draw(this case the home screen )
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
    //remember the on and off and redraws screen when its changed
    val shakeOn = remember { mutableStateOf(true) }
    val activePhrase = "I need help" // word to be spoken
    val context = LocalContext.current // this is to open another screen

    //background styling
    Surface(modifier = Modifier.fillMaxSize()) {
        //top to bottom styling
        Column(
            modifier = Modifier

                .fillMaxSize()
                .statusBarsPadding() //keeps the content below the clock and battery bar
                .padding(24.dp), //space around the edges
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("ShakeSpeak", fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Text("Active phrase:")
            Text("\"$activePhrase\"", fontSize = 24.sp)

            //this puts the label and on and off switch side by side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Shake detection")
                Switch(
                    checked = shakeOn.value,
                    onCheckedChange = { shakeOn.value = it }// new on and off switch value is "it " and gets saved on shakeOn
                )
            }
            //Speak now button
            Button(onClick = { }) {
                Text("Speak now")
            }
            //  Phrases  button
            Button(onClick = {
                //  explicit intent names the exact screen to open
                val intent = Intent(context, PhrasesActivity::class.java)
                intent.putExtra(EXTRA_PHRASE, activePhrase)  // put the phrase in the envelope
                context.startActivity(intent)
            }) {
                Text("Phrases")
            }

            //Settings button
            Button(onClick = {
                //  explicit intent names the exact screen to open
                val intent = Intent(context, SettingsActivity::class.java)
                intent.putExtra(EXTRA_PHRASE, activePhrase)
                context.startActivity(intent)
            }) {
                Text("Settings")
            }
        }
    }
}

@Preview(showBackground = true) // allows to show screen in the design view tab without the emulator
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}