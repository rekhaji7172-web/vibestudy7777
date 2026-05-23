package com.example.audio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.sin

class FocusSynth {

    private var audioTrack: AudioTrack? = null
    private var synthJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    @Volatile
    private var isPlaying = false
    @Volatile
    private var currentMode = "Binaural" // "Binaural", "WhiteNoise", "Rain", "Silence"
    @Volatile
    private var volumeMultiplier = 0.5f

    fun start(mode: String = "Binaural") {
        if (isPlaying && currentMode == mode) return
        stop()

        currentMode = mode
        isPlaying = true
        synthJob = scope.launch {
            runSynthLoop()
        }
    }

    fun stop() {
        isPlaying = false
        synthJob?.cancel()
        synthJob = null
        try {
            audioTrack?.apply {
                if (playState == AudioTrack.PLAYSTATE_PLAYING) {
                    stop()
                }
                release()
            }
        } catch (e: Exception) {
            Log.e("FocusSynth", "Error releasing AudioTrack", e)
        }
        audioTrack = null
    }

    fun setVolume(vol: Float) {
        volumeMultiplier = vol.coerceIn(0f, 1.0f)
        try {
            audioTrack?.setVolume(volumeMultiplier)
        } catch (e: Exception) {
            // Ignore if track not started
        }
    }

    fun isCurrentlyPlaying(mode: String): Boolean {
        return isPlaying && currentMode == mode
    }

    fun isAnyPlaying(): Boolean = isPlaying

    private fun runSynthLoop() {
        val sampleRate = 22050
        // We use stereo output for Binaural beats so we can play different frequencies in left vs right ears!
        val channelConfig = AudioFormat.CHANNEL_OUT_STEREO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val minBufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        val bufferSize = (minBufferSize * 2).coerceAtLeast(4096)

        try {
            audioTrack = AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize,
                AudioTrack.MODE_STREAM
            )
            audioTrack?.setVolume(volumeMultiplier)
            audioTrack?.play()
        } catch (e: Exception) {
            Log.e("FocusSynth", "Failed to initiate AudioTrack", e)
            isPlaying = false
            return
        }

        val buffer = ShortArray(bufferSize)
        var phaseL = 0.0
        var phaseR = 0.0
        
        // 40Hz Binaural focus: Left channel 200Hz, Right channel 240Hz
        val frequencyL = 200.0
        val frequencyR = 240.0
        
        val samplingIntervalL = 2.0 * Math.PI * frequencyL / sampleRate
        val samplingIntervalR = 2.0 * Math.PI * frequencyR / sampleRate

        val random = Random()
        var lastRainVal = 0.0

        while (isPlaying && synthJob?.isActive == true) {
            val mode = currentMode
            for (i in 0 until bufferSize step 2) {
                when (mode) {
                    "Binaural" -> {
                        // Left Ear Sine Wave
                        val valL = (sin(phaseL) * 32767.0 * 0.4).toInt()
                        phaseL += samplingIntervalL
                        if (phaseL > 2.0 * Math.PI) phaseL -= 2.0 * Math.PI

                        // Right Ear Sine Wave
                        val valR = (sin(phaseR) * 32767.0 * 0.4).toInt()
                        phaseR += samplingIntervalR
                        if (phaseR > 2.0 * Math.PI) phaseR -= 2.0 * Math.PI

                        buffer[i] = valL.toShort()
                        buffer[i + 1] = valR.toShort()
                    }
                    "WhiteNoise" -> {
                        // Completely random signal
                        val noiseValue = (random.nextGaussian() * 32767.0 * 0.15).toInt().coerceIn(-32768, 32767)
                        buffer[i] = noiseValue.toShort()
                        buffer[i + 1] = noiseValue.toShort()
                    }
                    "Rain" -> {
                        // Rain sound: Low frequency rumbling + random high pitch "raindrop" crackles
                        val pinkNoise = (random.nextGaussian() * 0.1) // rumble base
                        
                        // Add some high frequency splatters occasionally
                        var droplet = 0.0
                        if (random.nextFloat() > 0.992) {
                            droplet = (random.nextFloat() - 0.5) * 0.5
                        }
                        
                        // Smooth the base pink noise a little
                        val currentSample = 0.95 * lastRainVal + 0.05 * (pinkNoise + droplet)
                        lastRainVal = currentSample
                        
                        val finalShort = (currentSample * 32767.0 * 1.2).toInt().coerceIn(-32768, 32767)
                        buffer[i] = finalShort.toShort()
                        buffer[i + 1] = finalShort.toShort()
                    }
                    else -> { // Silicon Silence
                        buffer[i] = 0
                        buffer[i + 1] = 0
                    }
                }
            }
            try {
                audioTrack?.write(buffer, 0, bufferSize)
            } catch (e: Exception) {
                Log.e("FocusSynth", "Writing block error", e)
                break
            }
        }
    }
}
