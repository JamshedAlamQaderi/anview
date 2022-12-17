package com.jamshedalamqaderi.anview.entities

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

class AnViewObserverTest {

    @Test
    fun `Given duration when period is over then isReadToNotify will true`() {
        val entity = AnViewObserver("TEST", 10_000.milliseconds) {}
        // initial will return true
        assertEquals(entity.isReadyToNotify(1000), true)
        assertEquals(entity.isReadyToNotify(10999), false)
        assertEquals(entity.isReadyToNotify(11000), true)
    }
}
