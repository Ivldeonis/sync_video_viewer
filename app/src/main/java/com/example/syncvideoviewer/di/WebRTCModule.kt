package com.example.syncvideoviewer.di

import android.content.Context
import com.example.syncvideoviewer.webrtc.WebRTCManager
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WebRTCModule {

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return Gson()
    }

    @Singleton
    @Provides
    fun provideWebRTCManager(
        @ApplicationContext context: Context,
        gson: Gson
    ): WebRTCManager {
        return WebRTCManager(context, gson)
    }
}
