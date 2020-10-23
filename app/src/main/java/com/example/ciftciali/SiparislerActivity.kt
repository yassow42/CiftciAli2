package com.example.ciftciali

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils

import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ciftciali.Adapter.SiparisAdapter
import com.example.ciftciali.Datalar.SiparisData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_siparisler.*
import java.lang.Exception
import kotlin.collections.ArrayList

class SiparislerActivity : AppCompatActivity() {
    lateinit var fenerList: ArrayList<SiparisData>
    lateinit var mesrList: ArrayList<SiparisData>
    lateinit var ruzgarliList: ArrayList<SiparisData>
    lateinit var karaElmasList: ArrayList<SiparisData>
    lateinit var inAgziList: ArrayList<SiparisData>
    lateinit var kilimliList: ArrayList<SiparisData>
    lateinit var karadonList: ArrayList<SiparisData>
    lateinit var catalList: ArrayList<SiparisData>
    lateinit var tepeBasiList: ArrayList<SiparisData>
    lateinit var kapuzList: ArrayList<SiparisData>
    lateinit var yeniList: ArrayList<SiparisData>
    lateinit var yesilList: ArrayList<SiparisData>
    lateinit var baglikList: ArrayList<SiparisData>
    lateinit var cayDamarList: ArrayList<SiparisData>
    lateinit var kokaksuList: ArrayList<SiparisData>
    lateinit var birlikList: ArrayList<SiparisData>
    lateinit var muftuList: ArrayList<SiparisData>
    lateinit var ontemmuzList: ArrayList<SiparisData>
    lateinit var sogukList: ArrayList<SiparisData>
    lateinit var kavaklikList: ArrayList<SiparisData>
    lateinit var kozluList: ArrayList<SiparisData>
    lateinit var tipList: ArrayList<SiparisData>
    lateinit var siteList: ArrayList<SiparisData>
    lateinit var inciList: ArrayList<SiparisData>
    lateinit var ilerilist: ArrayList<SiparisData>


    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var userID: String
    lateinit var kullaniciAdi: String
    private val ACTIVITY_NO = 0

    lateinit var progressDialog: ProgressDialog
    val hndler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_siparisler)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        mAuth = FirebaseAuth.getInstance()
        initMyAuthStateListener()
        userID = mAuth.currentUser!!.uid


        setupKullaniciAdi()
        konumIzni()
        setupListeler()
        setupNavigationView()
        setupBtn()
        zamanAyarı()



        hndler.postDelayed(Runnable { setupVeri() }, 100)
        hndler.postDelayed(Runnable { progressDialog.dismiss() }, 5000)


    }


    private fun setupVeri() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Yükleniyor...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        var ref = FirebaseDatabase.getInstance().reference
        ref.child("Siparisler").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                var sayi = 0
                if (p0.hasChildren()) {
                    for (ds in p0.children) {
                        try {
                            var gelenData = ds.getValue(SiparisData::class.java)!!

                            if (gelenData.sucuk_fiyat == null) {
                                var refSiparisKey = ref.child("Siparisler").child(ds.key.toString())
                                refSiparisKey.child("sucuk_fiyat").setValue(0)
                                refSiparisKey.child("sut_cokelegi_fiyat").setValue(15)
                                refSiparisKey.child("yogurt_fiyat").setValue(10)
                                refSiparisKey.child("yogurt3").setValue("0") //-> Eklenecek
                                refSiparisKey.child("yogurt3_fiyat").setValue(20) //-> Eklenecek
                                refSiparisKey.child("yumurta_fiyat").setValue(2)
                                refSiparisKey.child("yybeyaz_pey1000").setValue("0")
                                refSiparisKey.child("yybeyaz_pey1000_fiyat").setValue(27.5)
                                refSiparisKey.child("yybeyaz_pey_fiyat").setValue(15)
                                refSiparisKey.child("yycig_sut_fiyat").setValue(15)
                                refSiparisKey.child("yycokertme_pey_fiyat").setValue(40)
                                refSiparisKey.child("yydil_pey_fiyat").setValue(30)
                                refSiparisKey.child("yykangal_sucuk_fiyat").setValue(0)
                                refSiparisKey.child("yykasar_400_fiyat").setValue(20)
                                refSiparisKey.child("yykasar_600_fiyat").setValue(30)
                                refSiparisKey.child("yykavurma_fiyat").setValue(0)
                                refSiparisKey.child("yykefir_fiyat").setValue(0)
                                refSiparisKey.child("yykefir").setValue("0")

                                Log.e("teslim activity", " gelen fiyat bilgileri guncellenıyor")
                            }



                            if (gelenData.siparis_teslim_tarihi!!.compareTo(System.currentTimeMillis()) == -1) {

                                if (gelenData.siparis_mah == "Fener") {
                                    fenerList.add(gelenData)
                                    fenerList.sortBy { it.siparis_adres }
                                    tvFenerSayi.text = fenerList.size.toString() + " Sipariş"
                                    recyclerView(rcFener, fenerList)

                                    if (fenerList.size > 0) {
                                        clFener.setBackgroundResource(R.drawable.bg_mah)
                                    }
                                }
                                if (gelenData.siparis_mah == "Meşrutiyet") {
                                    mesrList.add(gelenData)
                                    mesrList.sortBy { it.siparis_adres }
                                    tvMesrSayi.text = mesrList.size.toString() + " Sipariş"
                                    if (mesrList.size > 0) {
                                        clMesr.setBackgroundResource(R.drawable.bg_mah)
                                    }
                                    recyclerView(rcMesr, mesrList)
                                }
                                if (gelenData.siparis_mah == "Rüzgarlı") {
                                    ruzgarliList.add(gelenData)
                                    tvRuzgarliSayi.text = ruzgarliList.size.toString() + " Sipariş"

                                    if (ruzgarliList.size > 0) {
                                        clRuzgarli.setBackgroundResource(R.drawable.bg_mah)
                                    }

                                    recyclerView(rcRuzgarli, ruzgarliList)
                                }
                                if (gelenData.siparis_mah == "Karaelmas") {
                                    karaElmasList.add(gelenData)
                                    tvKaraelmasSayi.text = karaElmasList.size.toString() + " Sipariş"
                                    if (karaElmasList.size > 0) {
                                        clKaraelmas.setBackgroundResource(R.drawable.bg_mah)
                                    }
                                    recyclerView(rcKara, karaElmasList)
                                }

                                tvPazartesiSayi.text = (fenerList.size + mesrList.size + ruzgarliList.size + karaElmasList.size).toString() + " Sipariş"

                                if (gelenData.siparis_mah == "İnağzı") {
                                    inAgziList.add(gelenData)
                                    tvinagziSayi.text = inAgziList.size.toString() + " Sipariş"
                                    if (inAgziList.size > 0) {
                                        clinagzi.setBackgroundResource(R.drawable.bg_mah)
                                    }
                                    recyclerView(rcinagzi, inAgziList)
                                }
                                if (gelenData.siparis_mah == "Kilimli") {
                                    kilimliList.add(gelenData)
                                    tvKilimliSayi.text = kilimliList.size.toString() + " Sipariş"
                                    if (kilimliList.size > 0) {
                                        clKilimli.setBackgroundResource(R.drawable.bg_mah)
                                    }
                                    recyclerView(rcKilimli, kilimliList)
                                }
                                if (gelenData.siparis_mah == "Karadon") {
                                    karadonList.add(gelenData)
                                    tvKaradonSayi.text = karadonList.size.toString() + " Sipariş"
                                    if (karadonList.size > 0) {
                                        clKaradon.setBackgroundResource(R.drawable.bg_mah)
                                    }
                                    recyclerView(rcKaradon, karadonList)
                                }
                                if (gelenData.siparis_mah == "Çatalağzı") {
                                    catalList.add(gelenData)
                                    tvCatalSayi.text = catalList.size.toString() + " Sipariş"
                                    if (catalList.size > 0) {
                                        clCatal.setBackgroundResource(R.drawable.bg_mah)
                                    }
                                    recyclerView(rcCatal, catalList)
                                }
                                tvSaliSayi.text = (inAgziList.size + kilimliList.size + karadonList.size + catalList.size).toString() + " Sipariş"

                                if (gelenData.siparis_mah == "Tepebaşı") {
                                    tepeBasiList.add(gelenData)
                                    tvTepebasiSayi.text = tepeBasiList.size.toString() + " Sipariş"
                                    if (tepeBasiList.size > 0) {
                                        clTepebasi.setBackgroundResource(R.drawable.bg_mah)
                                    }
                                    recyclerView(rcTepebasi, tepeBasiList)
                                }
                                if (gelenData.siparis_mah == "Kapuz") {
                                    kapuzList.add(gelenData)
                                    tvKapuzSayi.text = kapuzList.size.toString() + " Sipariş"
                                    if (kapuzList.size > 0) {
                                        clKapuz.setBackgroundResource(R.drawable.bg_mah)
                                    }
                                    recyclerView(rcKapuz, kapuzList)
                                }
                                if (gelenData.siparis_mah == "Yeşil") {
                                    yesilList.add(gelenData)
                                    tvYesilSayi.text = yesilList.size.toString() + " Sipariş"
                                    if (yesilList.size > 0) {
                                        clYesil.setBackgroundResource(R.drawable.bg_mah)
                                    }
                                    recyclerView(rcYesil, yesilList)
                                }
                                if (gelenData.siparis_mah == "Yeni") {
                                    yeniList.add(gelenData)
                                    tvYeniSayi.text = yeniList.size.toString() + " Sipariş"
                                    if (yeniList.size > 0) {
                                        clYeni.setBackgroundColor(resources.getColor(R.color.kirmizi))
                                    }
                                    recyclerView(rcYeni, yeniList)
                                }
                                if (gelenData.siparis_mah == "Bağlık") {
                                    baglikList.add(gelenData)
                                    tvBaglikSayi.text = baglikList.size.toString() + " Sipariş"
                                    if (baglikList.size > 0) {
                                        clBaglik.setBackgroundResource(R.drawable.bg_mah)
                                    }
                                    recyclerView(rcBaglik, baglikList)
                                }

                                tvCarsambaSayi.text = (tepeBasiList.size + kapuzList.size + yesilList.size + yeniList.size + baglikList.size).toString() + " Sipariş"

                                if (gelenData.siparis_mah == "Çaydamar") {
                                    cayDamarList.add(gelenData)
                                    tvCaydamarSayi.text = cayDamarList.size.toString() + " Sipariş"
                                    if (cayDamarList.size > 0) {
                                        clCaydamar.setBackgroundResource(R.drawable.bg_mah)
                                    }
                                    recyclerView(rcCaydamar, cayDamarList)
                                }
                                if (gelenData.siparis_mah == "Kokaksu") {
                                    kokaksuList.add(gelenData)
                                    tvKokaksuSayi.text = kokaksuList.size.toString() + " Sipariş"
                                    if (kokaksuList.size > 0) {
                                        clKokaksu.setBackgroundResource(R.drawable.bg_mah)
                                    }
                                    recyclerView(rcKokaksu, kokaksuList)
                                }
                                if (gelenData.siparis_mah == "Birlik") {
                                    birlikList.add(gelenData)
                                    tvBirlikSayi.text = birlikList.size.toString() + " Sipariş"
                                    if (birlikList.size > 0) {
                                        clBirlik.setBackgroundResource(R.drawable.bg_mah)
                                    }
                                    recyclerView(rcBirlik, birlikList)
                                }
                                if (gelenData.siparis_mah == "Müftülük") {
                                    muftuList.add(gelenData)
                                    tvMuftuSayi.text = muftuList.size.toString() + " Sipariş"
                                    if (muftuList.size > 0) {
                                        clMuftu.setBackgroundResource(R.drawable.bg_mah)
                                    }
                                    recyclerView(rcMuftu, muftuList)
                                }
                                if (gelenData.siparis_mah == "Ontemmuz") {
                                    ontemmuzList.add(gelenData)
                                    tvTemmuzSayi.text = ontemmuzList.size.toString() + " Sipariş"
                                    if (ontemmuzList.size > 0) {
                                        clTemmuz.setBackgroundResource(R.drawable.bg_mah)
                                    }
                                    recyclerView(rcTemmuz, ontemmuzList)
                                }
                                if (gelenData.siparis_mah == "Soğuksu") {
                                    sogukList.add(gelenData)
                                    tvSogukSayi.text = sogukList.size.toString() + " Sipariş"
                                    if (sogukList.size > 0) {
                                        clSoguk.setBackgroundResource(R.drawable.bg_mah)
                                    }
                                    recyclerView(rcSoguk, sogukList)
                                }

                                tvPersSayi.text = (cayDamarList.size + kokaksuList.size + birlikList.size + muftuList.size + ontemmuzList.size + sogukList.size).toString() + " Sipariş"

                                if (gelenData.siparis_mah == "Kavaklık") {
                                    kavaklikList.add(gelenData)
                                    tvKavakSayi.text = kavaklikList.size.toString() + " Sipariş"
                                    if (kavaklikList.size > 0) {
                                        clKavak.setBackgroundResource(R.drawable.bg_mah)
                                    }
                                    recyclerView(rcKavak, kavaklikList)
                                }
                                if (gelenData.siparis_mah == "Kozlu") {
                                    kozluList.add(gelenData)
                                    tvKozluSayi.text = kozluList.size.toString() + " Sipariş"
                                    if (kozluList.size > 0) {
                                        clKozlu.setBackgroundResource(R.drawable.bg_mah)
                                    }
                                    recyclerView(rcKozlu, kozluList)
                                }
                                if (gelenData.siparis_mah == "Tıp") {
                                    tipList.add(gelenData)
                                    tvTipSayi.text = tipList.size.toString() + " Sipariş"
                                    if (tipList.size > 0) {
                                        clTip.setBackgroundResource(R.drawable.bg_mah)
                                    }
                                    recyclerView(rcTip, tipList)
                                }

                                tvCumaSayi.text = (kavaklikList.size + kozluList.size + tipList.size).toString() + " Sipariş"
                                if (gelenData.siparis_mah == "Site") {
                                    siteList.add(gelenData)
                                    tvSiteSayi.text = siteList.size.toString() + " Sipariş"
                                    if (siteList.size > 0) {
                                        clSite.setBackgroundResource(R.drawable.bg_mah)
                                    }
                                    recyclerView(rcSite, siteList)
                                }
                                if (gelenData.siparis_mah == "İncivez") {
                                    inciList.add(gelenData)
                                    tvinciSayi.text = inciList.size.toString() + " Sipariş"
                                    if (inciList.size > 0) {
                                        clinci.setBackgroundResource(R.drawable.bg_mah)
                                    }
                                    recyclerView(rcinci, inciList)
                                }
                                tvCumartesiSayi.text = (siteList.size + inciList.size).toString() + " Sipariş"

                            } else if (gelenData.siparis_teslim_tarihi!!.compareTo(System.currentTimeMillis()) == 1) {
                                ilerilist.add(gelenData)
                                tvileriSayi.text = ilerilist.size.toString() + " Sipariş"
                                recyclerViewileriTarihli()
                            }

                        } catch (e: Exception) {

                            ref.child("Hatalar/siparisDataHata").push().setValue(e.toString())

                        }
                    }
                    progressDialog.dismiss()
                } else {
                    progressDialog.setMessage("Sipariş yok...")
                    hndler.postDelayed(Runnable { progressDialog.dismiss() }, 2000)
                    Toast.makeText(this@SiparislerActivity, "Sipariş yok :(", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setupBtn() {

        imgCikis.setOnClickListener {
            mAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }
        //////////////////////////
        imgPazartesiDown.setOnClickListener {
            imgPazartesiDown.visibility = View.GONE
            imgPazartesiUp.visibility = View.VISIBLE
            llPazartesi.setAnimation(AnimationUtils.loadAnimation(this, R.anim.ustten_inme))
            llPazartesi.visibility = View.VISIBLE

        }
        imgPazartesiUp.setOnClickListener {
            imgPazartesiDown.visibility = View.VISIBLE
            imgPazartesiUp.visibility = View.GONE
            llPazartesi.visibility = View.GONE
        }

        imgFenerDown.setOnClickListener {
            imgFenerDown.visibility = View.GONE
            imgFenerUp.visibility = View.VISIBLE
            rcFener.visibility = View.VISIBLE
        }
        imgFenerUp.setOnClickListener {
            imgFenerDown.visibility = View.VISIBLE
            imgFenerUp.visibility = View.GONE
            rcFener.visibility = View.GONE
        }

        imgMesrDown.setOnClickListener {
            imgMesrDown.visibility = View.GONE
            imgMesrUp.visibility = View.VISIBLE
            rcMesr.visibility = View.VISIBLE
        }
        imgMesrUp.setOnClickListener {
            imgMesrDown.visibility = View.VISIBLE
            imgMesrUp.visibility = View.GONE
            rcMesr.visibility = View.GONE
        }

        imgRuzgarliDown.setOnClickListener {
            imgRuzgarliDown.visibility = View.GONE
            imgRuzgarliUp.visibility = View.VISIBLE
            rcRuzgarli.visibility = View.VISIBLE
        }
        imgRuzgarliUp.setOnClickListener {
            imgRuzgarliDown.visibility = View.VISIBLE
            imgRuzgarliUp.visibility = View.GONE
            rcRuzgarli.visibility = View.GONE
        }

        imgKaraDown.setOnClickListener {
            imgKaraDown.visibility = View.GONE
            imgKaraUp.visibility = View.VISIBLE
            rcKara.visibility = View.VISIBLE
        }
        imgKaraUp.setOnClickListener {
            imgKaraDown.visibility = View.VISIBLE
            imgKaraUp.visibility = View.GONE
            rcKara.visibility = View.GONE
        }

        //////////////////////////

        imgSaliDown.setOnClickListener {
            imgSaliDown.visibility = View.GONE
            imgSaliUp.visibility = View.VISIBLE
            llSali.visibility = View.VISIBLE
        }
        imgSaliUp.setOnClickListener {
            imgSaliDown.visibility = View.VISIBLE
            imgSaliUp.visibility = View.GONE
            llSali.visibility = View.GONE
        }

        imginagziDown.setOnClickListener {
            rcinagzi.setAnimation(AnimationUtils.loadAnimation(this, R.anim.scale))
            imginagziDown.visibility = View.GONE
            imginagziUp.visibility = View.VISIBLE
            rcinagzi.visibility = View.VISIBLE
        }
        imginagziUp.setOnClickListener {
            imginagziDown.visibility = View.VISIBLE
            imginagziUp.visibility = View.GONE
            rcinagzi.visibility = View.GONE
        }

        imgKilimliDown.setOnClickListener {
            imgKilimliDown.visibility = View.GONE
            imgKilimliUp.visibility = View.VISIBLE
            rcKilimli.visibility = View.VISIBLE
        }
        imgKilimliUp.setOnClickListener {
            imgKilimliDown.visibility = View.VISIBLE
            imgKilimliUp.visibility = View.GONE
            rcKilimli.visibility = View.GONE
        }

        imgKaradonDown.setOnClickListener {
            imgKaradonDown.visibility = View.GONE
            imgKaradonUp.visibility = View.VISIBLE
            rcKaradon.visibility = View.VISIBLE
        }
        imgKaradonUp.setOnClickListener {
            imgKaradonDown.visibility = View.VISIBLE
            imgKaradonUp.visibility = View.GONE
            rcKaradon.visibility = View.GONE
        }

        imgCatalDown.setOnClickListener {
            imgCatalDown.visibility = View.GONE
            imgCatalUp.visibility = View.VISIBLE
            rcCatal.visibility = View.VISIBLE
        }
        imgCatalUp.setOnClickListener {
            imgCatalDown.visibility = View.VISIBLE
            imgCatalUp.visibility = View.GONE
            rcCatal.visibility = View.GONE
        }


        //////////////////////////

        imgCarsambaDown.setOnClickListener {
            imgCarsambaDown.visibility = View.GONE
            imgCarsambaUp.visibility = View.VISIBLE
            llCarsamba.visibility = View.VISIBLE
        }
        imgCarsambaUp.setOnClickListener {
            imgCarsambaDown.visibility = View.VISIBLE
            imgCarsambaUp.visibility = View.GONE
            llCarsamba.visibility = View.GONE
        }

        imgTepebasiDown.setOnClickListener {
            imgTepebasiDown.visibility = View.GONE
            imgTepebasiUp.visibility = View.VISIBLE
            rcTepebasi.visibility = View.VISIBLE
        }
        imgTepebasiUp.setOnClickListener {
            imgTepebasiDown.visibility = View.VISIBLE
            imgTepebasiUp.visibility = View.GONE
            rcTepebasi.visibility = View.GONE
        }

        imgKapuzDown.setOnClickListener {
            imgKapuzDown.visibility = View.GONE
            imgKapuzUp.visibility = View.VISIBLE
            rcKapuz.visibility = View.VISIBLE
        }
        imgKapuzUp.setOnClickListener {
            imgKapuzDown.visibility = View.VISIBLE
            imgKapuzUp.visibility = View.GONE
            rcKapuz.visibility = View.GONE
        }

        imgYesilDown.setOnClickListener {
            imgYesilDown.visibility = View.GONE
            imgYesilUp.visibility = View.VISIBLE
            rcYesil.visibility = View.VISIBLE
        }
        imgYesilUp.setOnClickListener {
            imgYesilDown.visibility = View.VISIBLE
            imgYesilUp.visibility = View.GONE
            rcYesil.visibility = View.GONE
        }

        imgYeniDown.setOnClickListener {
            imgYeniDown.visibility = View.GONE
            imgYeniUp.visibility = View.VISIBLE
            rcYeni.visibility = View.VISIBLE
        }
        imgYeniUp.setOnClickListener {
            imgYeniDown.visibility = View.VISIBLE
            imgYeniUp.visibility = View.GONE
            rcYeni.visibility = View.GONE
        }

        imgBaglikDown.setOnClickListener {
            imgBaglikDown.visibility = View.GONE
            imgBaglikUp.visibility = View.VISIBLE
            rcBaglik.visibility = View.VISIBLE
        }
        imgBaglikUp.setOnClickListener {
            imgBaglikDown.visibility = View.VISIBLE
            imgBaglikUp.visibility = View.GONE
            rcBaglik.visibility = View.GONE
        }

        //////////////////////////


        imgPersDown.setOnClickListener {
            imgPersDown.visibility = View.GONE
            imgPersUp.visibility = View.VISIBLE
            llPersembe.visibility = View.VISIBLE
        }
        imgPersUp.setOnClickListener {
            imgPersDown.visibility = View.VISIBLE
            imgPersUp.visibility = View.GONE
            llPersembe.visibility = View.GONE
        }

        imgCaydamarDown.setOnClickListener {
            imgCaydamarDown.visibility = View.GONE
            imgCaydamarUp.visibility = View.VISIBLE
            rcCaydamar.visibility = View.VISIBLE
        }
        imgCaydamarUp.setOnClickListener {
            imgCaydamarDown.visibility = View.VISIBLE
            imgCaydamarUp.visibility = View.GONE
            rcCaydamar.visibility = View.GONE
        }

        imgKokaksuDown.setOnClickListener {
            imgKokaksuDown.visibility = View.GONE
            imgKokaksuUp.visibility = View.VISIBLE
            rcKokaksu.visibility = View.VISIBLE
        }
        imgKokaksuUp.setOnClickListener {
            imgKokaksuDown.visibility = View.VISIBLE
            imgKokaksuUp.visibility = View.GONE
            rcKokaksu.visibility = View.GONE
        }

        imgBirlikDown.setOnClickListener {
            imgBirlikDown.visibility = View.GONE
            imgBirlikUp.visibility = View.VISIBLE
            rcBirlik.visibility = View.VISIBLE
        }
        imgBirlikUp.setOnClickListener {
            imgBirlikDown.visibility = View.VISIBLE
            imgBirlikUp.visibility = View.GONE
            rcBirlik.visibility = View.GONE
        }

        imgMuftuDown.setOnClickListener {
            imgMuftuDown.visibility = View.GONE
            imgMuftuUp.visibility = View.VISIBLE
            rcMuftu.visibility = View.VISIBLE
        }
        imgMuftuUp.setOnClickListener {
            imgMuftuDown.visibility = View.VISIBLE
            imgMuftuUp.visibility = View.GONE
            rcMuftu.visibility = View.GONE
        }

        imgTemmuzDown.setOnClickListener {
            imgTemmuzDown.visibility = View.GONE
            imgTemmuzUp.visibility = View.VISIBLE
            rcTemmuz.visibility = View.VISIBLE
        }
        imgTemmuzUp.setOnClickListener {
            imgTemmuzDown.visibility = View.VISIBLE
            imgTemmuzUp.visibility = View.GONE
            rcTemmuz.visibility = View.GONE
        }

        imgSogukDown.setOnClickListener {
            imgSogukDown.visibility = View.GONE
            imgSogukUp.visibility = View.VISIBLE
            rcSoguk.visibility = View.VISIBLE
        }
        imgSogukUp.setOnClickListener {
            imgSogukDown.visibility = View.VISIBLE
            imgSogukUp.visibility = View.GONE
            rcSoguk.visibility = View.GONE
        }

        //////////////////////////


        imgCumaDown.setOnClickListener {
            imgCumaDown.visibility = View.GONE
            imgCumaUp.visibility = View.VISIBLE
            llCuma.visibility = View.VISIBLE
        }
        imgCumaUp.setOnClickListener {
            imgCumaDown.visibility = View.VISIBLE
            imgCumaUp.visibility = View.GONE
            llCuma.visibility = View.GONE
        }

        imgKavakDown.setOnClickListener {
            imgKavakDown.visibility = View.GONE
            imgKavakUp.visibility = View.VISIBLE
            rcKavak.visibility = View.VISIBLE
        }
        imgKavakUp.setOnClickListener {
            imgKavakDown.visibility = View.VISIBLE
            imgKavakUp.visibility = View.GONE
            rcKavak.visibility = View.GONE
        }

        imgKozluDown.setOnClickListener {
            imgKozluDown.visibility = View.GONE
            imgKozluUp.visibility = View.VISIBLE
            rcKozlu.visibility = View.VISIBLE
        }
        imgKozluUp.setOnClickListener {
            imgKozluDown.visibility = View.VISIBLE
            imgKozluUp.visibility = View.GONE
            rcKozlu.visibility = View.GONE
        }

        imgTıpDown.setOnClickListener {
            imgTıpDown.visibility = View.GONE
            imgTıpUp.visibility = View.VISIBLE
            rcTip.visibility = View.VISIBLE
        }
        imgTıpUp.setOnClickListener {
            imgTıpDown.visibility = View.VISIBLE
            imgTıpUp.visibility = View.GONE
            rcTip.visibility = View.GONE
        }

        /////////////////////


        imgCumartesiDown.setOnClickListener {
            imgCumartesiDown.visibility = View.GONE
            imgCumartesiUp.visibility = View.VISIBLE
            llCumartesi.visibility = View.VISIBLE
        }
        imgCumartesiUp.setOnClickListener {
            imgCumartesiDown.visibility = View.VISIBLE
            imgCumartesiUp.visibility = View.GONE
            llCumartesi.visibility = View.GONE
        }

        imgSiteDown.setOnClickListener {
            imgSiteDown.visibility = View.GONE
            imgSiteUp.visibility = View.VISIBLE
            rcSite.visibility = View.VISIBLE
        }
        imgSiteUp.setOnClickListener {
            imgSiteDown.visibility = View.VISIBLE
            imgSiteUp.visibility = View.GONE
            rcSite.visibility = View.GONE
        }

        imginciDown.setOnClickListener {
            imginciDown.visibility = View.GONE
            imginciUp.visibility = View.VISIBLE
            rcinci.visibility = View.VISIBLE
        }
        imginciUp.setOnClickListener {
            imginciDown.visibility = View.VISIBLE
            imginciUp.visibility = View.GONE
            rcinci.visibility = View.GONE
        }
        /////////////////////


        imgileriDown.setOnClickListener {
            imgileriDown.visibility = View.GONE
            imgileriUp.visibility = View.VISIBLE
            rcileriTarih.visibility = View.VISIBLE
        }
        imgileriUp.setOnClickListener {
            imgileriDown.visibility = View.VISIBLE
            imgileriUp.visibility = View.GONE
            rcileriTarih.visibility = View.GONE
        }
        /////////////////////


    }

    private fun setupListeler() {
        fenerList = ArrayList()
        mesrList = ArrayList()
        ruzgarliList = ArrayList()
        karaElmasList = ArrayList()
        inAgziList = ArrayList()
        kilimliList = ArrayList()
        karadonList = ArrayList()
        catalList = ArrayList()
        tepeBasiList = ArrayList()
        kapuzList = ArrayList()
        yesilList = ArrayList()
        yeniList = ArrayList()
        baglikList = ArrayList()
        cayDamarList = ArrayList()
        kokaksuList = ArrayList()
        birlikList = ArrayList()
        muftuList = ArrayList()
        ontemmuzList = ArrayList()
        sogukList = ArrayList()
        kavaklikList = ArrayList()
        kozluList = ArrayList()
        tipList = ArrayList()
        siteList = ArrayList()
        inciList = ArrayList()
        ilerilist = ArrayList()
    }

    private fun konumIzni() {
        Dexter.withActivity(this).withPermissions(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.INTERNET
        )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {


                    if (report!!.areAllPermissionsGranted()) {

                        Log.e("izin", "izinler Tamam")
                        //  Toast.makeText(this@SiparislerActivity,"İzinler Tamam",Toast.LENGTH_SHORT).show()

                    }

                    if (report!!.isAnyPermissionPermanentlyDenied) {

                        Toast.makeText(this@SiparislerActivity, "İzinleri kontrol et", Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?, token: PermissionToken?) {

                }


            }).check()
    }

    private fun zamanAyarı() {
        // FirebaseMessaging.getInstance().subscribeToTopic("msgNotification");
        FirebaseDatabase.getInstance().reference.child("Zaman").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                var gece3 = p0.child("gece3").value.toString().toLong()
                var suankıZaman = System.currentTimeMillis()

                if (gece3 < suankıZaman) {

                    var guncelGece3 = gece3 + 86400000

                    FirebaseDatabase.getInstance().reference.child("Zaman").child("gece3").setValue(guncelGece3).addOnCompleteListener {
                        FirebaseDatabase.getInstance().reference.child("Zaman").child("gerigece3").setValue(gece3)
                    }

                }
            }
        })
    }

    fun recyclerView(recyclerView: RecyclerView, siparisListesi: ArrayList<SiparisData>) {
        recyclerView.layoutManager = LinearLayoutManager(this@SiparislerActivity, LinearLayoutManager.VERTICAL, false)
        val Adapter = SiparisAdapter(this@SiparislerActivity, siparisListesi, kullaniciAdi.toString())

        recyclerView.adapter = Adapter
        recyclerView.setHasFixedSize(true)
    }


    fun recyclerViewileriTarihli() {
        rcileriTarih.layoutManager = LinearLayoutManager(this@SiparislerActivity, LinearLayoutManager.VERTICAL, false)
        val Adapter = SiparisAdapter(this@SiparislerActivity, ilerilist, kullaniciAdi.toString())
        rcileriTarih.adapter = Adapter
        rcileriTarih.setHasFixedSize(true)
    }


    fun setupNavigationView() {

        BottomNavigationViewHelper.setupBottomNavigationView(bottomNav)
        BottomNavigationViewHelper.setupNavigation(this, bottomNav) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNav.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }

    fun setupKullaniciAdi() {
        FirebaseDatabase.getInstance().reference.child("users").child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                kullaniciAdi = p0.child("user_name").value.toString()
            }

        })
    }

    private fun initMyAuthStateListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                val kullaniciGirisi = p0.currentUser
                if (kullaniciGirisi != null) { //eğer kişi giriş yaptıysa nul gorunmez. giriş yapmadıysa null olur
                } else {
                    startActivity(Intent(this@SiparislerActivity, LoginActivity::class.java))
                }
            }
        }
    }


}
