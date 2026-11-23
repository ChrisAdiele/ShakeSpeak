package com.griffith.shakespeak

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
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
class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private val x = mutableStateOf(0f)
    private val y = mutableStateOf(0f)
    private val z = mutableStateOf(0f)
    private val sensorAvailable = mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer == null) {
            sensorAvailable.value = false
        }

        setContent {
            HomeScreen(x.value, y.value, z.value, sensorAvailable.value)
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            x.value = event.values[0]
            y.value = event.values[1]
            z.value = event.values[2]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("ShakeSpeak", "Accuracy: $accuracy")
    }
}

@Composable
fun HomeScreen(x: Float, y: Float, z: Float, sensorAvailable: Boolean)  {
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
            // TEMP: remove later
            if (sensorAvailable) {
                Text("x: ${"%.2f".format(x)}  y: ${"%.2f".format(y)}  z: ${"%.2f".format(z)}")
            } else {
                Text("No accelerometer on this device")
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
    HomeScreen(0f, 9.8f, 0f, true)
}