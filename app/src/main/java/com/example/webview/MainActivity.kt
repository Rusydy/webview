package com.example.webview

import androidx.activity.ComponentActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebViewScreen()
        }
    }
}

@Composable
fun WebViewScreen() {
    var loading by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        WebViewComponent(
            url = "https://www.google.com",
            onLoadingStateChanged = { loading = it }
        )
    }
}

@Composable
fun WebViewComponent(url: String, onLoadingStateChanged: (Boolean) -> Unit) {
    var loading by remember { mutableStateOf(true) }
    var progress by remember { mutableStateOf(0) }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        GlobalScope.launch {
                            delay(1000)
                            onLoadingStateChanged(false)
                            loading = false
                        }
                    }
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)
                        progress = newProgress
                        // You can use 'progress' to update your UI accordingly
                    }
                }

                loadUrl(url)
            }
        },
        update = { webView ->
            webView.loadUrl(url)
        },
        modifier = Modifier.fillMaxSize()
    )

    if (loading) {
        LoadingProgress(progress = progress)
    }
}

@Composable
fun LoadingProgress(progress: Int) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp),
                color = MaterialTheme.colorScheme.primary,
                progress = (progress / 100f).coerceIn(0f, 1f)
            )
            Text(text = "Loading... $progress%")
        }
    }
}
