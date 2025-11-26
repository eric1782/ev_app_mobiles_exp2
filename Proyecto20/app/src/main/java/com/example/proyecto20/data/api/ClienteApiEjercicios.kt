package com.example.proyecto20.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ClienteApiEjercicios {
    
    // Base URL de wger API (gratuita, código abierto, soporte multiidioma incluyendo español)
    private const val URL_BASE = "https://wger.de/api/v2/"
    
    private val interceptorLogging = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {
            android.util.Log.d("OkHttp", message)
        }
    }).apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val clienteOkHttp = OkHttpClient.Builder()
        .addInterceptor(interceptorLogging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(URL_BASE)
        .client(clienteOkHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val servicioApiEjercicios: ServicioApiEjercicios = retrofit.create(ServicioApiEjercicios::class.java)
}

