package com.example.locationsharingapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        firestoreViewModel.getAllUsers(this) {
            for (user in it) {
                val userLocation = user.location
                val latLng = parseLocation(userLocation)
                if (userLocation.isEmpty() || userLocation == "Don't found any location yet" || userLocation == "Location not available") {
                    LatLng(37.4220936, -122.0839) // Default location
                } else {
                    val markerOptions = MarkerOptions().position(latLng).title(user.displayName)
                    googleMap.addMarker(markerOptions)
                }

                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                googleMap.animateCamera(cameraUpdate)


            }


        }
    }

    private fun parseLocation(location: String): LatLng {
        val latLngSplit = location.split(", ")
        val latitude = latLngSplit[0].substringAfter("Lat: ").toDouble()
        val longitude = latLngSplit[1].substringAfter("Long: ").toDouble()
        return LatLng(latitude, longitude)

    }
}