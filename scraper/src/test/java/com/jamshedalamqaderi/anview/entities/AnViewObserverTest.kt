package com.jamshedalamqaderi.anview.entities

import kotlin.time.Duration.Companion.milliseconds
import org.junit.Assert.assertEquals
import org.junit.Test

class AnViewObserverTest {

    @Test
    fun `Given duration when period is over then isReadToNotify will true`() {
        val entity = AnViewObserver("TEST", 10_000.milliseconds) {}
        val currentTime = System.currentTimeMillis()
        // initial will return true
        assertEquals(entity.isReadyToNotify(), true)
        assertEquals(entity.isReadyToNotify(currentTime + 9000), false)
        assertEquals(entity.isReadyToNotify(currentTime + 11000), true)
    }
}
