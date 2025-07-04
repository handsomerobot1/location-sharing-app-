package com.example.locationsharingapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.findyourfriend.viewmodel.FirestoreViewModel
import com.example.locationsharingapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.locationsharingapp.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var firestoreViewModel: FirestoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestoreViewModel = ViewModelProvider(this).get(FirestoreViewModel::class.java)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        firestoreViewModel.getAllUsers(this) { userList ->
            for (user in userList) {
                val userLocation = user.location

                val latLng = parseLocation(userLocation)
                if (latLng != null) {
                    // Add marker if location is valid
                    val markerOptions = MarkerOptions().position(latLng).title(user.displayName)
                    googleMap.addMarker(markerOptions)

                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                    googleMap.animateCamera(cameraUpdate)
                } else {
                    Log.w("MapsActivity", "Invalid location for user ${user.displayName}: $userLocation")
                    // Optionally add a default marker
                    /*
                    val defaultLatLng = LatLng(37.4220936, -122.0839)
                    googleMap.addMarker(
                        MarkerOptions().position(defaultLatLng).title("${user.displayName} (default location)")
                    )
                    */
                }
            }
        }
    }

    private fun parseLocation(location: String?): LatLng? {
        if (location.isNullOrBlank()) return null

        if (location.contains("Don't found any location yet", ignoreCase = true) ||
            location.contains("Location not available", ignoreCase = true)
        ) {
            return null
        }

        return try {
            val latLngSplit = location.split(", ")
            if (latLngSplit.size != 2) return null

            val latitude = latLngSplit[0].substringAfter("Lat: ").toDouble()
            val longitude = latLngSplit[1].substringAfter("Long: ").toDouble()

            LatLng(latitude, longitude)
        } catch (e: Exception) {
            Log.e("MapsActivity", "Error parsing location string: $location", e)
            null
        }
    }
}
