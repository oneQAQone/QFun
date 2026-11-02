package me.yxp.qfun.plugin.view

import org.junit.Assert.assertEquals
import org.junit.Test

class PluginEntryModeTest {
    @Test
    fun missingOrUnknownPreferenceRestoresOriginalFloatingEntry() {
        assertEquals(PluginEntryMode.FLOATING, PluginEntryMode.fromPreference(null))
        assertEquals(PluginEntryMode.FLOATING, PluginEntryMode.fromPreference("obsolete"))
    }

    @Test
    fun everyModeHasAStableDistinctStoredValue() {
        assertEquals(3, PluginEntryMode.entries.map { it.preferenceValue }.toSet().size)
        for (mode in PluginEntryMode.entries) {
            assertEquals(mode, PluginEntryMode.fromPreference(mode.preferenceValue))
        }
    }
}
