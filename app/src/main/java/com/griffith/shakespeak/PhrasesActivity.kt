package com.griffith.shakespeak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
        setContent {
            PhrasesScreen()
        }
    }
}

@Composable
fun PhrasesScreen() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text(
            "Phrases screen",
            modifier = Modifier.statusBarsPadding().padding(24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PhrasesScreenPreview() {
    PhrasesScreen()
}