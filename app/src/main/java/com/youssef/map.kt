package com.youssef
import android.Manifest
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.location.Geocoder
import android.location.LocationManager
import android.content.pm.PackageManager
import android.content.Context
import android.location.Address
import android.location.Location
import android.os.Build
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.util.*
class MapActivity : AppCompatActivity()
{
    lateinit var locManager : LocationManager
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val tv = findViewById<TextView>(R.id.locText)
        locManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
            && checkSelfPermission( Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED)
        {
            val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            requestPermissions(permission, 1)
        }
        else
        {
            val gpsloc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            getLocation(tv, gpsloc!!)
        }
    }
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    val tv = findViewById<TextView>(R.id.locText)
                    val gps_loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    getLocation(tv, gps_loc!!)
                }
                else
                {
                    Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun getLocation(tv: TextView, gpsloc: Location)
    {
        val finalLoc = gpsloc
        val latitude = finalLoc.latitude
        val longitude = finalLoc.longitude
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses != null && addresses.isNotEmpty())
        {
            val userCountry = addresses[0].countryName
            val userAddress = addresses[0].getAddressLine(0)
            tv.text = "country: $userCountry,\n\nAddress: $userAddress,\n\nLatitude: $latitude, \n\nLongitude: $longitude "
        }
        else
        {
            tv.text = "Unknown"
        }
    }
}