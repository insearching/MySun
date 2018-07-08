package com.sun.mysun.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.sun.mysun.R
import com.sun.mysun.di.ContextModule
import com.sun.mysun.di.DaggerActivityComponent
import javax.inject.Inject

class SunInfoActivity : FragmentActivity(), SunInfoPresenter.View {

    @Inject
    lateinit var presenter: SunInfoPresenter

    private lateinit var content: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var infoView: TextView

    companion object {
        private const val LOCATION_PERMISSION_CHECK: Int = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependencies()
        setContentView(R.layout.activity_sun_info)

        val autocompleteFragment = fragmentManager.findFragmentById(R.id.place_autocomplete_fragment)
                as PlaceAutocompleteFragment
        autocompleteFragment.setOnPlaceSelectedListener(presenter.placeSelectionListener())
        findViewById<ImageView>(R.id.current_location)
                .setOnClickListener { presenter.checkCurrentLocation() }
        content = findViewById(R.id.content)
        progressBar = findViewById(R.id.progress)
        infoView = findViewById(R.id.info_view)
        val poweredByTv = findViewById<TextView>(R.id.powered_by)
        poweredByTv.text = Html.fromHtml("Powered by <a href=https://sunrise-sunset.org/api> " +
                        "Sunset and sunrise times API</a>")
        poweredByTv.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onResume() {
        super.onResume()
        presenter.bind(this)
    }

    override fun onPause() {
        super.onPause()
        presenter.unbind()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        presenter.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        presenter.onRestoreInstanceState(savedInstanceState)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CHECK) {
            presenter.handleRequestPermissionsResult(grantResults)
        }
    }

    @SuppressLint("InflateParams")
    override fun addInfoRow(title: String, value: String) {
        val row = LayoutInflater.from(this)
                .inflate(R.layout.content_row, null)
        row.findViewById<TextView>(R.id.title).text = title
        row.findViewById<TextView>(R.id.value).text = value
        content.addView(row)
    }

    override fun requestPermissions() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CHECK)
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun updateUi(state: SunInfoPresenter.ViewState) {
        content.removeAllViews()
        when (state) {
            SunInfoPresenter.ViewState.EMPTY -> {
                content.visibility = View.GONE
                progressBar.visibility = View.GONE
                infoView.visibility = View.VISIBLE
            }
            SunInfoPresenter.ViewState.LOADING -> {
                infoView.visibility = View.GONE
                content.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            }
            SunInfoPresenter.ViewState.INFO -> {
                infoView.visibility = View.GONE
                progressBar.visibility = View.GONE
                content.visibility = View.VISIBLE
            }
        }
    }

    override fun showLocationPermissionDialog() {
        AlertDialog.Builder(this)
                .setTitle(R.string.permission_required_title)
                .setMessage(R.string.permission_required_message)
                .setPositiveButton(android.R.string.ok) { _, _ -> presenter.checkCurrentLocation() }
                .create().show()
    }

    private fun injectDependencies() {
        DaggerActivityComponent.builder()
                .contextModule(ContextModule(this))
                .build().inject(this)
    }
}
