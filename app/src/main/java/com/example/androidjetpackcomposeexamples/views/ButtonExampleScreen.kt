package com.example.androidjetpackcomposeexamples.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.androidjetpackcomposeexamples.R

@Composable
fun ButtonExampleScreen() {
    var clicks by rememberSaveable { mutableStateOf(0) }
    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { clicks++ }) {
            Text(stringResource(R.string.button_example_action))
        }
        Text(stringResource(R.string.button_example_click_count, clicks))
    }
}
