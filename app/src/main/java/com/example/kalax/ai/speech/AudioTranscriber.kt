package com.example.kalax.ai.speech

import android.net.Uri

interface AudioTranscriber {
    suspend fun transcribeAudio(audioUri: Uri): String
}

class LocalAudioTranscriber : AudioTranscriber {
    override suspend fun transcribeAudio(audioUri: Uri): String {
        // Mock implementation for Speech-to-Text
        // Could integrate Android's SpeechRecognizer or a lightweight local Whisper model
        return "This is a beautiful handcrafted terracotta pot."
    }
}
