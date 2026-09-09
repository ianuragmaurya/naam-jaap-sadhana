package com.am.naamjaap.presentation.focusmode

/**
 * Lets FocusModeScreen "claim" volume-button presses while it's visible,
 * without giving every screen in the app permanent control over hardware keys.
 */
object VolumeKeyHandler {
    private var onVolumeKeyPressed: (() -> Unit)? = null

    fun register(callback: () -> Unit) {
        onVolumeKeyPressed = callback
    }

    fun unregister() {
        onVolumeKeyPressed = null
    }

    fun isActive(): Boolean = onVolumeKeyPressed != null

    fun trigger() {
        onVolumeKeyPressed?.invoke()
    }
}