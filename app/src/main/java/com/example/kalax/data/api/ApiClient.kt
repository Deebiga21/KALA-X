package com.example.kalax.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // 10.0.2.2 is the special alias for the emulator.
    // For physical device testing on Wi-Fi, using the actual machine IP:
    private const val BASE_URL = "http://192.168.31.59:8000/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: KalaApi by lazy {
        retrofit.create(KalaApi::class.java)
    }
}
