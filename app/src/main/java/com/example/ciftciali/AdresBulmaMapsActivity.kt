package com.example.ciftciali

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_adres_bulma_maps.*


class AdresBulmaMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val ACTIVITY_NO = 3
    var ref = FirebaseDatabase.getInstance().reference
    var musteriAdi: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adres_bulma_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        musteriAdi = intent.getStringExtra("musteriAdi")

        setupNavigationView()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //     mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            konumKaydi()
        } else {
            konumKaydi()
            Toast.makeText(this, "Ayarlardan uygulamaya konum izni verin", Toast.LENGTH_LONG).show()
            mMap.isMyLocationEnabled = false
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }


        val zonguldak = LatLng(41.450000, 31.795937)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(zonguldak))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zonguldak, 11.8f))

    }

    private fun konumKaydi() {

        tvKaydet.setOnClickListener {
            //  hand.removeCallbacks(mRunnable)
            val latLng: LatLng = mMap.getCameraPosition().target

            ref.child("Musteriler").child(musteriAdi.toString()).child("musteri_zkonum").setValue(true)
            ref.child("Musteriler").child(musteriAdi.toString()).child("musteri_zlat").setValue(latLng.latitude)
            ref.child("Musteriler").child(musteriAdi.toString()).child("musteri_zlong").setValue(latLng.longitude)




            finish()
        }

    }


    fun setupNavigationView() {
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNav)
        BottomNavigationViewHelper.setupNavigation(this, bottomNav) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNav.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }
}