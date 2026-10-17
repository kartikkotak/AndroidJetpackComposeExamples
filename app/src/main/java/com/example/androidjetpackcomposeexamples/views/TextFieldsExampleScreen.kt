package com.example.androidjetpackcomposeexamples.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun TextFieldsExampleScreen() {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var visible by rememberSaveable { mutableStateOf(false) }
    var notes by rememberSaveable { mutableStateOf("") }
    var submitted by rememberSaveable { mutableStateOf(false) }
    var enabled by rememberSaveable { mutableStateOf(true) }
    val focus = LocalFocusManager.current
    val emailError = email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    ExampleColumn {
        Text("Text field styles", style = MaterialTheme.typography.headlineSmall)
        Row { Text("Enable editable fields", Modifier.weight(1f)); Switch(enabled, { enabled = it }) }
        TextField(
            value = name, onValueChange = { name = it; submitted = false }, enabled = enabled,
            label = { Text("Name · filled") }, placeholder = { Text("Your name") },
            supportingText = { Text("Labels remain visible while typing") },
            singleLine = true, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        OutlinedTextField(
            value = email, onValueChange = { email = it; submitted = false }, enabled = enabled,
            label = { Text("Email · outlined") }, placeholder = { Text("you@example.com") },
            leadingIcon = { Icon(Icons.Default.Email, null) },
            trailingIcon = {
                if (email.isNotEmpty()) IconButton(onClick = { email = "" }, enabled = enabled) { Icon(Icons.Default.Clear, "Clear email") }
            },
            isError = emailError,
            supportingText = { Text(if (emailError) "Enter a valid email address" else "Validation runs as you type") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            singleLine = true, modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = password, onValueChange = { password = it; submitted = false }, enabled = enabled,
            label = { Text("Password") },
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { visible = !visible }, enabled = enabled) {
                    Icon(if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility, if (visible) "Hide password" else "Show password")
                }
            },
            supportingText = { Text("Use at least 8 characters for this example") },
            isError = password.isNotEmpty() && password.length < 8,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focus.clearFocus() }),
            singleLine = true, modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = notes, onValueChange = { if (it.length <= 200) notes = it }, enabled = enabled,
            label = { Text("Multiline notes") }, minLines = 3, maxLines = 5,
            supportingText = { Text("${notes.length}/200") }, modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(value = "Read-only value", onValueChange = {}, readOnly = true, label = { Text("Read only · selectable") }, modifier = Modifier.fillMaxWidth())
        TextField(value = "Disabled value", onValueChange = {}, enabled = false, label = { Text("Disabled") }, modifier = Modifier.fillMaxWidth())
        Button(
            onClick = { submitted = true; focus.clearFocus() },
            enabled = enabled && name.isNotBlank() && email.isNotBlank() && !emailError && password.length >= 8,
        ) { Text("Validate form") }
        if (submitted) Text("Form is valid. This demo does not send your data.")
    }
}
