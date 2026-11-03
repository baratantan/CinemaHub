package com.cinemahub.app.presentation.screens.player

import org.junit.Assert.assertEquals
import org.junit.Test

class VideoPlayerScreenTest {

    @Test
    fun formatTime_formatsMinutesAndSeconds() {
        assertEquals("01:05", formatTime(65_000L))
    }

    @Test
    fun formatTime_formatsHoursMinutesAndSeconds() {
        assertEquals("01:01:01", formatTime(3_661_000L))
    }

    @Test
    fun formatTime_clampsNegativeValuesToZero() {
        assertEquals("00:00", formatTime(-1L))
    }
}
