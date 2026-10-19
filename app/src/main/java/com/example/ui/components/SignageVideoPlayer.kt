package com.example.ui.components

import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import java.io.File

@OptIn(UnstableApi::class)
@Composable
fun SignageVideoPlayer(
    videoSource: String,
    modifier: Modifier = Modifier,
    isMuted: Boolean = false,
    repeatMode: Int = Player.REPEAT_MODE_ALL
) {
    val context = LocalContext.current

    val parsedUri = remember(videoSource) {
        when {
            videoSource.startsWith("/videos/") -> {
                val filename = videoSource.removePrefix("/videos/")
                val primaryFile = File(context.filesDir, "media/videos/$filename")
                val fallbackFile = File(context.filesDir, "signage_videos/$filename")
                when {
                    primaryFile.exists() -> Uri.fromFile(primaryFile)
                    fallbackFile.exists() -> Uri.fromFile(fallbackFile)
                    else -> Uri.parse("http://127.0.0.1:8080$videoSource")
                }
            }
            videoSource.startsWith("/") -> {
                val f = File(videoSource)
                if (f.exists()) Uri.fromFile(f) else Uri.parse("http://127.0.0.1:8080$videoSource")
            }
            else -> {
                Uri.parse(videoSource)
            }
        }
    }

    val exoPlayer = remember(parsedUri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(parsedUri))
            this.repeatMode = repeatMode
            volume = if (isMuted) 0f else 1f
            playWhenReady = true
            prepare()
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.stop()
            exoPlayer.release()
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        update = { playerView ->
            if (playerView.player != exoPlayer) {
                playerView.player = exoPlayer
            }
        }
    )
}
