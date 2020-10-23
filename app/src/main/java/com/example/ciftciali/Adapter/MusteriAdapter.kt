package com.example.ciftciali.Adapter

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ciftciali.AdresBulmaMapsActivity
import com.example.ciftciali.Datalar.MusteriData
import com.example.ciftciali.Datalar.SiparisData
import com.example.ciftciali.R
import com.example.ciftciali.TimeAgo
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.dialog_gidilen_musteri.view.*
import kotlinx.android.synthetic.main.dialog_siparis_ekle.view.*
import kotlinx.android.synthetic.main.item_musteri.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MusteriAdapter(val myContext: Context, val musteriler: ArrayList<MusteriData>, val kullaniciAdi: String?) : RecyclerView.Adapter<MusteriAdapter.MusteriHolder>() {

    lateinit var dialogViewSp: View
    lateinit var dialogMsDznle: Dialog

    var ref = FirebaseDatabase.getInstance().reference
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusteriHolder {
        val myView = LayoutInflater.from(myContext).inflate(R.layout.item_musteri, parent, false)

        return MusteriHolder(myView)
    }

    override fun getItemCount(): Int {
        return musteriler.size
    }

    override fun onBindViewHolder(holder: MusteriHolder, position: Int) {
        var item = musteriler[position]
        holder.setData(musteriler[position])

        holder.btnSiparisEkle.setOnClickListener {


            var builder: AlertDialog.Builder = AlertDialog.Builder(this.myContext)
            dialogViewSp = View.inflate(myContext, R.layout.dialog_siparis_ekle, null)


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
                DatePickerDialog(
                    myContext, dateSetListener, cal.get(Calendar.YEAR), cal.get(
                        Calendar.MONTH
                    ), cal.get(Calendar.DAY_OF_MONTH)
                ).show()
                TimePickerDialog(
                    myContext, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(
                        Calendar.MINUTE
                    ), true
                ).show()
            }

            dialogViewSp.tablePeynirler.visibility = View.GONE
            dialogViewSp.tableDiger.visibility = View.GONE
            dialogViewSp.peynirAsagi.setOnClickListener {
                dialogViewSp.tablePeynirler.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.ustten_inme))
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
                dialogViewSp.tableDiger.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.ustten_inme))
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
                        item.musteri_zkonum, item.musteri_adres, item.musteri_apartman, siparisKey, item.musteri_mah, siparisNotu, item.musteri_tel, cal.timeInMillis, System.currentTimeMillis(),
                        item.musteri_ad_soyad, System.currentTimeMillis(), kullaniciAdi.toString(), sucuk, sucukF, sutCokelegi, sutCokelegiFiyat, yogurt, yogurt_fiyat, yogurt3, yogurt3_fiyat,
                        yumurta, yumurta_fiyat, beyazPey, beyazPeyF, beyazPey1000, beyazPey1000F, cigSut, cigSutF, cokertmePey, cokertmePeyF, dilPey, dilPeyF,
                        kangal, kangalF, kasar400, kasar400Fiyat, kasar600, kasar600Fiyat, kavurma, kavurmaF, kefir, kefirF, item.musteri_zlat, item.musteri_zlong
                    )


                    ref.child("Siparisler").child(siparisKey).setValue(siparisData)
                    ref.child("Siparisler").child(siparisKey).child("siparis_zamani").setValue(ServerValue.TIMESTAMP)
                    ref.child("Siparisler").child(siparisKey).child("siparis_teslim_zamani").setValue(ServerValue.TIMESTAMP)
                    ref.child("Musteriler").child(musteriler[position].musteri_ad_soyad.toString()).child("siparisleri").child(siparisKey).setValue(siparisData)
                    ref.child("Musteriler").child(musteriler[position].musteri_ad_soyad.toString()).child("siparisleri").child(siparisKey).child("siparis_teslim_zamani").setValue(ServerValue.TIMESTAMP)
                    ref.child("Musteriler").child(musteriler[position].musteri_ad_soyad.toString()).child("siparisleri").child(siparisKey).child("siparis_zamani").setValue(ServerValue.TIMESTAMP)

                }
            })

            builder.setTitle(musteriler[position].musteri_ad_soyad)
            builder.setIcon(R.drawable.cow)

            builder.setView(dialogViewSp)
            var dialog: Dialog = builder.create()
            dialog.show()

        }

        holder.itemView.setOnLongClickListener {
            val popup = PopupMenu(myContext, holder.itemView)
            popup.inflate(R.menu.popup_menu_musteri)
            popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                when (it.itemId) {
                    R.id.popHizliSiparis -> {
                        var builder: AlertDialog.Builder = AlertDialog.Builder(this.myContext)
                        dialogViewSp = View.inflate(myContext, R.layout.dialog_siparis_ekle, null)


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
                            DatePickerDialog(
                                myContext, dateSetListener, cal.get(Calendar.YEAR), cal.get(
                                    Calendar.MONTH
                                ), cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                            TimePickerDialog(
                                myContext, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(
                                    Calendar.MINUTE
                                ), true
                            ).show()
                        }

                        dialogViewSp.tablePeynirler.visibility = View.GONE
                        dialogViewSp.tableDiger.visibility = View.GONE
                        dialogViewSp.peynirAsagi.setOnClickListener {
                            dialogViewSp.tablePeynirler.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.ustten_inme))
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
                            dialogViewSp.tableDiger.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.ustten_inme))
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
                                    item.musteri_zkonum, item.musteri_adres, item.musteri_apartman, siparisKey, item.musteri_mah, siparisNotu, item.musteri_tel, cal.timeInMillis, System.currentTimeMillis(),
                                    item.musteri_ad_soyad, System.currentTimeMillis(), kullaniciAdi.toString(), sucuk, sucukF, sutCokelegi, sutCokelegiFiyat, yogurt, yogurt_fiyat, yogurt3, yogurt3_fiyat,
                                    yumurta, yumurta_fiyat, beyazPey, beyazPeyF, beyazPey1000, beyazPey1000F, cigSut, cigSutF, cokertmePey, cokertmePeyF, dilPey, dilPeyF,
                                    kangal, kangalF, kasar400, kasar400Fiyat, kasar600, kasar600Fiyat, kavurma, kavurmaF, kefir, kefirF, item.musteri_zlat, item.musteri_zlong
                                )
                                ref.child("Teslim_siparisler").child(siparisKey).setValue(siparisData)
                                ref.child("Musteriler").child(musteriler[position].musteri_ad_soyad.toString()).child("siparisleri").child(siparisKey).setValue(siparisData)


                            }
                        })

                        builder.setTitle(musteriler[position].musteri_ad_soyad)
                        builder.setIcon(R.drawable.cow)

                        builder.setView(dialogViewSp)
                        var dialog: Dialog = builder.create()
                        dialog.show()
                    }
                    R.id.popSiparisGir -> {
                        var builder: AlertDialog.Builder = AlertDialog.Builder(this.myContext)
                        dialogViewSp = View.inflate(myContext, R.layout.dialog_siparis_ekle, null)


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
                            DatePickerDialog(
                                myContext, dateSetListener, cal.get(Calendar.YEAR), cal.get(
                                    Calendar.MONTH
                                ), cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                            TimePickerDialog(
                                myContext, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(
                                    Calendar.MINUTE
                                ), true
                            ).show()
                        }

                        dialogViewSp.tablePeynirler.visibility = View.GONE
                        dialogViewSp.tableDiger.visibility = View.GONE
                        dialogViewSp.peynirAsagi.setOnClickListener {
                            dialogViewSp.tablePeynirler.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.ustten_inme))
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
                            dialogViewSp.tableDiger.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.ustten_inme))
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
                                    item.musteri_zkonum, item.musteri_adres, item.musteri_apartman, siparisKey, item.musteri_mah, siparisNotu, item.musteri_tel, cal.timeInMillis, System.currentTimeMillis(),
                                    item.musteri_ad_soyad, System.currentTimeMillis(), kullaniciAdi.toString(), sucuk, sucukF, sutCokelegi, sutCokelegiFiyat, yogurt, yogurt_fiyat, yogurt3, yogurt3_fiyat,
                                    yumurta, yumurta_fiyat, beyazPey, beyazPeyF, beyazPey1000, beyazPey1000F, cigSut, cigSutF, cokertmePey, cokertmePeyF, dilPey, dilPeyF,
                                    kangal, kangalF, kasar400, kasar400Fiyat, kasar600, kasar600Fiyat, kavurma, kavurmaF, kefir, kefirF, item.musteri_zlat, item.musteri_zlong
                                )
                                ref.child("Siparisler").child(siparisKey).setValue(siparisData)
                                ref.child("Siparisler").child(siparisKey).child("siparis_zamani").setValue(ServerValue.TIMESTAMP)
                                ref.child("Siparisler").child(siparisKey).child("siparis_teslim_zamani").setValue(ServerValue.TIMESTAMP)
                                ref.child("Musteriler").child(musteriler[position].musteri_ad_soyad.toString()).child("siparisleri").child(siparisKey).setValue(siparisData)
                                ref.child("Musteriler").child(musteriler[position].musteri_ad_soyad.toString()).child("siparisleri").child(siparisKey).child("siparis_teslim_zamani").setValue(ServerValue.TIMESTAMP)
                                ref.child("Musteriler").child(musteriler[position].musteri_ad_soyad.toString()).child("siparisleri").child(siparisKey).child("siparis_zamani").setValue(ServerValue.TIMESTAMP)


                            }
                        })

                        builder.setTitle(musteriler[position].musteri_ad_soyad)
                        builder.setIcon(R.drawable.cow)

                        builder.setView(dialogViewSp)
                        var dialog: Dialog = builder.create()
                        dialog.show()
                    }
                    R.id.popDüzenle -> {
                        var musteriAdi = musteriler[position].musteri_ad_soyad.toString()
                        var builder: AlertDialog.Builder = AlertDialog.Builder(myContext)

                        var dialogView: View =
                            View.inflate(myContext, R.layout.dialog_gidilen_musteri, null)
                        builder.setView(dialogView)

                        dialogMsDznle = builder.create()

                        dialogView.swKonumKaydet.setOnClickListener {
                            val intent = Intent(myContext, AdresBulmaMapsActivity::class.java)
                            intent.putExtra("musteriAdi", musteriAdi)
                            myContext.startActivity(intent)

                        }

                        dialogView.imgCheck.setOnClickListener {
                            if (dialogView.etAdresGidilen.text.toString().isNotEmpty() && dialogView.etTelefonGidilen.text.toString().isNotEmpty()) {
                                var adres = dialogView.etAdresGidilen.text.toString()
                                var telefon = dialogView.etTelefonGidilen.text.toString()
                                var apartman = dialogView.etApartman.text.toString()

                                FirebaseDatabase.getInstance().reference.child("Musteriler").child(musteriAdi).child("musteri_adres").setValue(adres)
                                FirebaseDatabase.getInstance().reference.child("Musteriler").child(musteriAdi).child("musteri_apartman").setValue(apartman)
                                FirebaseDatabase.getInstance().reference.child("Musteriler").child(musteriAdi).child("musteri_tel").setValue(telefon).addOnCompleteListener {
///locationsu durduruyruz
                                    holder.locationManager.removeUpdates(holder.myLocationListener)
                                    dialogMsDznle.dismiss()

                                    Toast.makeText(myContext, "Müşteri Bilgileri Güncellendi", Toast.LENGTH_LONG).show()
                                }.addOnFailureListener { Toast.makeText(myContext, "Müşteri Bilgileri Güncellenemedi", Toast.LENGTH_LONG).show() }
                            } else {
                                Toast.makeText(myContext, "Bilgilerde boşluklar var", Toast.LENGTH_LONG).show()
                            }
                        }

                        dialogView.imgBack.setOnClickListener {
                            holder.locationManager.removeUpdates(holder.myLocationListener)
                            dialogMsDznle.dismiss()
                        }

                        dialogView.tvAdSoyad.text = musteriler[position].musteri_ad_soyad.toString()
                        dialogView.tvMahalle.text = musteriler[position].musteri_mah.toString() + " Mahallesi"
                        dialogView.etApartman.setText(musteriler[position].musteri_apartman.toString())
                        ref.child("Musteriler").child(musteriAdi).addListenerForSingleValueEvent(object : ValueEventListener {
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
                                    dialogView.rcSiparisGidilen.layoutManager = LinearLayoutManager(myContext, LinearLayoutManager.VERTICAL, false)
                                    val Adapter =
                                        MusteriSiparisleriAdapter(
                                            myContext,
                                            list
                                        )
                                    dialogView.rcSiparisGidilen.adapter = Adapter
                                    dialogView.rcSiparisGidilen.setHasFixedSize(true)
                                }
                            }
                        })
                        dialogMsDznle.setCancelable(false)
                        dialogMsDznle.show()

                    }
                    R.id.popSil -> {
                        var alert = AlertDialog.Builder(myContext)
                            .setTitle("Müşteriyi Sil")
                            .setMessage("Emin Misin ?")
                            .setPositiveButton("Sil", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    FirebaseDatabase.getInstance().reference.child("Musteriler").child(musteriler[position].musteri_ad_soyad.toString()).removeValue()
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
            return@setOnLongClickListener true
        }
    }

    inner class MusteriHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var musteriAdi = itemView.tvMusteriAdi
        var btnSiparisEkle = itemView.tvSiparisEkle
        var sonSiparisZamani = itemView.tvMusteriSonZaman
        var swKonumKaydet = itemView.swKonumKaydet

        var musteriAdiGnl = ""
        fun setData(musteriData: MusteriData) {
            musteriAdiGnl = musteriData.musteri_ad_soyad.toString()
            musteriAdi.text = musteriData.musteri_ad_soyad
            sonSiparisZamani.text = TimeAgo.getTimeAgo(musteriData.siparis_son_zaman!!).toString()
        }


        var locationManager = myContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        @SuppressLint("MissingPermission")
        fun getLocation(musteriAdi: String) {
            if (isLocationEnabled(myContext)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1500, 0f, myLocationListener)
            } else {
                Toast.makeText(myContext, "Konumu Açın", Toast.LENGTH_LONG).show()
                dialogMsDznle.dismiss()
            }
        }

        private fun isLocationEnabled(mContext: Context): Boolean {
            val lm = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        }

        val myLocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                var Lat = location!!.latitude
                var Long = location!!.longitude

                FirebaseDatabase.getInstance().reference.child("Musteriler").child(musteriAdiGnl).child("musteri_zlat").setValue(Lat)
                FirebaseDatabase.getInstance().reference.child("Musteriler").child(musteriAdiGnl).child("musteri_zlong").setValue(Long).addOnCompleteListener {
                    Toast.makeText(myContext, "Konum Kaydedildi.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                TODO("Not yet implemented")
            }

            override fun onProviderEnabled(provider: String?) {
                TODO("Not yet implemented")
            }

            override fun onProviderDisabled(provider: String?) {
                TODO("Not yet implemented")
            }
        }


    }


}
