package com.example.ciftciali

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ciftciali.Adapter.MusteriAdapter
import com.example.ciftciali.Adapter.MusteriSiparisleriAdapter
import com.example.ciftciali.Datalar.MusteriData
import com.example.ciftciali.Datalar.SiparisData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import kotlinx.android.synthetic.main.activity_musteriler.*
import kotlinx.android.synthetic.main.dialog_gidilen_musteri.view.*
import kotlinx.android.synthetic.main.dialog_musteri_ekle.view.*
import kotlinx.android.synthetic.main.dialog_musteri_ekle.view.etApartman
import kotlinx.android.synthetic.main.dialog_musteri_ekle.view.tvAdresTam
import kotlinx.android.synthetic.main.dialog_siparis_ekle.view.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MusterilerActivity : AppCompatActivity() {


    private val ACTIVITY_NO = 3
    var secilenMah: String? = null
    lateinit var musteriAdList: ArrayList<String>
    lateinit var musteriList: ArrayList<MusteriData>
    lateinit var pazartesiList: ArrayList<MusteriData>
    lateinit var saliList: ArrayList<MusteriData>
    lateinit var carsambaList: ArrayList<MusteriData>
    lateinit var persembeList: ArrayList<MusteriData>
    lateinit var cumaList: ArrayList<MusteriData>
    lateinit var cumartesiList: ArrayList<MusteriData>

    lateinit var dialogViewSpArama: View

    lateinit var dialogView: View

    lateinit var progressDialog: ProgressDialog
    val hndler = Handler()
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var userID: String
    var kullaniciAdi: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_musteriler)
        setupNavigationView()
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        mAuth = FirebaseAuth.getInstance()
        userID = mAuth.currentUser!!.uid
        setupKullaniciAdi()

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Yükleniyor...")
        progressDialog.setCancelable(false)
        progressDialog.show()


        hndler.postDelayed(Runnable { setupVeri() }, 500)
        hndler.postDelayed(Runnable { progressDialog.dismiss() }, 5000)

        setupBtn()
        setupBtn2()

        // FirebaseDatabase.getInstance().reference.child("Musteriler").keepSynced(true)
        // window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN


    }

    fun setupKullaniciAdi() {
        FirebaseDatabase.getInstance().reference.child("users").child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                kullaniciAdi = p0.child("user_name").value.toString()
                setupVeri()
            }

        })
    }


    fun setupVeri() {
        musteriAdList = ArrayList()
        musteriList = ArrayList()
        pazartesiList = ArrayList()
        saliList = ArrayList()
        carsambaList = ArrayList()
        persembeList = ArrayList()
        cumaList = ArrayList()
        cumartesiList = ArrayList()

        musteriList.clear()
        pazartesiList.clear()
        saliList.clear()
        carsambaList.clear()
        persembeList.clear()
        cumaList.clear()
        cumartesiList.clear()

        FirebaseDatabase.getInstance().reference.child("Musteriler").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.hasChildren()) {

                    for (ds in p0.children) {
                        try {
                            var gelenData = ds.getValue(MusteriData::class.java)!!
                            var musteriAdlari = gelenData.musteri_ad_soyad
                            musteriList.add(gelenData)
                            musteriAdList.add(musteriAdlari.toString())

                            if (gelenData.musteri_mah == "Fener" || gelenData.musteri_mah == "Meşrutiyet" || gelenData.musteri_mah == "Rüzgarlı" || gelenData.musteri_mah == "Karaelmas") {
                                pazartesiList.add(gelenData)
                                tvPazartesiSayi.text = pazartesiList.size.toString() + " Müşteri"
                            }
                            if (gelenData.musteri_mah == "İnağzı" || gelenData.musteri_mah == "Kilimli" || gelenData.musteri_mah == "Karadon" || gelenData.musteri_mah == "Çatalağzı") {
                                saliList.add(gelenData)
                                tvSaliSayi.text = saliList.size.toString() + " Müşteri"
                            }
                            if (gelenData.musteri_mah == "Tepebaşı" || gelenData.musteri_mah == "Kapuz" || gelenData.musteri_mah == "Yeşil" || gelenData.musteri_mah == "Yeni" || gelenData.musteri_mah == "Bağlık") {
                                carsambaList.add(gelenData)
                                tvCarsambaSayi.text = carsambaList.size.toString() + " Müşteri"
                            }
                            if (gelenData.musteri_mah == "Çaydamar" || gelenData.musteri_mah == "Kokaksu" || gelenData.musteri_mah == "Birlik" || gelenData.musteri_mah == "Müftülük" || gelenData.musteri_mah == "Ontemmuz" || gelenData.musteri_mah == "Soğuksu") {
                                persembeList.add(gelenData)
                                tvPersSayi.text = persembeList.size.toString() + " Müşteri"
                            }

                            if (gelenData.musteri_mah == "Kavaklık" || gelenData.musteri_mah == "Kozlu" || gelenData.musteri_mah == "Tıp") {
                                cumaList.add(gelenData)
                                tvCumaSayi.text = cumaList.size.toString() + " Müşteri"
                            }
                            if (gelenData.musteri_mah == "Site" || gelenData.musteri_mah == "İncivez") {
                                cumartesiList.add(gelenData)
                                tvCumartesiSayi.text = cumartesiList.size.toString() + " Müşteri"
                            }
                        } catch (e: Exception) {
                            FirebaseDatabase.getInstance().reference.child("Hatalar/MusteriData").push().setValue(e.message.toString())
                        }


                    }

                    progressDialog.dismiss()
                    var adapterSearch = ArrayAdapter<String>(this@MusterilerActivity, android.R.layout.simple_expandable_list_item_1, musteriAdList)
                    searchMs.setAdapter(adapterSearch)
                    tvMusteri.text = "Müşteriler " + "(" + (musteriList.size) + ")"

                    //  setupRecyclerViewMusteriler(rcMusteri, musteriList)
                    setupRecyclerViewMusteriler(rcPazartesiMusteri, pazartesiList)
                    setupRecyclerViewMusteriler(rcSaliMusteri, saliList)
                    setupRecyclerViewMusteriler(rcCarsambaMusteri, carsambaList)
                    setupRecyclerViewMusteriler(rcPersMusteri, persembeList)
                    setupRecyclerViewMusteriler(rcCumaMusteri, cumaList)
                    setupRecyclerViewMusteriler(rcCumartesiMusteri, cumartesiList)


                    //     Log.e("ass", musteriAdList.size.toString() + " " + musteriAdList[1].toString())
                } else {
                    Toast.makeText(this@MusterilerActivity, "Müşteri Bilgisi Alınamadı.", Toast.LENGTH_SHORT).show()
                }
            }


        })


    }

    fun setupBtn2() {

        imgPazartesiDown.setOnClickListener {
            imgPazartesiDown.visibility = View.GONE
            imgPazartesiUp.visibility = View.VISIBLE
            rcPazartesiMusteri.setAnimation(AnimationUtils.loadAnimation(this, R.anim.ustten_inme))
            rcPazartesiMusteri.visibility = View.VISIBLE

        }
        imgPazartesiUp.setOnClickListener {
            imgPazartesiDown.visibility = View.VISIBLE
            imgPazartesiUp.visibility = View.GONE
            rcPazartesiMusteri.visibility = View.GONE
        }


        imgSaliDown.setOnClickListener {
            imgSaliDown.visibility = View.GONE
            imgSaliUp.visibility = View.VISIBLE
            rcSaliMusteri.setAnimation(AnimationUtils.loadAnimation(this, R.anim.ustten_inme))
            rcSaliMusteri.visibility = View.VISIBLE

        }
        imgSaliUp.setOnClickListener {
            imgSaliDown.visibility = View.VISIBLE
            imgSaliUp.visibility = View.GONE
            rcSaliMusteri.visibility = View.GONE
        }


        imgCarsambaDown.setOnClickListener {
            imgCarsambaDown.visibility = View.GONE
            imgCarsambaUp.visibility = View.VISIBLE
            rcCarsambaMusteri.setAnimation(AnimationUtils.loadAnimation(this, R.anim.ustten_inme))
            rcCarsambaMusteri.visibility = View.VISIBLE

        }
        imgCarsambaUp.setOnClickListener {
            imgCarsambaDown.visibility = View.VISIBLE
            imgCarsambaUp.visibility = View.GONE
            rcCarsambaMusteri.visibility = View.GONE
        }

        imgPersDown.setOnClickListener {
            imgPersDown.visibility = View.GONE
            imgPersUp.visibility = View.VISIBLE
            rcPersMusteri.setAnimation(AnimationUtils.loadAnimation(this, R.anim.ustten_inme))
            rcPersMusteri.visibility = View.VISIBLE

        }
        imgPersUp.setOnClickListener {
            imgPersDown.visibility = View.VISIBLE
            imgPersUp.visibility = View.GONE
            rcPersMusteri.visibility = View.GONE
        }
        imgCumaDown.setOnClickListener {
            imgCumaDown.visibility = View.GONE
            imgCumaUp.visibility = View.VISIBLE
            rcCumaMusteri.setAnimation(AnimationUtils.loadAnimation(this, R.anim.ustten_inme))
            rcCumaMusteri.visibility = View.VISIBLE

        }
        imgCumaUp.setOnClickListener {
            imgCumaDown.visibility = View.VISIBLE
            imgCumaUp.visibility = View.GONE
            rcCumaMusteri.visibility = View.GONE
        }
        imgCumartesiDown.setOnClickListener {
            imgCumartesiDown.visibility = View.GONE
            imgCumartesiUp.visibility = View.VISIBLE
            rcCumartesiMusteri.setAnimation(AnimationUtils.loadAnimation(this, R.anim.ustten_inme))
            rcCumartesiMusteri.visibility = View.VISIBLE

        }
        imgCumartesiUp.setOnClickListener {
            imgCumartesiDown.visibility = View.VISIBLE
            imgCumartesiUp.visibility = View.GONE
            rcCumartesiMusteri.visibility = View.GONE
        }


    }

    fun setupBtn() {
        imgMusteriEkle.setOnClickListener {
            var builder: AlertDialog.Builder = AlertDialog.Builder(this)
            var inflater: LayoutInflater = layoutInflater
            dialogView = inflater.inflate(R.layout.dialog_musteri_ekle, null)

            builder.setView(dialogView)
            builder.setTitle("Müşteri Ekle")
//////////////////////spinner

            val mahalleler = ArrayList<String>()


            mahalleler.add("Fener")
            mahalleler.add("Meşrutiyet")
            mahalleler.add("Rüzgarlı")
            mahalleler.add("Karaelmas")

            mahalleler.add("İnağzı")
            mahalleler.add("Kilimli")
            mahalleler.add("Karadon")
            mahalleler.add("Çatalağzı")


            mahalleler.add("Tepebaşı")
            mahalleler.add("Kapuz")
            mahalleler.add("Yeşil")
            mahalleler.add("Yeni")
            mahalleler.add("Bağlık")

            mahalleler.add("Çaydamar")
            mahalleler.add("Kokaksu")
            mahalleler.add("Birlik")
            mahalleler.add("Müftülük")
            mahalleler.add("Ontemmuz")
            mahalleler.add("Soğuksu")

            mahalleler.add("Kavaklık")
            mahalleler.add("Kozlu")
            mahalleler.add("Tıp")

            mahalleler.add("Site")
            mahalleler.add("İncivez")


            var adapterMah = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mahalleler)
            adapterMah.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dialogView.spnMahler.adapter = adapterMah
            dialogView.spnMahler.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(this@MusterilerActivity, "Lütfen Mahalle Seç", Toast.LENGTH_LONG).show()
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    secilenMah = dialogView.spnMahler.selectedItem.toString()

                }

            }

//////////////////////spinner

            dialogView.etAdres.addTextChangedListener(watcherAdres)

            builder.setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.dismiss()
                }

            })
            builder.setPositiveButton("Ekle", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {


                    var musteriAdi = "Müşteri"
                    if (!dialogView.etMusteriAdSoyad.text.toString().isNullOrEmpty()) {
                        musteriAdi = dialogView.etMusteriAdSoyad.text.toString().trim().capitalize()
                    } else if (musteriAdi == "Müşteri") {
                        Toast.makeText(this@MusterilerActivity, "Müşteri Adı Girmedin", Toast.LENGTH_LONG).show()
                    }
                    var musteriAdres = "Adres"
                    if (!dialogView.etAdres.text.toString().isNullOrEmpty()) {
                        musteriAdres = dialogView.etAdres.text.toString().trim()
                    } else if (musteriAdres == "Adres") {
                        Toast.makeText(this@MusterilerActivity, "Adres Girmedin", Toast.LENGTH_LONG).show()
                    }

                    var musteriApt = "Apartman"
                    if (!dialogView.etApartman.text.toString().isNullOrEmpty()) {
                        musteriApt = dialogView.etApartman.text.toString().trim()
                    } else if (musteriAdres == "Adres") {
                        Toast.makeText(this@MusterilerActivity, "Adres Girmedin", Toast.LENGTH_LONG).show()
                    }


                    var musteriTel = "Tel"
                    if (!dialogView.etTelefon.text.toString().isNullOrEmpty()) {
                        musteriTel = dialogView.etTelefon.text.toString()
                    } else if (musteriTel == "Tel") {
                        Toast.makeText(this@MusterilerActivity, "Tel Girmedin", Toast.LENGTH_LONG).show()
                    }


                    var musteriBilgileri = MusteriData(musteriAdi, secilenMah, musteriAdres, musteriApt, musteriTel, null, false, null, null)

                    FirebaseDatabase.getInstance().reference.child("Musteriler").child(musteriAdi.toString()).setValue(musteriBilgileri).addOnCompleteListener {
                        FirebaseDatabase.getInstance().reference.child("Musteriler").child(musteriAdi.toString()).child("siparis_son_zaman").setValue(ServerValue.TIMESTAMP)
                        setupVeri()
                    }

                }
            })

            var dialog: Dialog = builder.create()
            dialog.show()

        }

        imgMusteriAra.setOnClickListener {
            var arananMusteriAdi = searchMs.text.toString()

            val arananMusteriVarMi = musteriAdList.containsAll(listOf(arananMusteriAdi))

            if (arananMusteriVarMi) {
                var ref = FirebaseDatabase.getInstance().reference
                ref.child("Musteriler").child(arananMusteriAdi).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        if (p0.hasChildren()) {
                            var musteri = p0.getValue(MusteriData::class.java)!!
                            val popup = PopupMenu(this@MusterilerActivity, imgMusteriAra)
                            popup.inflate(R.menu.popup_menu_musteri)
                            popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                                when (it.itemId) {
                                    R.id.popHizliSiparis -> {

                                        var builder: AlertDialog.Builder = AlertDialog.Builder(this@MusterilerActivity)
                                        var dialogViewSp = View.inflate(this@MusterilerActivity, R.layout.dialog_siparis_ekle, null)


                                        dialogViewSp.tvZamanEkleDialog.text = SimpleDateFormat("HH:mm dd.MM.yyyy").format(System.currentTimeMillis())
                                        var cal = Calendar.getInstance()
                                        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                            cal.set(Calendar.YEAR, year)
                                            cal.set(Calendar.MONTH, monthOfYear)
                                            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)


                                            val myFormat = "HH:mm dd.MM.yyyy" // mention the format you need
                                            val sdf = SimpleDateFormat(myFormat, Locale("tr"))
                                            dialogViewSp.tvZamanEkleDialog.text = sdf.format(cal.time)
                                        }

                                        val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                                            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                            cal.set(Calendar.MINUTE, minute)
                                        }

                                        dialogViewSp.tvZamanEkleDialog.setOnClickListener {
                                            DatePickerDialog(this@MusterilerActivity, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                                            TimePickerDialog(this@MusterilerActivity, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
                                        }

                                        dialogViewSp.tablePeynirler.visibility = View.GONE
                                        dialogViewSp.tableDiger.visibility = View.GONE
                                        dialogViewSp.peynirAsagi.setOnClickListener {
                                            dialogViewSp.tablePeynirler.setAnimation(AnimationUtils.loadAnimation(this@MusterilerActivity, R.anim.ustten_inme))
                                            dialogViewSp.tablePeynirler.visibility = View.VISIBLE
                                            dialogViewSp.peynirAsagi.visibility = View.GONE
                                            dialogViewSp.peynirYukari.visibility = View.VISIBLE

                                        }
                                        dialogViewSp.peynirYukari.setOnClickListener {
                                            dialogViewSp.tablePeynirler.visibility = View.GONE
                                            dialogViewSp.peynirAsagi.visibility = View.VISIBLE
                                            dialogViewSp.peynirYukari.visibility = View.GONE

                                        }

                                        dialogViewSp.digerAsagi.setOnClickListener {
                                            dialogViewSp.tableDiger.setAnimation(AnimationUtils.loadAnimation(this@MusterilerActivity, R.anim.ustten_inme))
                                            dialogViewSp.tableDiger.visibility = View.VISIBLE
                                            dialogViewSp.digerAsagi.visibility = View.GONE
                                            dialogViewSp.digerYukari.visibility = View.VISIBLE

                                        }
                                        dialogViewSp.digerYukari.setOnClickListener {
                                            dialogViewSp.tableDiger.visibility = View.GONE
                                            dialogViewSp.digerAsagi.visibility = View.VISIBLE
                                            dialogViewSp.digerYukari.visibility = View.GONE

                                        }


                                        builder.setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                                dialog!!.dismiss()
                                            }

                                        })
                                        builder.setPositiveButton("Hızlı Sipariş Ekle", object : DialogInterface.OnClickListener {
                                            override fun onClick(dialog: DialogInterface?, which: Int) {


                                                var kasar400 = "0"
                                                if (dialogViewSp.et400Kasar.text.toString().isNotEmpty()) kasar400 = dialogViewSp.et400Kasar.text.toString()
                                                var kasar400Fiyat = 0.0
                                                if (dialogViewSp.et400KasarFiyat.text.toString().isNotEmpty()) kasar400Fiyat = dialogViewSp.et400KasarFiyat.text.toString().toDouble()

                                                var kasar600 = "0"
                                                if (dialogViewSp.et600Kasar.text.toString().isNotEmpty()) kasar600 = dialogViewSp.et600Kasar.text.toString()

                                                var kasar600Fiyat =  0.0
                                                if (dialogViewSp.et600KasarFiyat.text.toString().isNotEmpty()) kasar600Fiyat = dialogViewSp.et600KasarFiyat.text.toString().toDouble()

                                                var yumurta = "0"
                                                if (dialogViewSp.etYumurta.text.toString().isNotEmpty()) {
                                                    yumurta = dialogViewSp.etYumurta.text.toString()
                                                }
                                                var yumurta_fiyat =  0.0
                                                if (dialogViewSp.etYumurtaFiyat.text.toString().isNotEmpty()) {
                                                    yumurta_fiyat = dialogViewSp.etYumurtaFiyat.text.toString().toDouble()
                                                }
                                                var yogurt = "0"
                                                if (dialogViewSp.etYogurt.text.toString().isNotEmpty()) {
                                                    yogurt = dialogViewSp.etYogurt.text.toString()
                                                }
                                                var yogurt_fiyat =  0.0
                                                if (dialogViewSp.etYogurtFiyat.text.toString().isNotEmpty()) {
                                                    yogurt_fiyat = dialogViewSp.etYogurtFiyat.text.toString().toDouble()
                                                }
                                                var yogurt3 = "0"
                                                if (dialogViewSp.etYogurt3.text.toString().isNotEmpty()) {
                                                    yogurt3 = dialogViewSp.etYogurt3.text.toString()
                                                }
                                                var yogurt3_fiyat =  0.0
                                                if (dialogViewSp.etYogurt3Fiyat.text.toString().isNotEmpty()) {
                                                    yogurt3_fiyat = dialogViewSp.etYogurt3Fiyat.text.toString().toDouble()
                                                }
                                                var sutCokelegi = "0"
                                                if (dialogViewSp.etSutCokelegi.text.toString().isNotEmpty()) {
                                                    sutCokelegi = dialogViewSp.etSutCokelegi.text.toString()
                                                }
                                                var sutCokelegiFiyat =  0.0
                                                if (dialogViewSp.etSutCokelegiFiyat.text.toString().isNotEmpty()) {
                                                    sutCokelegiFiyat = dialogViewSp.etSutCokelegiFiyat.text.toString().toDouble()
                                                }
                                                var cokertmePey = "0"
                                                if (dialogViewSp.etCokertmePeyniri.text.toString().isNotEmpty()) {
                                                    cokertmePey = dialogViewSp.etCokertmePeyniri.text.toString()
                                                }
                                                var cokertmePeyF =  0.0
                                                if (dialogViewSp.etCokertmePeyniriFiyat.text.toString().isNotEmpty()) {
                                                    cokertmePeyF = dialogViewSp.etCokertmePeyniriFiyat.text.toString().toDouble()
                                                }
                                                var dilPey = "0"
                                                if (dialogViewSp.etDilPeyniri.text.toString().isNotEmpty()) {
                                                    dilPey = dialogViewSp.etDilPeyniri.text.toString()
                                                }
                                                var dilPeyF =  0.0
                                                if (dialogViewSp.etDilPeyniriFiyat.text.toString().isNotEmpty()) {
                                                    dilPeyF = dialogViewSp.etDilPeyniriFiyat.text.toString().toDouble()
                                                }
                                                var beyazPey = "0"
                                                if (dialogViewSp.etBeyazPeynir.text.toString().isNotEmpty()) {
                                                    beyazPey = dialogViewSp.etBeyazPeynir.text.toString()
                                                }
                                                var beyazPeyF =  0.0
                                                if (dialogViewSp.etBeyazPeynirFiyat.text.toString().isNotEmpty()) {
                                                    beyazPeyF = dialogViewSp.etBeyazPeynirFiyat.text.toString().toDouble()
                                                }
                                                var beyazPey1000 = "0"
                                                if (dialogViewSp.etBeyazPeynir1000.text.toString().isNotEmpty()) {
                                                    beyazPey1000 = dialogViewSp.etBeyazPeynir1000.text.toString()
                                                }
                                                var beyazPey1000F =  0.0
                                                if (dialogViewSp.etBeyazPeynir1000Fiyat.text.toString().isNotEmpty()) {
                                                    beyazPey1000F = dialogViewSp.etBeyazPeynir1000Fiyat.text.toString().toDouble()
                                                }
                                                var cigSut = "0"
                                                if (dialogViewSp.etCigsut.text.toString().isNotEmpty()) {
                                                    cigSut = dialogViewSp.etCigsut.text.toString()
                                                }
                                                var cigSutF =  0.0
                                                if (dialogViewSp.etCigsutFiyat.text.toString().isNotEmpty()) {
                                                    cigSutF = dialogViewSp.etCigsutFiyat.text.toString().toDouble()
                                                }
                                                var kangal = "0"
                                                if (dialogViewSp.etKangal.text.toString().isNotEmpty()) {
                                                    kangal = dialogViewSp.etKangal.text.toString()
                                                }
                                                var kangalF =  0.0
                                                if (dialogViewSp.etKangalFiyat.text.toString().isNotEmpty()) {
                                                    kangalF = dialogViewSp.etKangalFiyat.text.toString().toDouble()
                                                }
                                                var sucuk = "0"
                                                if (dialogViewSp.etSucuk.text.toString().isNotEmpty()) {
                                                    sucuk = dialogViewSp.etSucuk.text.toString()
                                                }
                                                var sucukF =  0.0
                                                if (dialogViewSp.etSucukFiyat.text.toString().isNotEmpty()) {
                                                    sucukF = dialogViewSp.etSucukFiyat.text.toString().toDouble()
                                                }
                                                var kavurma = "0"
                                                if (dialogViewSp.etKavurma.text.toString().isNotEmpty()) {
                                                    kavurma = dialogViewSp.etKavurma.text.toString()
                                                }
                                                var kavurmaF =  0.0
                                                if (dialogViewSp.etKavurmaFiyat.text.toString().isNotEmpty()) {
                                                    kavurmaF = dialogViewSp.etKavurmaFiyat.text.toString().toDouble()
                                                }
                                                var kefir = "0"
                                                if (dialogViewSp.etKefir.text.toString().isNotEmpty()) {
                                                    kefir = dialogViewSp.etKefir.text.toString()
                                                }
                                                var kefirF = 0.0
                                                if (dialogViewSp.etKefirFiyat.text.toString().isNotEmpty()) {
                                                    kefirF = dialogViewSp.etKefirFiyat.text.toString().toDouble()
                                                }


                                                var siparisNotu = dialogViewSp.etSiparisNotu.text.toString()
                                                var siparisKey = ref.child("Siparisler").push().key.toString()
                                                var siparisData = SiparisData(
                                                    musteri.musteri_zkonum, musteri.musteri_adres, musteri.musteri_apartman, siparisKey, musteri.musteri_mah, siparisNotu, musteri.musteri_tel, cal.timeInMillis, System.currentTimeMillis(),
                                                    musteri.musteri_ad_soyad, System.currentTimeMillis(), kullaniciAdi.toString(), sucuk, sucukF, sutCokelegi, sutCokelegiFiyat, yogurt, yogurt_fiyat, yogurt3, yogurt3_fiyat,
                                                    yumurta, yumurta_fiyat, beyazPey, beyazPeyF, beyazPey1000, beyazPey1000F, cigSut, cigSutF, cokertmePey, cokertmePeyF, dilPey, dilPeyF,
                                                    kangal, kangalF, kasar400, kasar400Fiyat, kasar600, kasar600Fiyat, kavurma, kavurmaF, kefir, kefirF, musteri.musteri_zlat, musteri.musteri_zlong
                                                )
                                                ref.child("Teslim_siparisler").child(siparisKey).setValue(siparisData)
                                                ref.child("Musteriler").child(musteri.musteri_ad_soyad.toString()).child("siparisleri").child(siparisKey).setValue(siparisData)


                                            }
                                        })

                                        builder.setTitle(musteri.musteri_ad_soyad)
                                        builder.setIcon(R.drawable.cow)

                                        builder.setView(dialogViewSp)
                                        var dialog: Dialog = builder.create()
                                        dialog.show()
                                    }
                                    R.id.popSiparisGir -> {
                                        var builder: AlertDialog.Builder = AlertDialog.Builder(this@MusterilerActivity)
                                        var dialogViewSp = View.inflate(this@MusterilerActivity, R.layout.dialog_siparis_ekle, null)


                                        dialogViewSp.tvZamanEkleDialog.text = SimpleDateFormat("HH:mm dd.MM.yyyy").format(System.currentTimeMillis())
                                        var cal = Calendar.getInstance()
                                        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                            cal.set(Calendar.YEAR, year)
                                            cal.set(Calendar.MONTH, monthOfYear)
                                            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)


                                            val myFormat = "HH:mm dd.MM.yyyy" // mention the format you need
                                            val sdf = SimpleDateFormat(myFormat, Locale("tr"))
                                            dialogViewSp.tvZamanEkleDialog.text = sdf.format(cal.time)
                                        }

                                        val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                                            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                            cal.set(Calendar.MINUTE, minute)
                                        }

                                        dialogViewSp.tvZamanEkleDialog.setOnClickListener {
                                            DatePickerDialog(this@MusterilerActivity, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                                            TimePickerDialog(this@MusterilerActivity, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
                                        }

                                        dialogViewSp.tablePeynirler.visibility = View.GONE
                                        dialogViewSp.tableDiger.visibility = View.GONE
                                        dialogViewSp.peynirAsagi.setOnClickListener {
                                            dialogViewSp.tablePeynirler.setAnimation(AnimationUtils.loadAnimation(this@MusterilerActivity, R.anim.ustten_inme))
                                            dialogViewSp.tablePeynirler.visibility = View.VISIBLE
                                            dialogViewSp.peynirAsagi.visibility = View.GONE
                                            dialogViewSp.peynirYukari.visibility = View.VISIBLE

                                        }
                                        dialogViewSp.peynirYukari.setOnClickListener {
                                            dialogViewSp.tablePeynirler.visibility = View.GONE
                                            dialogViewSp.peynirAsagi.visibility = View.VISIBLE
                                            dialogViewSp.peynirYukari.visibility = View.GONE

                                        }

                                        dialogViewSp.digerAsagi.setOnClickListener {
                                            dialogViewSp.tableDiger.setAnimation(AnimationUtils.loadAnimation(this@MusterilerActivity, R.anim.ustten_inme))
                                            dialogViewSp.tableDiger.visibility = View.VISIBLE
                                            dialogViewSp.digerAsagi.visibility = View.GONE
                                            dialogViewSp.digerYukari.visibility = View.VISIBLE

                                        }
                                        dialogViewSp.digerYukari.setOnClickListener {
                                            dialogViewSp.tableDiger.visibility = View.GONE
                                            dialogViewSp.digerAsagi.visibility = View.VISIBLE
                                            dialogViewSp.digerYukari.visibility = View.GONE

                                        }


                                        builder.setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                                dialog!!.dismiss()
                                            }

                                        })
                                        builder.setPositiveButton("Sipariş Ekle", object : DialogInterface.OnClickListener {
                                            override fun onClick(dialog: DialogInterface?, which: Int) {


                                                var kasar400 = "0"
                                                if (dialogViewSp.et400Kasar.text.toString().isNotEmpty()) kasar400 = dialogViewSp.et400Kasar.text.toString()
                                                var kasar400Fiyat = 0.0
                                                if (dialogViewSp.et400KasarFiyat.text.toString().isNotEmpty()) kasar400Fiyat = dialogViewSp.et400KasarFiyat.text.toString().toDouble()

                                                var kasar600 = "0"
                                                if (dialogViewSp.et600Kasar.text.toString().isNotEmpty()) kasar600 = dialogViewSp.et600Kasar.text.toString()

                                                var kasar600Fiyat =  0.0
                                                if (dialogViewSp.et600KasarFiyat.text.toString().isNotEmpty()) kasar600Fiyat = dialogViewSp.et600KasarFiyat.text.toString().toDouble()

                                                var yumurta = "0"
                                                if (dialogViewSp.etYumurta.text.toString().isNotEmpty()) {
                                                    yumurta = dialogViewSp.etYumurta.text.toString()
                                                }
                                                var yumurta_fiyat =  0.0
                                                if (dialogViewSp.etYumurtaFiyat.text.toString().isNotEmpty()) {
                                                    yumurta_fiyat = dialogViewSp.etYumurtaFiyat.text.toString().toDouble()
                                                }
                                                var yogurt = "0"
                                                if (dialogViewSp.etYogurt.text.toString().isNotEmpty()) {
                                                    yogurt = dialogViewSp.etYogurt.text.toString()
                                                }
                                                var yogurt_fiyat =  0.0
                                                if (dialogViewSp.etYogurtFiyat.text.toString().isNotEmpty()) {
                                                    yogurt_fiyat = dialogViewSp.etYogurtFiyat.text.toString().toDouble()
                                                }
                                                var yogurt3 = "0"
                                                if (dialogViewSp.etYogurt3.text.toString().isNotEmpty()) {
                                                    yogurt3 = dialogViewSp.etYogurt3.text.toString()
                                                }
                                                var yogurt3_fiyat =  0.0
                                                if (dialogViewSp.etYogurt3Fiyat.text.toString().isNotEmpty()) {
                                                    yogurt3_fiyat = dialogViewSp.etYogurt3Fiyat.text.toString().toDouble()
                                                }
                                                var sutCokelegi = "0"
                                                if (dialogViewSp.etSutCokelegi.text.toString().isNotEmpty()) {
                                                    sutCokelegi = dialogViewSp.etSutCokelegi.text.toString()
                                                }
                                                var sutCokelegiFiyat =  0.0
                                                if (dialogViewSp.etSutCokelegiFiyat.text.toString().isNotEmpty()) {
                                                    sutCokelegiFiyat = dialogViewSp.etSutCokelegiFiyat.text.toString().toDouble()
                                                }
                                                var cokertmePey = "0"
                                                if (dialogViewSp.etCokertmePeyniri.text.toString().isNotEmpty()) {
                                                    cokertmePey = dialogViewSp.etCokertmePeyniri.text.toString()
                                                }
                                                var cokertmePeyF =  0.0
                                                if (dialogViewSp.etCokertmePeyniriFiyat.text.toString().isNotEmpty()) {
                                                    cokertmePeyF = dialogViewSp.etCokertmePeyniriFiyat.text.toString().toDouble()
                                                }
                                                var dilPey = "0"
                                                if (dialogViewSp.etDilPeyniri.text.toString().isNotEmpty()) {
                                                    dilPey = dialogViewSp.etDilPeyniri.text.toString()
                                                }
                                                var dilPeyF =  0.0
                                                if (dialogViewSp.etDilPeyniriFiyat.text.toString().isNotEmpty()) {
                                                    dilPeyF = dialogViewSp.etDilPeyniriFiyat.text.toString().toDouble()
                                                }
                                                var beyazPey = "0"
                                                if (dialogViewSp.etBeyazPeynir.text.toString().isNotEmpty()) {
                                                    beyazPey = dialogViewSp.etBeyazPeynir.text.toString()
                                                }
                                                var beyazPeyF =  0.0
                                                if (dialogViewSp.etBeyazPeynirFiyat.text.toString().isNotEmpty()) {
                                                    beyazPeyF = dialogViewSp.etBeyazPeynirFiyat.text.toString().toDouble()
                                                }
                                                var beyazPey1000 = "0"
                                                if (dialogViewSp.etBeyazPeynir1000.text.toString().isNotEmpty()) {
                                                    beyazPey1000 = dialogViewSp.etBeyazPeynir1000.text.toString()
                                                }
                                                var beyazPey1000F =  0.0
                                                if (dialogViewSp.etBeyazPeynir1000Fiyat.text.toString().isNotEmpty()) {
                                                    beyazPey1000F = dialogViewSp.etBeyazPeynir1000Fiyat.text.toString().toDouble()
                                                }
                                                var cigSut = "0"
                                                if (dialogViewSp.etCigsut.text.toString().isNotEmpty()) {
                                                    cigSut = dialogViewSp.etCigsut.text.toString()
                                                }
                                                var cigSutF =  0.0
                                                if (dialogViewSp.etCigsutFiyat.text.toString().isNotEmpty()) {
                                                    cigSutF = dialogViewSp.etCigsutFiyat.text.toString().toDouble()
                                                }
                                                var kangal = "0"
                                                if (dialogViewSp.etKangal.text.toString().isNotEmpty()) {
                                                    kangal = dialogViewSp.etKangal.text.toString()
                                                }
                                                var kangalF =  0.0
                                                if (dialogViewSp.etKangalFiyat.text.toString().isNotEmpty()) {
                                                    kangalF = dialogViewSp.etKangalFiyat.text.toString().toDouble()
                                                }
                                                var sucuk = "0"
                                                if (dialogViewSp.etSucuk.text.toString().isNotEmpty()) {
                                                    sucuk = dialogViewSp.etSucuk.text.toString()
                                                }
                                                var sucukF =  0.0
                                                if (dialogViewSp.etSucukFiyat.text.toString().isNotEmpty()) {
                                                    sucukF = dialogViewSp.etSucukFiyat.text.toString().toDouble()
                                                }
                                                var kavurma = "0"
                                                if (dialogViewSp.etKavurma.text.toString().isNotEmpty()) {
                                                    kavurma = dialogViewSp.etKavurma.text.toString()
                                                }
                                                var kavurmaF =  0.0
                                                if (dialogViewSp.etKavurmaFiyat.text.toString().isNotEmpty()) {
                                                    kavurmaF = dialogViewSp.etKavurmaFiyat.text.toString().toDouble()
                                                }
                                                var kefir = "0"
                                                if (dialogViewSp.etKefir.text.toString().isNotEmpty()) {
                                                    kefir = dialogViewSp.etKefir.text.toString()
                                                }
                                                var kefirF = 0.0
                                                if (dialogViewSp.etKefirFiyat.text.toString().isNotEmpty()) {
                                                    kefirF = dialogViewSp.etKefirFiyat.text.toString().toDouble()
                                                }


                                                var siparisNotu = dialogViewSp.etSiparisNotu.text.toString()
                                                var siparisKey = ref.child("Siparisler").push().key.toString()
                                                var siparisData = SiparisData(
                                                    musteri.musteri_zkonum, musteri.musteri_adres, musteri.musteri_apartman, siparisKey, musteri.musteri_mah, siparisNotu, musteri.musteri_tel, cal.timeInMillis, System.currentTimeMillis(),
                                                    musteri.musteri_ad_soyad, System.currentTimeMillis(), kullaniciAdi.toString(), sucuk, sucukF, sutCokelegi, sutCokelegiFiyat, yogurt, yogurt_fiyat, yogurt3, yogurt3_fiyat,
                                                    yumurta, yumurta_fiyat, beyazPey, beyazPeyF, beyazPey1000, beyazPey1000F, cigSut, cigSutF, cokertmePey, cokertmePeyF, dilPey, dilPeyF,
                                                    kangal, kangalF, kasar400, kasar400Fiyat, kasar600, kasar600Fiyat, kavurma, kavurmaF, kefir, kefirF, musteri.musteri_zlat, musteri.musteri_zlong
                                                )
                                                ref.child("Siparisler").child(siparisKey).setValue(siparisData)
                                                ref.child("Siparisler").child(siparisKey).child("siparis_zamani").setValue(ServerValue.TIMESTAMP)
                                                ref.child("Siparisler").child(siparisKey).child("siparis_teslim_zamani").setValue(ServerValue.TIMESTAMP)

                                                ref.child("Musteriler").child(musteri.musteri_ad_soyad.toString()).child("siparisleri").child(siparisKey).setValue(siparisData)
                                                ref.child("Musteriler").child(musteri.musteri_ad_soyad.toString()).child("siparisleri").child(siparisKey).child("siparis_teslim_zamani").setValue(ServerValue.TIMESTAMP)
                                                ref.child("Musteriler").child(musteri.musteri_ad_soyad.toString()).child("siparisleri").child(siparisKey).child("siparis_zamani").setValue(ServerValue.TIMESTAMP)


                                            }
                                        })

                                        builder.setTitle(musteri.musteri_ad_soyad)
                                        builder.setIcon(R.drawable.cow)

                                        builder.setView(dialogViewSp)
                                        var dialog: Dialog = builder.create()
                                        dialog.show()
                                    }
                                    R.id.popDüzenle -> {
                                        var dialogMsDznle: Dialog

                                        var musteriAdi = musteri.musteri_ad_soyad.toString()
                                        var builder: AlertDialog.Builder = AlertDialog.Builder(this@MusterilerActivity)

                                        var dialogView: View = inflate(this@MusterilerActivity, R.layout.dialog_gidilen_musteri, null)
                                        builder.setView(dialogView)

                                        dialogMsDznle = builder.create()

                                        dialogView.swKonumKaydet.setOnClickListener {
                                            val intent = Intent(this@MusterilerActivity, AdresBulmaMapsActivity::class.java)
                                            intent.putExtra("musteriAdi", musteriAdi)
                                            startActivity(intent)
                                        }

                                        dialogView.imgCheck.setOnClickListener {
                                            if (dialogView.etAdresGidilen.text.toString().isNotEmpty() && dialogView.etTelefonGidilen.text.toString().isNotEmpty()) {
                                                var adres = dialogView.etAdresGidilen.text.toString()
                                                var telefon = dialogView.etTelefonGidilen.text.toString()
                                                var apartman = dialogView.etApartman.text.toString()

                                               ref.child("Musteriler").child(musteriAdi).child("musteri_adres").setValue(adres)
                                             ref.child("Musteriler").child(musteriAdi).child("musteri_apartman").setValue(apartman)
                                               ref.child("Musteriler").child(musteriAdi).child("musteri_tel").setValue(telefon).addOnCompleteListener {
                                                    dialogMsDznle.dismiss()
                                                    Toast.makeText(this@MusterilerActivity, "Müşteri Bilgileri Güncellendi", Toast.LENGTH_LONG).show()

                                                }.addOnFailureListener { Toast.makeText(this@MusterilerActivity, "Müşteri Bilgileri Güncellenemedi", Toast.LENGTH_LONG).show() }

                                            } else Toast.makeText(this@MusterilerActivity, "Bilgilerde boşluklar var", Toast.LENGTH_LONG).show()

                                        }

                                        dialogView.imgBack.setOnClickListener {
                                            dialogMsDznle.dismiss()
                                        }

                                        dialogView.tvAdSoyad.text = musteri.musteri_ad_soyad.toString()
                                        dialogView.tvMahalle.text = musteri.musteri_mah.toString() + " Mahallesi"
                                        dialogView.etApartman.setText(musteri.musteri_apartman.toString())
                                        FirebaseDatabase.getInstance().reference.child("Musteriler").child(musteriAdi).addListenerForSingleValueEvent(object :
                                            ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError) {}
                                            override fun onDataChange(p0: DataSnapshot) {
                                                var adres = p0.child("musteri_adres").value.toString()
                                                var telefon = p0.child("musteri_tel").value.toString()
                                                var konum = p0.child("musteri_zkonum").value.toString().toBoolean()

                                                dialogView.swKonumKaydet.isChecked = konum
                                                dialogView.etAdresGidilen.setText(adres)
                                                dialogView.etTelefonGidilen.setText(telefon)

                                                var list = ArrayList<SiparisData>()
                                                list = ArrayList()
                                                if (p0.child("siparisleri").hasChildren()) {
                                                    for (ds in p0.child("siparisleri").children) {
                                                        var gelenData = ds.getValue(SiparisData::class.java)!!
                                                        list.add(gelenData)
                                                    }
                                                    dialogView.rcSiparisGidilen.layoutManager = LinearLayoutManager(this@MusterilerActivity, LinearLayoutManager.VERTICAL, false)
                                                    val Adapter = MusteriSiparisleriAdapter(this@MusterilerActivity, list)
                                                    dialogView.rcSiparisGidilen.adapter = Adapter
                                                    dialogView.rcSiparisGidilen.setHasFixedSize(true)
                                                }
                                            }
                                        })
                                        dialogMsDznle.setCancelable(false)
                                        dialogMsDznle.show()

                                    }
                                    R.id.popSil -> {
                                        var alert = AlertDialog.Builder(this@MusterilerActivity)
                                            .setTitle("Müşteriyi Sil")
                                            .setMessage("Emin Misin ?")
                                            .setPositiveButton("Sil", object : DialogInterface.OnClickListener {
                                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                                    ref.child("Musteriler").child(musteri.musteri_ad_soyad.toString()).removeValue()
                                                }
                                            })
                                            .setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                                    p0!!.dismiss()
                                                }
                                            }).create()
                                        alert.show()
                                    }
                                }
                                return@OnMenuItemClickListener true
                            })
                            popup.show()

                        }


                    }

                })




            } else {
                Toast.makeText(this, "Böyle Bir Müşteri Yok", Toast.LENGTH_LONG).show()
            }


        }

    }


    fun setupRecyclerViewMusteriler(rcMusteri: FastScrollRecyclerView, musteriList: ArrayList<MusteriData>) {
        rcMusteri.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val Adapter = MusteriAdapter(this, musteriList, kullaniciAdi)
        rcMusteri.adapter = Adapter
        rcMusteri.setHasFixedSize(true)
    }

    var watcherAdres = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s!!.length >= 0) {

                dialogView.tvAdresTam.text = secilenMah + " mahallesi " + s.toString()

            } else {

                dialogView.tvAdresTam.text = "Sadece Sokak ve No Girilecek  Örnek: Topuz Siteleri Sokak No 5 "
            }
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
