package com.sun.mysun.api

import com.sun.mysun.dto.SunriseSunset
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface SunriseSunsetApi {
    @GET("json")
    fun requestSunriseSunset(@Query("lat") latitude: String,
                    @Query("lng") longitude: String): Single<SunriseSunset>
}