package com.example.kalax.domain.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.kalax.data.api.CorrectionPayload
import com.example.kalax.data.local.KalaXDatabase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.kalax.data.api.KalaApi
import java.io.File

class CorrectionSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val db = KalaXDatabase.getDatabase(applicationContext)
        val catalogDao = db.catalogDao()

        return try {
            // 1. Upload unsynced corrections
            val unsynced = catalogDao.getUnsyncedCorrections()
            if (unsynced.isEmpty()) return Result.success()

            val api = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(KalaApi::class.java)

            val payloads = unsynced.map {
                CorrectionPayload(
                    catalogItemId = it.catalogItemId,
                    originalText = it.originalText,
                    correctedText = it.correctedText,
                    originalPrice = it.originalPrice,
                    correctedPrice = it.correctedPrice,
                    correctionType = it.correctionType
                )
            }

            val response = api.syncCorrections(payloads)
            if (response.success) {
                catalogDao.markCorrectionsAsSynced(unsynced.map { it.id })
            }

            // 2. Check for new LoRA adapter
            try {
                val adapterResponse = api.getLoraAdapter("default_artisan")
                val adapterDir = File(applicationContext.filesDir, "lora")
                adapterDir.mkdirs()
                val adapterFile = File(adapterDir, "adapter_latest.bin")
                adapterFile.outputStream().use { out ->
                    adapterResponse.byteStream().use { it.copyTo(out) }
                }
            } catch (_: Exception) {
                // No adapter available yet, skip
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
