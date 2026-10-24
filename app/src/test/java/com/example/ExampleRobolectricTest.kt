package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.ClockStyle
import com.example.model.ScreenState
import com.example.viewmodel.LauncherViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("iOS Nova", appName)
    }

    @Test
    fun `launcher view model initializes with lock screen and available presets`() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = LauncherViewModel(application)

        assertEquals(ScreenState.LOCK_SCREEN, viewModel.screenState.value)
        assertEquals(ClockStyle.CLASSIC_BOLD, viewModel.clockStyle.value)
        assertTrue(viewModel.availableWallpapers.isNotEmpty())
        assertTrue(viewModel.dockApps.value.isNotEmpty())

        viewModel.unlockToHome()
        assertEquals(ScreenState.HOME_SCREEN, viewModel.screenState.value)

        viewModel.toggleControlCenter()
        assertTrue(viewModel.isControlCenterOpen.value)

        viewModel.closeControlCenter()
        assertEquals(false, viewModel.isControlCenterOpen.value)
    }
}
