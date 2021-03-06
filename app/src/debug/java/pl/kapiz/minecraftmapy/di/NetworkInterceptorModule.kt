/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-13.
 */

package pl.kapiz.minecraftmapy.di

import com.facebook.stetho.okhttp3.StethoInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
internal class NetworkInterceptorModule {

    @Singleton
    @Provides
    fun provideNetworkInterceptors(): NetworkInterceptors {
        return NetworkInterceptors(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            },
            StethoInterceptor()
        )
    }
}
