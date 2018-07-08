package com.sun.mysun.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.maps.model.LatLng
import com.sun.mysun.api.SunriseSunsetApi
import com.sun.mysun.dto.SunriseSunset
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SunInfoPresenter @Inject constructor(private val context: Context,
                                           private val locationClient: FusedLocationProviderClient,
                                           private val api: SunriseSunsetApi)
    : BasePresenter<SunInfoPresenter.View>() {

    companion object {
        private const val SUNRISE_SUNSET_KEY = "sunrise_sunset_key"
    }

    private var sunriseSunset: SunriseSunset? = null

    fun placeSelectionListener(): PlaceSelectionListener {
        return object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                requestSunsetSunriseInfo(place.latLng)
                Timber.i("Place: $place")
            }

            override fun onError(status: Status) {
                applyToView { it.showMessage("Failed to retrieve a place. Please try later.") }
                Timber.e("An error occurred: $status")
            }
        }
    }

    fun checkCurrentLocation() {
        if (hasLocationPermission()) {
            requestCurrentLocation()
        } else {
            applyToView { it.requestPermissions() }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestCurrentLocation() {
        locationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        Timber.i(location.toString())
                        requestSunsetSunriseInfo(LatLng(location.latitude, location.longitude))
                    } ?: applyToView { it.showMessage("Failed to retrieve current location.") }
                }
    }

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestSunsetSunriseInfo(latLng: LatLng) {
        api.requestSunriseSunset(latLng.latitude.toString(), latLng.longitude.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { applyToView { it.updateUi(ViewState.LOADING) } }
                .doOnError { applyToView { it.updateUi(ViewState.EMPTY) } }
                .subscribe({
                    showInfo(it, true)
                    Timber.i(it.toString())
                }, {
                    applyToView {
                        it.showMessage("Failed to retrieve sunrise/sunset info from server")
                    }
                    Timber.e(it, "Failed to retrieve sunrise/sunset info from server")
                })
    }

    private fun showInfo(sunriseSunset: SunriseSunset, withAnimation: Boolean) {
        this.sunriseSunset = sunriseSunset
        postToView { it.updateUi(ViewState.INFO) }
        with(sunriseSunset.results) {
            Flowable.just(retrieveFieldsAsList())
                    .flatMap { Flowable.fromIterable(it) }
                    .concatMap { pair ->
                        val flowable = Flowable.just(pair)
                        if (withAnimation) {
                            flowable.delay(200, TimeUnit.MILLISECONDS)
                        } else {
                            flowable
                        }
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { pair -> postToView { it.addInfoRow(pair.first, pair.second) } }
        }
    }

    fun handleRequestPermissionsResult(grantResults: IntArray) {
        if (grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestCurrentLocation()
            } else {
                postToView { it.showLocationPermissionDialog() }
            }
        }
    }

    fun onSaveInstanceState(outState: Bundle) {
        sunriseSunset?.let {
            outState.putParcelable(SUNRISE_SUNSET_KEY, sunriseSunset)
        }
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            showInfo(savedInstanceState.getParcelable(SUNRISE_SUNSET_KEY), false)
        }
    }

    enum class ViewState {
        EMPTY,
        LOADING,
        INFO
    }

    interface View : PresentableView {
        fun requestPermissions()
        fun updateUi(state: ViewState)
        fun addInfoRow(title: String, value: String)
        fun showMessage(message: String)
        fun showLocationPermissionDialog()
    }
}
