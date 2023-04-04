package com.dev.weatherapp.view

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dev.weatherapp.R
import com.dev.weatherapp.const.Globals
import com.dev.weatherapp.databinding.ActivityMainBinding
import com.dev.weatherapp.model.WeatherResponse
import com.dev.weatherapp.viewmodel.CustomViewModelFactory
import com.dev.weatherapp.viewmodel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2


    private lateinit var mViewModel: WeatherViewModel
    lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main)

        mViewModel =
            ViewModelProvider(this, CustomViewModelFactory(this)).get(WeatherViewModel::class.java)

        // initialize fused location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.button.setOnClickListener {
            val cityName = binding.etSearchbox.text.toString()
            fetchWeatherDetailsWithCityName(cityName)
            try {
                val imm: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            } catch (e: Exception) {
                // TODO: handle exception
            }

        }

        if (getSavedCityName()?.isNotEmpty() == true) {
            binding.etSearchbox?.setText(getSavedCityName())
            fetchWeatherDetailsWithCityName(getSavedCityName()!!)
        }


        if (locationEnabled()) {
            getLocation()
        }else{
            Toast.makeText(
                this@MainActivity,
                "Please enable GPS to get current Location",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun fetchWeatherDetailsWithCityName(cityName: String) {
        lifecycleScope.launchWhenStarted {
            mViewModel.getWeatherDataWithCityName(cityName, Globals.apiKey)
                .flowOn(
                    Dispatchers.IO
                ).catch {
                    bindData(null)
                }.collectLatest {
                    bindData(it)
                }
        }
    }


    private fun bindData(weatherData: WeatherResponse?) {
        weatherData?.let { it ->
            binding.tvDetails.text =
                "City : ${it.name} \nTemperature : ${it.main?.temp} \nHumidity : ${it.main?.humidity} \n"
            "Pressure : ${it.main?.pressure}"


            Picasso.get()
                .load("https://openweathermap.org/img/wn/${it.weather.get(0).icon}@2x.png")
                .into(binding.imageView);

            saveData(it.name)
        } ?: run {

            binding.tvDetails.text = "Please Enter Valid City Name"

        }
    }

    override fun onResume() {
        super.onResume()
    }


    override fun onDestroy() {
        super.onDestroy()
    }


    fun getCurrentLocationWeatherDetails() {
        lifecycleScope.launchWhenStarted {
            mViewModel.getWeatherData(
                latitude.toString(),
                longitude.toString(),
                "b67af2e321e57e6754d055ee78a99348"
            )
                .flowOn(
                    Dispatchers.IO
                ).catch {
                    bindData(null)
                }.collectLatest {
                    bindData(it)
                }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun saveData(cityName: String?) {
        val myEdit: SharedPreferences.Editor =
            getSharedPreferences("MySharedPref", MODE_PRIVATE).edit()
        myEdit.putString("CityName", cityName)
        myEdit.apply()
    }

    fun getSavedCityName(): String? {
        val prefs = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        return prefs.getString("CityName", "")
    }

    override fun onLocationChanged(location: Location) {

        latitude = location.latitude
        longitude = location.longitude

        Log.d("VASANTHI", "LAT  ${location.latitude}")

        getCurrentLocationWeatherDetails()

    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }

    private fun locationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}
