package com.example.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.SignageAdminModal
import com.example.ui.components.SignageAttendanceCornerOverlay
import com.example.ui.components.SignageFlashAlertOverlay
import com.example.ui.components.SignageHeader
import com.example.ui.components.SignageMarqueeTicker
import com.example.ui.components.SignageNoticeWidget
import com.example.ui.components.SignageQuickInfoWidget
import com.example.ui.components.SignageSlideCarousel
import com.example.ui.theme.SignageThemePalettes
import com.example.ui.viewmodel.SignageViewModel

@Composable
fun SignageScreen(
    viewModel: SignageViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val attendanceResult by viewModel.attendancePopupResult.collectAsStateWithLifecycle()
    val palette = SignageThemePalettes.getPalette(uiState.config.themeId)

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(palette.bgGradient),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = palette.primaryColor)
        }
        return
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.bgGradient)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Top Header Bar
            SignageHeader(
                config = uiState.config,
                palette = palette,
                serverUrl = uiState.serverUrl,
                onOpenAdmin = { viewModel.openAdminModal() }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Main Body Content (Landscape TV Grid or Portrait Flow)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                if (isLandscape) {
                    // Landscape TV / Monitor Standard Grid
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Left: Hero Carousel Slideshow (70% width for perfect 16:9 proportion)
                        Box(
                            modifier = Modifier
                                .weight(0.70f)
                                .fillMaxHeight()
                        ) {
                            SignageSlideCarousel(
                                slides = uiState.slides,
                                palette = palette,
                                defaultIntervalSeconds = uiState.config.slideIntervalSeconds,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Right: Side Info Widgets (30% width)
                        Column(
                            modifier = Modifier
                                .weight(0.30f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (uiState.config.enableQuickInfo) {
                                SignageQuickInfoWidget(
                                    config = uiState.config,
                                    palette = palette,
                                    agendasCount = uiState.agendas.size,
                                    noticesCount = uiState.notices.size,
                                    slidesCount = uiState.slides.size
                                )
                            }

                            if (uiState.config.enableNotices) {
                                SignageNoticeWidget(
                                    notices = uiState.notices,
                                    palette = palette,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                } else {
                    // Portrait Mode (Mobile / Vertical Signage Kiosk)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                        ) {
                            SignageSlideCarousel(
                                slides = uiState.slides,
                                palette = palette,
                                defaultIntervalSeconds = uiState.config.slideIntervalSeconds,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        if (uiState.config.enableQuickInfo) {
                            SignageQuickInfoWidget(
                                config = uiState.config,
                                palette = palette,
                                agendasCount = uiState.agendas.size,
                                noticesCount = uiState.notices.size,
                                slidesCount = uiState.slides.size
                            )
                        }

                        if (uiState.config.enableNotices) {
                            Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                                SignageNoticeWidget(
                                    notices = uiState.notices,
                                    palette = palette,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Bottom Marquee Running Text Ticker
            SignageMarqueeTicker(
                tickers = uiState.tickers,
                palette = palette,
                serverUrl = uiState.serverUrl,
                speedFactor = uiState.config.runningTextSpeed
            )
        }

        // Overlay 1: Flash Emergency Announcement Alert
        if (uiState.config.activeFlashAlert.isNotBlank()) {
            SignageFlashAlertOverlay(
                message = uiState.config.activeFlashAlert,
                onDismiss = { viewModel.dismissFlashAlert() }
            )
        }

        // Overlay 2: In-App Admin & Phone Connection QR Modal
        if (uiState.isAdminModalOpen) {
            SignageAdminModal(
                config = uiState.config,
                palette = palette,
                serverUrl = uiState.serverUrl,
                slides = uiState.slides,
                agendas = uiState.agendas,
                notices = uiState.notices,
                tickers = uiState.tickers,
                onDismiss = { viewModel.closeAdminModal() },
                onApplyPreset = { presetType -> viewModel.applyPreset(presetType) },
                onUpdateTheme = { themeId -> viewModel.updateTheme(themeId) },
                onSaveConfig = { newConfig -> viewModel.saveConfig(newConfig) },
                onAddSlide = { slide -> viewModel.addSlide(slide) },
                onUpdateSlide = { slide -> viewModel.updateSlide(slide) },
                onDeleteSlide = { id -> viewModel.deleteSlide(id) },
                onAddAgenda = { agenda -> viewModel.addAgenda(agenda) },
                onUpdateAgenda = { agenda -> viewModel.updateAgenda(agenda) },
                onDeleteAgenda = { id -> viewModel.deleteAgenda(id) },
                onAddNotice = { notice -> viewModel.addNotice(notice) },
                onUpdateNotice = { notice -> viewModel.updateNotice(notice) },
                onDeleteNotice = { id -> viewModel.deleteNotice(id) },
                onAddTicker = { ticker -> viewModel.addTicker(ticker) },
                onUpdateTicker = { ticker -> viewModel.updateTicker(ticker) },
                onDeleteTicker = { id -> viewModel.deleteTicker(id) },
                onSendFlashAlert = { msg -> viewModel.setFlashAlert(msg) },
                onDismissFlashAlert = { viewModel.dismissFlashAlert() }
            )
        }

        // Overlay 3: Corner Floating RFID Attendance Notification Card
        SignageAttendanceCornerOverlay(
            result = attendanceResult,
            onDismiss = { viewModel.dismissAttendancePopup() }
        )
    }
}

