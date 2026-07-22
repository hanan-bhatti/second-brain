/*
 * Second Brain - A universal capture and personal knowledge archive
 * Copyright (C) 2026 Hanan Bhatti
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.R
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun YouTubeTrailerPlayer(
    trailerUrl: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val videoId = remember(trailerUrl) { extractYouTubeVideoId(trailerUrl) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (videoId != null) {
            var isLoading by remember { mutableStateOf(true) }
            val embedUrl = "https://www.youtube-nocookie.com/embed/$videoId?autoplay=0&rel=0&playsinline=1&modestbranding=1"

            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                mediaPlaybackRequiresUserGesture = false
                                allowFileAccess = true
                                useWideViewPort = true
                                loadWithOverviewMode = true
                                userAgentString = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36"
                            }
                            webChromeClient = WebChromeClient()
                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isLoading = false
                                }
                            }
                            loadUrl(embedUrl)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                if (isLoading) {
                    CircularWavyProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        } else {
            VideoPlayer(
                videoUri = trailerUrl,
                modifier = modifier.clip(RoundedCornerShape(16.dp))
            )
        }

        // Quick Action: Watch directly on YouTube App
        Text(
            text = "Watch on YouTube App ↗",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // ignore
                    }
                }
                .padding(vertical = 4.dp)
        )
    }
}

fun extractYouTubeVideoId(url: String): String? {
    if (url.isBlank()) return null
    val pattern = "(?:youtube\\.com\\/(?:[^\\/]+\\/.+\\/|(?:v|e(?:mbed)?)\\/" +
            "|.*[?&]v=)|youtu\\.be\\/)([^\"&?\\/\\s]{11})"
    val regex = Regex(pattern, RegexOption.IGNORE_CASE)
    val matchResult = regex.find(url)
    return matchResult?.groupValues?.getOrNull(1)
}

data class StreamingProviderConfig(
    val backgroundColor: Color,
    val contentColor: Color,
    val iconResId: Int? = null,
    val label: String
)

@Composable
fun getStreamingProviderConfig(name: String): StreamingProviderConfig {
    val clean = name.lowercase().trim()
    return when {
        clean.contains("netflix") -> StreamingProviderConfig(StreamingNetflix, Color.White, R.drawable.ic_provider_netflix, "Netflix")
        clean.contains("prime") || clean.contains("amazon") -> StreamingProviderConfig(StreamingPrime, Color.White, R.drawable.ic_provider_prime, "Prime Video")
        clean.contains("disney") -> StreamingProviderConfig(StreamingDisney, Color.White, R.drawable.ic_provider_disney, "Disney+")
        clean.contains("crunchyroll") -> StreamingProviderConfig(StreamingCrunchyroll, Color.White, R.drawable.ic_provider_crunchyroll, "Crunchyroll")
        clean.contains("hulu") -> StreamingProviderConfig(StreamingHulu, StreamingHuluDark, R.drawable.ic_provider_hulu, "Hulu")
        clean.contains("apple") || clean.contains("itunes") -> StreamingProviderConfig(StreamingApple, Color.White, R.drawable.ic_provider_apple, "Apple TV+")
        clean.contains("hbo") || clean.contains("max") -> StreamingProviderConfig(StreamingMax, Color.White, R.drawable.ic_provider_hbo, "Max")
        clean.contains("paramount") -> StreamingProviderConfig(StreamingParamount, Color.White, null, "Paramount+")
        clean.contains("peacock") -> StreamingProviderConfig(StreamingPeacock, Color.White, null, "Peacock")
        clean.contains("youtube") -> StreamingProviderConfig(StreamingYouTube, Color.White, R.drawable.ic_provider_youtube, "YouTube")
        clean.contains("sony") || clean.contains("liv") -> StreamingProviderConfig(StreamingSony, Color.White, null, "Sony LIV")
        clean.contains("zee5") -> StreamingProviderConfig(StreamingZee5, Color.White, null, "ZEE5")
        clean.contains("hotstar") -> StreamingProviderConfig(StreamingHotstar, Color.White, null, "Hotstar")
        clean.contains("jio") -> StreamingProviderConfig(StreamingJio, Color.White, null, "JioCinema")
        clean.contains("funimation") -> StreamingProviderConfig(StreamingFunimation, Color.White, null, "Funimation")
        clean.contains("vudu") || clean.contains("fandango") -> StreamingProviderConfig(StreamingVudu, Color.White, null, "Vudu")
        clean.contains("google play") -> StreamingProviderConfig(StreamingGooglePlay, Color.White, null, "Google Play")
        clean.contains("tubi") -> StreamingProviderConfig(StreamingTubi, Color.White, null, "Tubi")
        clean.contains("pluto") -> StreamingProviderConfig(StreamingPluto, Color.Black, null, "Pluto TV")
        clean.contains("starz") -> StreamingProviderConfig(StreamingStarz, Color.White, null, "STARZ")
        clean.contains("showtime") -> StreamingProviderConfig(StreamingShowtime, Color.White, null, "Showtime")
        clean.contains("iplayer") || clean.contains("bbc") -> StreamingProviderConfig(StreamingBbc, Color.White, null, "BBC iPlayer")
        clean.contains("hidive") -> StreamingProviderConfig(StreamingHidive, Color.White, null, "HIDIVE")
        else -> StreamingProviderConfig(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, null, name)
    }
}
