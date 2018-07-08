package com.sun.mysun.di

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sun.mysun.api.SunriseSunsetApi
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.text.DateFormat
import java.util.concurrent.TimeUnit


@Module(includes = [(ContextModule::class)])
class NetworkModule {

    @ActivityScope
    @Provides
    fun sunriseSunsetApi(retrofit: Retrofit) =
            retrofit.create<SunriseSunsetApi>(SunriseSunsetApi::class.java)

    @ActivityScope
    @Provides
    fun retrofit(okHttpClient: OkHttpClient, gson: Gson) =
            Retrofit.Builder()
                    .baseUrl("https://api.sunrise-sunset.org/")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()!!

    @ActivityScope
    @Provides
    fun gson() = GsonBuilder()
            .enableComplexMapKeySerialization()
            .serializeNulls()
            .setDateFormat(DateFormat.LONG)
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .setPrettyPrinting()
            .create()

    @ActivityScope
    @Provides
    fun okHttpClient(cache: Cache, httpLoggingInterceptor: HttpLoggingInterceptor) =
            OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .addInterceptor(httpLoggingInterceptor)
                    .cache(cache)
                    .build()!!

    @ActivityScope
    @Provides
    fun cacheFile(context: Context) = File(context.cacheDir, "okhttp_cache")

    @ActivityScope
    @Provides
    fun cache(cacheFile: File): Cache = Cache(cacheFile, 64 * 1024 * 1024)

    @ActivityScope
    @Provides
    fun httpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }
}