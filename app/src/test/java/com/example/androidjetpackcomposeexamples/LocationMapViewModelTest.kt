package com.example.androidjetpackcomposeexamples

import com.example.androidjetpackcomposeexamples.data.LocationFix
import com.example.androidjetpackcomposeexamples.data.LocationProvider
import com.example.androidjetpackcomposeexamples.viewmodel.LocationMapViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LocationMapViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    @Before fun setup() { Dispatchers.setMain(dispatcher) }
    @After fun teardown() { Dispatchers.resetMain() }

    @Test fun locationIsRequestedOnlyOnActionAndCanBeClearedOnRevocation() = runTest(dispatcher) {
        var calls = 0
        val fix = LocationFix(37.0, -122.0, 25f)
        val model = LocationMapViewModel(LocationProvider { calls++; fix })
        advanceUntilIdle()
        assertEquals(0, calls)
        model.locate()
        model.locate()
        advanceUntilIdle()
        assertEquals(1, calls)
        assertEquals(fix, model.state.value.location)
        assertFalse(model.state.value.loading)
        model.permissionRevoked()
        assertNull(model.state.value.location)
    }

    @Test fun nullFixAndPermissionFailureHaveRetryableStates() = runTest(dispatcher) {
        var fail = false
        val model = LocationMapViewModel(LocationProvider {
            if (fail) throw SecurityException()
            null
        })
        model.locate()
        advanceUntilIdle()
        assertTrue(model.state.value.error!!.contains("No location fix"))
        fail = true
        model.locate()
        advanceUntilIdle()
        assertTrue(model.state.value.error!!.contains("Allow location access"))
        assertFalse(model.state.value.loading)
    }

    @Test fun leavingScreenCancelsPendingLocationAndAllowsRetry() = runTest(dispatcher) {
        var cancelled = false
        var calls = 0
        val fix = LocationFix(1.0, 2.0, 100f)
        val model = LocationMapViewModel(LocationProvider {
            calls++
            if (calls == 1) {
                try { awaitCancellation() } finally { cancelled = true }
            }
            fix
        })
        model.locate()
        runCurrent()
        model.stop()
        model.locate()
        advanceUntilIdle()
        assertTrue(cancelled)
        assertEquals(fix, model.state.value.location)
        assertFalse(model.state.value.loading)
    }
}
