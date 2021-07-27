package com.rasel.flickergallery.di

import com.rasel.flickergallery.data.api.FlickerApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.net.CookieManager
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Singleton
    @Provides
    fun provideFlickerApiService(@AuthInterceptorOkHttpClient client: OkHttpClient) : FlickerApiService {
        return FlickerApiService.create(client)
    }

    @Provides
    @AuthInterceptorOkHttpClient
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient
            .Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .addInterceptor {
                it.proceed(it.request().newBuilder().build())
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .cookieJar(JavaNetCookieJar(CookieManager()))
            .build()
    }
}