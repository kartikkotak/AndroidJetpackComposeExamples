package com.example.androidjetpackcomposeexamples.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.androidjetpackcomposeexamples.ui.theme.AndroidJetpackComposeExamplesTheme
import com.example.androidjetpackcomposeexamples.viewmodel.MainLayoutViewModel
import com.example.androidjetpackcomposeexamples.views.AppNavigator

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var navTitle by remember { mutableStateOf("Compose Examples") }
            AndroidJetpackComposeExamplesTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopAppBar(title = { Text(navTitle, fontWeight = FontWeight.Bold) }) },
                ) { innerPadding ->
                    AppNavigator(
                        modifier = Modifier.padding(innerPadding),
                        updateTitle = { title -> navTitle = title }
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidJetpackComposeExamplesTheme {
        Greeting("Android")
    }
}