package com.griffith.shakespeak

import com.griffith.shakespeak.ui.theme.ShakeSpeakTheme
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.layout.height
import kotlin.math.sqrt
import android.speech.tts.TextToSpeech
import java.util.Locale
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
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

// EXTRA_PHRASE is the label on the envelope, because the other screen uses it to find the phrase.
// PREFS_NAME is the name of the notebook, because all three screens must use the same name.
const val EXTRA_PHRASE = "phrase"
const val PREFS_NAME = "shakespeak_prefs"

// MainActivity is the home screen, and it listens to the sensor and speaks the phrase, because it owns the state.
class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    // x, y and z hold the sensor readings, because the screen redraws when they change.
    private val x = mutableStateOf(0f)
    private val y = mutableStateOf(0f)
    private val z = mutableStateOf(0f)
    // sensorAvailable is true if the phone has an accelerometer, because otherwise the app shows a message.
    private val sensorAvailable = mutableStateOf(true)
    // shakeOn is true when the switch is on, because shakes are ignored when it is off.
    private val shakeOn = mutableStateOf(true)
    private val shakeDetected = mutableStateOf(false)
    private var lastShakeTime = 0L
    // shakeThreshold is how hard a shake must be, and it is a var because the slider changes it.
    private var shakeThreshold = 20f
    // cooldownMs is how long to wait between shakes, because one shake should not speak many times.
    private val cooldownMs = 2000L
    // tts is the speech engine, and it starts out empty because it is created later.
    private var tts: TextToSpeech? = null
    // ttsReady is true when the engine is ready, because speaking too early does nothing
    private var ttsReady = false
    // activePhrase is the phrase to say, and it is state so the screen updates when it changes.
    private val activePhrase = mutableStateOf("I need help")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ask Android for the sensor manager, because it is the way to reach the phone's sensors.
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // Get the accelerometer from the manager, because it measures how the phone is moving.
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        // If the phone has none, set sensorAvailable to false, because the screen then shows a message instead of numbers.
        if (accelerometer == null) {
            sensorAvailable.value = false
        }

        // Start the speech engine, and Android runs this code when it has finished starting.
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Set the voice to UK English, and only mark the engine ready if the voice is available.
                val result = tts?.setLanguage(Locale.UK)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // If the voice is missing, write a note in the log instead of crashing.
                    Log.d("ShakeSpeak", "UK voice not available")
                } else {
                    ttsReady = true
                }
            }
        }

        setContent {
            // Draw the Home screen inside the theme, so every screen shares the same colours.
            ShakeSpeakTheme {
                // Pass in the current values and two actions, because the activity owns the state and the screen only shows it.
                HomeScreen(
                    x.value, y.value, z.value, sensorAvailable.value,
                    shakeOn.value, { shakeOn.value = it }, shakeDetected.value,
                    activePhrase.value, { speakPhrase() }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Read the saved settings here, because onResume runs every time the screen comes back, so changes are picked up.
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val sensitivity = prefs.getFloat("sensitivity", 5f)
        // Higher sensitivity makes the threshold lower, so a lighter shake counts.
        shakeThreshold = 28f - 1.6f * sensitivity
        // Read the saved phrase, and use "I need help" if nothing is saved yet.
        activePhrase.value = prefs.getString("active_phrase", "I need help") ?: "I need help"

        // Listen only if the sensor exists, because not every phone has one.
        accelerometer?.let {
            // Use a medium speed, because a shake does not need fast readings and it saves battery.
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        // Stop listening when the user leaves the screen, so the sensor stops using power.
        sensorManager.unregisterListener(this)
    }



    private fun speakPhrase() {
        // Speak the active phrase, but only if the engine is ready, otherwise nothing is said.
        if (ttsReady) {
            // QUEUE_FLUSH drops anything waiting and says the new phrase now.
            tts?.speak(activePhrase.value, TextToSpeech.QUEUE_FLUSH, null, "shakespeak")
        }
    }

    // When the screen is closed, stop the speech and free the engine, so it does not keep using memory.
    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // Android calls this for every new reading, and we only use it if it came from the accelerometer.
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            x.value = event.values[0]
            y.value = event.values[1]
            z.value = event.values[2]

            // Strength is one number for how hard the phone is pushed, and it is about 9.8 at rest because of gravity.
            val strength = sqrt(x.value * x.value + y.value * y.value + z.value * z.value)
            // Get the time since the phone started, so the cooldown can measure the gap between shakes.
            val now = SystemClock.elapsedRealtime()

            // The shake only counts if all three checks are true: switch on, strong enough, cooldown over.
            if (shakeOn.value && strength > shakeThreshold && now - lastShakeTime > cooldownMs) {
                // Save the time of this shake, so the cooldown can start counting.
                lastShakeTime = now
                shakeDetected.value = true
                speakPhrase()
                // After 1 second, hide the message again, without freezing the screen.
                Handler(Looper.getMainLooper()).postDelayed({ shakeDetected.value = false }, 1000)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("ShakeSpeak", "Accuracy: $accuracy")
    }
}

@Composable
fun HomeScreen(
    x: Float, y: Float, z: Float, sensorAvailable: Boolean,
    shakeOn: Boolean, onShakeOnChange: (Boolean) -> Unit, shakeDetected: Boolean,
    activePhrase: String, onSpeak: () -> Unit
)  {
    // the Switch state now lives in MainActivity
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
            ScreenTitle("ShakeSpeak")
            Text("Active phrase:")
            Text("\"$activePhrase\"", fontSize = 32.sp, fontWeight = FontWeight.Medium)

            //this puts the label and on and off switch side by side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Shake detection")
                Switch(
                    checked = shakeOn,
                    onCheckedChange = onShakeOnChange,
                    modifier = Modifier.semantics { contentDescription = "Shake detection switch" }
                )
            }
            //Speak now button
            Button(
                onClick = onSpeak,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
            ) {
                Text("Speak now", fontSize = 24.sp)
            }
            // live sensor readout (shows the accelerometer working)
            if (sensorAvailable) {
                Text("x: ${"%.2f".format(x)}  y: ${"%.2f".format(y)}  z: ${"%.2f".format(z)}")
            } else {
                Text("No accelerometer on this device")
            }
            if (shakeDetected) {
                Text("Shake detected!")
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
    HomeScreen(0f, 9.8f, 0f, true, true, {}, false, "I need help", {})
}