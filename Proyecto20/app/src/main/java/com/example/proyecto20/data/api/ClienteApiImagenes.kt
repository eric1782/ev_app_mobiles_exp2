package com.example.proyecto20.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente para la API de Pexels (imágenes gratuitas)
 * 
 * API gratuita: https://www.pexels.com/api/
 * - 200 requests por hora (gratis)
 * - No requiere atribución obligatoria (recomendable)
 * - Muchas imágenes de ejercicios y fitness
 * 
 * Para obtener una API key:
 * 1. Ve a https://www.pexels.com/api/
 * 2. Regístrate (gratis)
 * 3. Obtén tu API key
 */
object ClienteApiImagenes {
    
    // Base URL de Pexels API
    private const val URL_BASE = "https://api.pexels.com/v1/"
    
    // API key de Pexels
    private const val API_KEY = "xmwcqjEgjZMOyKnKTMfUucpNJykyqidKdI1rWHcZkA8RzlSGRxd6GY3Y"
    
    private val interceptorLogging = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {
            android.util.Log.d("PexelsApi", message)
        }
    }).apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val interceptorAutenticacion = okhttp3.Interceptor { chain ->
        val requestOriginal = chain.request()
        val request = requestOriginal.newBuilder()
            .header("Authorization", API_KEY)
            .build()
        chain.proceed(request)
    }
    
    private val clienteOkHttp = OkHttpClient.Builder()
        .addInterceptor(interceptorAutenticacion)
        .addInterceptor(interceptorLogging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(URL_BASE)
        .client(clienteOkHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val servicioApiImagenes: ServicioApiImagenes = retrofit.create(ServicioApiImagenes::class.java)
}

