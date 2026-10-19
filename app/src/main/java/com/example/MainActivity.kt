package com.example

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.screens.SignageScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.SignageViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: SignageViewModel by viewModels()
    private val rfidBuffer = StringBuilder()
    private var lastKeyTimestamp = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Keep TV screen awake for continuous Digital Signage display
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(dynamicColor = false) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SignageScreen(viewModel = viewModel)
                }
            }
        }
    }

    /**
     * Intercept keystrokes from USB RFID Card Readers (Keyboard Wedge emulators).
     * Automatically captures UID characters and fires attendance on ENTER keycode.
     */
    override fun dispatchKeyEvent(event: android.view.KeyEvent): Boolean {
        if (event.action == android.view.KeyEvent.ACTION_DOWN) {
            val now = System.currentTimeMillis()
            if (now - lastKeyTimestamp > 1500L) {
                rfidBuffer.setLength(0)
            }
            lastKeyTimestamp = now

            if (event.keyCode == android.view.KeyEvent.KEYCODE_ENTER ||
                event.keyCode == android.view.KeyEvent.KEYCODE_NUMPAD_ENTER
            ) {
                val code = rfidBuffer.toString().trim()
                rfidBuffer.setLength(0)
                if (code.length >= 3) {
                    viewModel.onRfidScanned(code)
                    return true
                }
            } else {
                val unicode = event.unicodeChar
                if (unicode in 32..126) {
                    rfidBuffer.append(unicode.toChar())
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }
}

