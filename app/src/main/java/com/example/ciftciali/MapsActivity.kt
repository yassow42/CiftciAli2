package com.example.ciftciali;

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Transformations.map
import com.example.ciftciali.Datalar.SiparisData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.dialog_item_siparisler.view.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var bottomSheetDialog: BottomSheetDialog
    lateinit var progressDialog: ProgressDialog
    private val ACTIVITY_NO = 1

    lateinit var mAuth: FirebaseAuth
    lateinit var userID: String
    lateinit var kullaniciAdi: String

    var konum: Boolean = false
    val hndler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        //  this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setupNavigationView()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(" Harita Yükleniyor... Lütfen bekleyin...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        //   konum = sharedPreferences.getBoolean("Konum", false)
        //  swKonum.isChecked = konum

        //Toast.makeText(this, "Bazı Siparişler Adres Bulunamadığından gösterilmeyebilir. Dikkatli Ol.!!!", Toast.LENGTH_LONG).show()

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //     mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            setupKullaniciAdi()
            swKonum.setOnClickListener {
                val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                if (swKonum.isChecked) {
                    editor.putBoolean("Konum", true)
                    editor.apply()
                    mMap.isMyLocationEnabled = true
                    Toast.makeText(this, "Konum açıldı", Toast.LENGTH_SHORT).show()
                } else {
                    editor.putBoolean("Konum", false)
                    editor.apply()
                    mMap.isMyLocationEnabled = false
                    Toast.makeText(this, "Konum kapalı", Toast.LENGTH_SHORT).show()
                }
            }

        } else {
            Toast.makeText(this, "Ayarlardan uygulamaya konum izni verin", Toast.LENGTH_LONG).show()
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        mMap.isMyLocationEnabled = false
        val zonguldak = LatLng(41.450000, 31.795937)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(zonguldak))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zonguldak, 11.8f))
        hndler.postDelayed(Runnable { progressDialog.dismiss() }, 6000)
    }

    fun setupKullaniciAdi() {
        mAuth = FirebaseAuth.getInstance()
        userID = mAuth.currentUser!!.uid
        FirebaseDatabase.getInstance().reference.child("users").child(userID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    kullaniciAdi = p0.child("user_name").value.toString()
                    veriler()
                }
            })
    }

    fun veriler() {
        var gelenData: SiparisData
        val ref = FirebaseDatabase.getInstance().reference
        ref.child("Siparisler").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.hasChildren()) {

                    for (ds in p0.children) {
                        try {
                            gelenData = ds.getValue(SiparisData::class.java)!!

                            if (gelenData.siparis_adres.toString() != "Adres") {

                                if (gelenData.siparis_teslim_tarihi!!.compareTo(System.currentTimeMillis()) == -1) {

                                    var konumVarMi = gelenData.musteri_zkonum.toString().toBoolean()

                                    try {
                                        if (konumVarMi) {
                                            var lat = gelenData.musteri_zlat!!.toDouble()
                                            var long = gelenData.musteri_zlong!!.toDouble()
                                            val adres = LatLng(lat, long)
                                            var myMarker = mMap.addMarker(MarkerOptions().position(adres).title(gelenData.siparis_veren).snippet(gelenData.siparis_adres + " / " + gelenData.siparis_apartman))

                                            //   myMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.order_map))
                                            myMarker.tag = gelenData.siparis_key
                                        } else {
                                            var lat = convertAddressLat(gelenData.siparis_mah + " mahallesi " + gelenData.siparis_adres + " Zonguldak")!!.toDouble()
                                            var long = convertAddressLng(gelenData.siparis_mah + " mahallesi " + gelenData.siparis_adres + " Zonguldak")!!.toDouble()
                                            val adres = LatLng(lat, long)
                                            var myMarker = mMap.addMarker(MarkerOptions().position(adres).title(gelenData.siparis_veren).snippet(gelenData.siparis_adres + " / " + gelenData.siparis_apartman))
                                          //  mMap.addCircle(CircleOptions().center(adres).radius(75.0).strokeWidth(6f).strokeColor(Color.BLUE).fillColor(ContextCompat.getColor(applicationContext, R.color.transpan)))

                                            //   myMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.order_map))
                                            myMarker.tag = gelenData.siparis_key
                                        }
                                    } catch (e: Exception) {
                                        Log.e("hata", "maps activity harita adres yukleme hatası")
                                        ref.child("hatalar/mapsactivity/adreshatasi").setValue(e.message.toString())
                                    }


                                    mMap.setOnMarkerClickListener {
                                        val benimMarker = it
                                        var markerTag = it.tag

                                        var bottomSheetDialog = BottomSheetDialog(this@MapsActivity)

                                        var view = bottomSheetDialog.layoutInflater.inflate(R.layout.dialog_item_siparisler, null)
                                        bottomSheetDialog.setContentView(view)

                                        var ref = FirebaseDatabase.getInstance().reference
                                        ref.child("Siparisler").child(it.tag.toString())
                                            .addListenerForSingleValueEvent(object :
                                                ValueEventListener {
                                                override fun onCancelled(p0: DatabaseError) {
                                                }

                                                override fun onDataChange(p0: DataSnapshot) {
                                                    try {
                                                        var siparisData = p0.getValue(SiparisData::class.java)!!
                                                        view.tvSiparisVeren.text = siparisData!!.siparis_veren
                                                        view.tvSiparisAdres.text = siparisData!!.siparis_mah + " mah. " + siparisData!!.siparis_adres + " " + siparisData!!.siparis_apartman
                                                        view.tvSiparisTel.text = siparisData.siparis_tel
                                                        view.tvNot.text = siparisData.siparis_notu


                                                        dataBosmuKontrol(view, siparisData)

                                                        view.tvSiparisTel.setOnClickListener {
                                                            val arama = Intent(Intent.ACTION_DIAL)//Bu kod satırımız bizi rehbere telefon numarası ile yönlendiri.
                                                            arama.data = Uri.parse("tel:" + siparisData.siparis_tel)
                                                            startActivity(arama)
                                                        }
                                                        view.btnTeslim.setOnClickListener {
                                                            var alert = AlertDialog.Builder(this@MapsActivity)
                                                                .setTitle("Sipariş Teslim Edildi")
                                                                .setMessage("Emin Misin ?")
                                                                .setPositiveButton("Onayla", object : DialogInterface.OnClickListener {
                                                                    override fun onClick(p0: DialogInterface?, p1: Int) {

                                                                        benimMarker.remove()
                                                                        bottomSheetDialog.dismiss()


                                                                        var siparisData = SiparisData(
                                                                            siparisData.siparis_zamani,
                                                                            siparisData.siparis_teslim_zamani,
                                                                            siparisData.siparis_teslim_tarihi,
                                                                            siparisData.siparis_adres,
                                                                            siparisData.siparis_apartman,
                                                                            siparisData.siparis_tel,
                                                                            siparisData.siparis_veren,
                                                                            siparisData.siparis_mah,
                                                                            siparisData.siparis_notu,
                                                                            siparisData.siparis_key,
                                                                            siparisData.yumurta,
                                                                            siparisData.yykasar_400,
                                                                            siparisData.yykasar_600,
                                                                            siparisData.yogurt,
                                                                            siparisData.sut_cokelegi,
                                                                            siparisData.yycokertme_pey,
                                                                            siparisData.yydil_pey,
                                                                            siparisData.yybeyaz_pey,
                                                                            siparisData.yycig_sut,
                                                                            siparisData.yykangal_sucuk,
                                                                            siparisData.sucuk,
                                                                            siparisData.yykavurma,
                                                                            siparisData.musteri_zkonum,
                                                                            siparisData.musteri_zlat,
                                                                            siparisData.musteri_zlong,
                                                                            kullaniciAdi
                                                                        )

                                                                        ref.child("Musteriler").child(siparisData.siparis_veren.toString()).child("siparisleri").child(siparisData.siparis_key.toString())
                                                                            .setValue(siparisData)
                                                                        ref.child("Teslim_siparisler").child(siparisData.siparis_key.toString()).setValue(siparisData).addOnCompleteListener {

                                                                      //      startActivity(Intent(this@MapsActivity, SiparislerActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
                                                                            Toast.makeText(this@MapsActivity, "Sipariş Teslim Edildi", Toast.LENGTH_LONG).show()
                                                                            ref.child("Siparisler").child(siparisData.siparis_key.toString()).removeValue()
                                                                            ref.child("Teslim_siparisler").child(siparisData.siparis_key.toString()).child("siparis_teslim_zamani").setValue(ServerValue.TIMESTAMP)
                                                                            FirebaseDatabase.getInstance().reference.child("Musteriler").child(siparisData.siparis_veren.toString()).child("siparis_son_zaman")
                                                                                .setValue(ServerValue.TIMESTAMP)
                                                                        }
                                                                    }
                                                                }).setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                                                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                                                        p0!!.dismiss()
                                                                    }
                                                                }).create()
                                                            alert.show()
                                                        }
                                                    } catch (e: Exception) {
                                                        ref.child("hatalar/mapsactivity/220satir").setValue(e.message.toString())
                                                    }

                                                }
                                            })

                                        bottomSheetDialog.show()
                                        it.isVisible
                                    }

                                }
                            }
                        } catch (ex: Exception) {
                            Log.e("Harita Veri Hatası", ex.toString())
                        }

                    }
                    progressDialog.dismiss()
                } else {
                    progressDialog.setMessage("Veri Alınamıyor...")
                    hndler.postDelayed(Runnable { progressDialog.dismiss() }, 2000)
                }
            }
        })


    }

    fun dataBosmuKontrol(view: View, siparisData: SiparisData) {

        if (siparisData.yykasar_400 == "0") {
            view.tb400gr.visibility = View.GONE
        }
        if (siparisData.yykasar_600 == "0") {
            view.tb600gr.visibility = View.GONE
        }
        if (siparisData.sucuk == "0") {
            view.tbSucuk.visibility = View.GONE
        }
        if (siparisData.sut_cokelegi == "0") {
            view.tbCokelek.visibility = View.GONE
        }
        if (siparisData.yogurt == "0") {
            view.tbYogurt.visibility = View.GONE
        }
        if (siparisData.yumurta == "0") {
            view.tbYumurta.visibility = View.GONE
        }
        if (siparisData.yybeyaz_pey == "0") {
            view.tbBeyazPeynir.visibility = View.GONE
        }
        if (siparisData.yycig_sut == "0") {
            view.tbCigsut.visibility = View.GONE
        }
        if (siparisData.yycokertme_pey == "0") {
            view.tbCokertme.visibility = View.GONE
        }
        if (siparisData.yydil_pey == "0") {
            view.tbDil.visibility = View.GONE
        }
        if (siparisData.yykangal_sucuk == "0") {
            view.tbKangal.visibility = View.GONE
        }
        if (siparisData.yykavurma == "0") {
            view.tbKavurma.visibility = View.GONE
        }

        if (!siparisData.yykasar_400.isNullOrEmpty()) {
            view.tv3lt.text = siparisData.yykasar_400
        } else {
            hataMesajiYazdir(
                "sut3 yok ${siparisData.siparis_key}",
                siparisData.siparis_veren.toString()
            )
        }

        if (!siparisData.yykasar_600.isNullOrEmpty()) {
            view.tv5lt.text = siparisData.yykasar_600
        } else {
            hataMesajiYazdir(
                "sut5 yok ${siparisData.siparis_key}",
                siparisData.siparis_veren.toString()
            )
        }


        if (!siparisData.yumurta.isNullOrEmpty()) {
            view.tvYumurta.text = siparisData.yumurta

        } else {
            hataMesajiYazdir(
                "yumurta yok ${siparisData.siparis_key}",
                siparisData.siparis_veren.toString()
            )
        }

        if (!siparisData.yogurt.isNullOrEmpty()) {
            view.tvYogurt.text = siparisData.yogurt

        } else {
            hataMesajiYazdir(
                "yogurt yok ${siparisData.siparis_key}",
                siparisData.siparis_veren.toString()
            )
        }

        if (!siparisData.sut_cokelegi.isNullOrEmpty()) {
            view.tvCokelek.text = siparisData.sut_cokelegi

        } else {
            hataMesajiYazdir(
                "Cokelek yok ${siparisData.siparis_key}",
                siparisData.siparis_veren.toString()
            )
        }

        if (!siparisData.yycokertme_pey.isNullOrEmpty()) {
            view.tvCokertme.text = siparisData.yycokertme_pey

        } else {
            hataMesajiYazdir(
                "cokertme yok ${siparisData.siparis_key}",
                siparisData.siparis_veren.toString()
            )
        }
        if (!siparisData.yydil_pey.isNullOrEmpty()) {
            view.tvDil.text = siparisData.yydil_pey

        } else {
            hataMesajiYazdir(
                "dil yok ${siparisData.siparis_key}",
                siparisData.siparis_veren.toString()
            )
        }
        if (!siparisData.yybeyaz_pey.isNullOrEmpty()) {
            view.tvBeyazPeynir.text = siparisData.yybeyaz_pey

        } else {
            hataMesajiYazdir(
                "beyaz yok ${siparisData.siparis_key}",
                siparisData.siparis_veren.toString()
            )
        }
        if (!siparisData.yycig_sut.isNullOrEmpty()) {
            view.tvCigsut.text = siparisData.yycig_sut

        } else {
            hataMesajiYazdir(
                "cig sut yok ${siparisData.siparis_key}",
                siparisData.siparis_veren.toString()
            )
        }

        if (!siparisData.yykangal_sucuk.isNullOrEmpty()) {
            view.tvKangal.text = siparisData.yykangal_sucuk

        } else {
            hataMesajiYazdir(
                "kangal yok ${siparisData.siparis_key}",
                siparisData.siparis_veren.toString()
            )
        }

        if (!siparisData.sucuk.isNullOrEmpty()) {
            view.tvSucuk.text = siparisData.sucuk

        } else {
            hataMesajiYazdir(
                "sucuk yok ${siparisData.siparis_key}",
                siparisData.siparis_veren.toString()
            )
        }
        if (!siparisData.yykavurma.isNullOrEmpty()) {
            view.tvKavurma.text = siparisData.yykavurma
        } else {
            hataMesajiYazdir(
                "kavurma yok ${siparisData.siparis_key}",
                siparisData.siparis_veren.toString()
            )
        }
    }

    fun hataMesajiYazdir(s: String, isim: String) {
        FirebaseDatabase.getInstance().reference.child("Hatalar/SiparisAdapter").push().setValue(s)
        Toast.makeText(this, "Bu Kişinin Siparişi Hatalı Lütfen Sil $isim", Toast.LENGTH_LONG).show()
    }


    fun convertAddressLat(adres: String): Double? {

        try {
            var geoCoder = Geocoder(this)

            val addressList: List<Address> =
                geoCoder.getFromLocationName(adres.toString(), 1)
            if (addressList != null && addressList.size > 0) {
                val lat: Double = addressList[0].getLatitude()
                val lng: Double = addressList[0].getLongitude()



                return lat
            }
        } catch (ex: Exception) {
            Log.e("Harita Verilerini Conve", ex.toString())

        }
        return null
    }

    fun convertAddressLng(adres: String): Double? {

        try {
            var geoCoder = Geocoder(this)

            val addressList: List<Address> =
                geoCoder.getFromLocationName(adres.toString(), 1)
            if (addressList != null && addressList.size > 0) {
                val lat: Double = addressList[0].getLatitude()
                val lng: Double = addressList[0].getLongitude()

                return lng

            }
        } catch (ex: Exception) {
            Log.e("Harita Verilerini Conve", ex.toString())
        }
        return null
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == 1) {
            Toast.makeText(this, "Konum izni tamam", Toast.LENGTH_SHORT).show()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun setupNavigationView() {

        BottomNavigationViewHelper.setupBottomNavigationView(bottomNav)
        BottomNavigationViewHelper.setupNavigation(this, bottomNav) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNav.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }

}
