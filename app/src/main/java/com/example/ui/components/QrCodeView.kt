package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.util.QrCodeGenerator

@Composable
fun QrCodeView(
    data: String,
    size: Dp = 140.dp,
    darkColor: Color = Color(0xFF0F172A),
    lightColor: Color = Color(0xFFFFFFFF),
    modifier: Modifier = Modifier
) {
    val qrMatrix = remember(data) {
        try {
            QrCodeGenerator.encode(data)
        } catch (e: Exception) {
            null
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(lightColor)
            .border(2.dp, lightColor.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        if (qrMatrix != null) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val moduleCount = qrMatrix.size
                val cellSize = this.size.width / moduleCount

                for (r in 0 until moduleCount) {
                    for (c in 0 until moduleCount) {
                        if (qrMatrix.isDark(r, c)) {
                            drawRect(
                                color = darkColor,
                                topLeft = Offset(c * cellSize, r * cellSize),
                                size = Size(cellSize + 0.5f, cellSize + 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}
