package com.sun.mysun.dto

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class SunriseSunset (
    @SerializedName("results")
    @Expose
    val results: Results,
    @SerializedName("status")
    @Expose
    val status: String
): Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Results (
    @SerializedName("sunrise")
    @Expose
    val sunrise: String,
    @SerializedName("sunset")
    @Expose
    val sunset: String,
    @SerializedName("solar_noon")
    @Expose
    val solarNoon: String,
    @SerializedName("day_length")
    @Expose
    val dayLength: String,
    @SerializedName("civil_twilight_begin")
    @Expose
    val civilTwilightBegin: String,
    @SerializedName("civil_twilight_end")
    @Expose
    val civilTwilightEnd: String,
    @SerializedName("nautical_twilight_begin")
    @Expose
    val nauticalTwilightBegin: String,
    @SerializedName("nautical_twilight_end")
    @Expose
    val nauticalTwilightEnd: String,
    @SerializedName("astronomical_twilight_begin")
    @Expose
    val astronomicalTwilightBegin: String,
    @SerializedName("astronomical_twilight_end")
    @Expose
    val astronomicalTwilightEnd: String
): Parcelable {
    fun retrieveFieldsAsList(): List<Pair<String, String>> =
            listOf(
                    Pair("Sunrise", sunrise),
                    Pair("Sunset", sunset),
                    Pair("Solar Noon", solarNoon),
                    Pair("Day Length", dayLength),
                    Pair("Civil Twilight Begin", civilTwilightBegin),
                    Pair("Civil TwilightEnd", civilTwilightEnd),
                    Pair("Nautical Twilight Begin", nauticalTwilightBegin),
                    Pair("Nautical Twilight End", nauticalTwilightEnd),
                    Pair("Astronomical Twilight Begin", astronomicalTwilightBegin),
                    Pair("Astronomical Twilight End", astronomicalTwilightEnd)
            )
}