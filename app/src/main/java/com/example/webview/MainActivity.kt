package com.example.webview

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebViewScreen()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun WebViewScreen() {
    var loading by remember { mutableStateOf(true) }
    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        WebViewComponent(url = "https://www.google.com", onLoadingStateChanged = { loading = it })
    }
}

@OptIn(DelicateCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun WebViewComponent(url: String, onLoadingStateChanged: (Boolean) -> Unit) {
    var loading by remember { mutableStateOf(true) }
    var progress by remember { mutableStateOf(0) }
    var internetQuality by remember { mutableStateOf(InternetQuality.UNKNOWN) }

    AndroidView(factory = { context ->
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
    }, update = { webView ->
        webView.loadUrl(url)
    }, modifier = Modifier.fillMaxSize()
    )

    if (loading) {
        val context = LocalContext.current
        val connectivityManager = getSystemService(context, ConnectivityManager::class.java)
        val network = connectivityManager?.activeNetwork
        val networkCapabilities = connectivityManager?.getNetworkCapabilities(network)

        if (networkCapabilities != null) {
            internetQuality = getInternetQuality(networkCapabilities)
        }
        LoadingProgress(progress = progress, internetQuality = internetQuality)
    }
}

@Composable
fun LoadingProgress(progress: Int, internetQuality: InternetQuality) {
    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp),
                color = MaterialTheme.colorScheme.primary,
                progress = (progress / 100f).coerceIn(0f, 1f)
            )
            Text(text = "Loading... $progress%")
            Text(text = "Internet Quality: $internetQuality")
            if (internetQuality == InternetQuality.POOR) {
                Text(text = "Internet quality is poor, you may experience slow loading")
            }
        }
    }
}

enum class InternetQuality {
    UNKNOWN, POOR, MODERATE, GOOD
}

@RequiresApi(Build.VERSION_CODES.Q)
fun getInternetQuality(networkCapabilities: NetworkCapabilities): InternetQuality {
    return when {
        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
            when (networkCapabilities.signalStrength) {
                in 0..50 -> InternetQuality.GOOD
                in 51..100 -> InternetQuality.MODERATE
                else -> InternetQuality.POOR
            }
        }

        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
            when (networkCapabilities.signalStrength) {
                in -50..0 -> InternetQuality.GOOD
                in -60..-51 -> InternetQuality.MODERATE
                else -> InternetQuality.POOR
            }
        }

        else -> InternetQuality.UNKNOWN
    }
}