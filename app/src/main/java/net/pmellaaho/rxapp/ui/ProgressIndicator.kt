package net.pmellaaho.rxapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.themeadapter.material.MdcTheme

@Composable
fun ProgressIndicator() {
    Surface(color = Color.White) {
        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
}

@Preview("ProgressIndicator")
@Composable
private fun PreviewProgressIndicator() {
    MdcTheme {
       ProgressIndicator()
    }
}