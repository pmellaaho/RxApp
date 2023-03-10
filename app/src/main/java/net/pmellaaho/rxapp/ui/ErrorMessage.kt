package net.pmellaaho.rxapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.themeadapter.material.MdcTheme

@Composable
fun ErrorMessage() {
    Surface(color = Color.White) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Error loading data")
        }
    }
}

@Preview("ErrorMessage")
@Composable
private fun PreviewErrorMessage() {
    MdcTheme {
        ErrorMessage()
    }
}