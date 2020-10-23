package com.example.ciftciali.Adapter


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ciftciali.Datalar.SiparisData
import com.example.ciftciali.R
import com.example.ciftciali.SiparislerActivity
import com.example.ciftciali.TimeAgo

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import kotlinx.android.synthetic.main.dialog_siparis_ekle.view.*
import kotlinx.android.synthetic.main.item_siparisler.view.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SiparisAdapter(val myContext: Context, val siparisler: ArrayList<SiparisData>, val kullaniciAdi: String?) : RecyclerView.Adapter<SiparisAdapter.SiparisHolder>() {

    val ref = FirebaseDatabase.getInstance().reference
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SiparisAdapter.SiparisHolder {
        val view = LayoutInflater.from(myContext).inflate(R.layout.item_siparisler, parent, false)


        return SiparisHolder(view)
    }

    override fun getItemCount(): Int {

        return siparisler.size
    }

    override fun onBindViewHolder(holder: SiparisAdapter.SiparisHolder, position: Int) {
        var item = siparisler[position]
        holder.setData(siparisler[position])


        holder.itemView.setOnLongClickListener {

            val popup = PopupMenu(myContext, holder.itemView)
            popup.inflate(R.menu.popup_menu)
            popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                when (it.itemId) {
                    R.id.popTeslim -> {
                        var alert = AlertDialog.Builder(myContext)
                            .setTitle("Sipariş Teslim Edildi")
                            .setMessage("Emin Misin ?")
                            .setPositiveButton("Onayla", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {


                                    var key = siparisler[position].siparis_key.toString()
                                    ref.child("Musteriler").child(siparisler[position].siparis_veren.toString()).child("siparisleri").child(key).setValue(item)

                                    ref.child("Teslim_siparisler").child(key).setValue(item)
                                        .addOnCompleteListener {

                                            myContext.startActivity(Intent(myContext, SiparislerActivity::class.java))
                                            Toast.makeText(myContext, "Sipariş Teslim Edildi... Sayfayı Yenileyiniz..", Toast.LENGTH_LONG).show()

                                            ref.child("Siparisler").child(key).removeValue()
                                            ref.child("Teslim_siparisler").child(key).child("siparis_teslim_zamani").setValue(ServerValue.TIMESTAMP)
                                            ref.child("Musteriler").child(siparisler[position].siparis_veren.toString()).child("siparis_son_zaman").setValue(ServerValue.TIMESTAMP)
                                        }


                                }
                            })
                            .setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    p0!!.dismiss()
                                }
                            }).create()

                        alert.show()

                    }
                    R.id.popDüzenle -> {
                        var builder: AlertDialog.Builder = AlertDialog.Builder(this.myContext)

                        var viewDuzenle: View = View.inflate(myContext, R.layout.dialog_siparis_ekle, null)

                        builder.setTitle(siparisler[position].siparis_veren)
                        builder.setIcon(R.drawable.cow)

                        viewDuzenle.etCokertmePeyniri.setText(siparisler[position].yycokertme_pey);viewDuzenle.etCokertmePeyniriFiyat.setText(siparisler[position].yycokertme_pey_fiyat.toString())
                        viewDuzenle.etDilPeyniri.setText(siparisler[position].yydil_pey);viewDuzenle.etDilPeyniriFiyat.setText(siparisler[position].yydil_pey_fiyat.toString());
                        viewDuzenle.etBeyazPeynir.setText(siparisler[position].yybeyaz_pey);viewDuzenle.etBeyazPeynirFiyat.setText(siparisler[position].yybeyaz_pey_fiyat.toString())
                        viewDuzenle.etBeyazPeynir1000.setText(siparisler[position].yybeyaz_pey1000);viewDuzenle.etBeyazPeynir1000Fiyat.setText(siparisler[position].yybeyaz_pey1000_fiyat.toString())
                        viewDuzenle.et400Kasar.setText(siparisler[position].yykasar_400);viewDuzenle.et400KasarFiyat.setText(siparisler[position].yykasar_400_fiyat.toString())
                        viewDuzenle.et600Kasar.setText(siparisler[position].yykasar_600);viewDuzenle.et600KasarFiyat.setText(siparisler[position].yykasar_600_fiyat.toString())
                        viewDuzenle.etSutCokelegi.setText(siparisler[position].sut_cokelegi);viewDuzenle.etSutCokelegiFiyat.setText(siparisler[position].sut_cokelegi_fiyat.toString())
                        viewDuzenle.etYumurta.setText(siparisler[position].yumurta);viewDuzenle.etYumurtaFiyat.setText(siparisler[position].yumurta_fiyat.toString())
                        viewDuzenle.etYogurt.setText(siparisler[position].yogurt);viewDuzenle.etYogurtFiyat.setText(siparisler[position].yogurt_fiyat.toString())
                        viewDuzenle.etYogurt3.setText(siparisler[position].yogurt3);viewDuzenle.etYogurt3Fiyat.setText(siparisler[position].yogurt3_fiyat.toString())
                        viewDuzenle.etCigsut.setText(siparisler[position].yycig_sut);viewDuzenle.etCigsutFiyat.setText(siparisler[position].yycig_sut_fiyat.toString())
                        viewDuzenle.etKefir.setText(siparisler[position].yykefir);viewDuzenle.etKefirFiyat.setText(siparisler[position].yykefir_fiyat.toString())
                        viewDuzenle.etKangal.setText(siparisler[position].yykangal_sucuk);viewDuzenle.etKangalFiyat.setText(siparisler[position].yykangal_sucuk_fiyat.toString());
                        viewDuzenle.etSucuk.setText(siparisler[position].sucuk);viewDuzenle.etSucukFiyat.setText(siparisler[position].sucuk_fiyat.toString())
                        viewDuzenle.etKavurma.setText(siparisler[position].yykavurma);viewDuzenle.etKavurmaFiyat.setText(siparisler[position].yykavurma_fiyat.toString())



                        viewDuzenle.etSiparisNotu.setText(siparisler[position].siparis_notu)

                        viewDuzenle.tvZamanEkleDialog.text = SimpleDateFormat("HH:mm dd.MM.yyyy").format(System.currentTimeMillis())
                        var cal = Calendar.getInstance()
                        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            cal.set(Calendar.YEAR, year)
                            cal.set(Calendar.MONTH, monthOfYear)
                            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)


                            val myFormat = "HH:mm dd.MM.yyyy" // mention the format you need
                            val sdf = SimpleDateFormat(myFormat, Locale("tr"))
                            viewDuzenle.tvZamanEkleDialog.text = sdf.format(cal.time)
                        }

                        val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                            cal.set(Calendar.HOUR_OF_DAY, hourOfDay); cal.set(Calendar.MINUTE, minute)
                        }

                        viewDuzenle.tvZamanEkleDialog.setOnClickListener {
                            DatePickerDialog(myContext, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                            TimePickerDialog(myContext, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
                        }
                        builder.setView(viewDuzenle)


                        builder.setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                dialog!!.dismiss()
                            }
                        })

                        builder.setPositiveButton("Güncelle", object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {

                                var kasar400 = "0"
                                if (viewDuzenle.et400Kasar.text.toString().isNotEmpty()) kasar400 = viewDuzenle.et400Kasar.text.toString()
                                var kasar400Fiyat = 0.0
                                if (viewDuzenle.et400KasarFiyat.text.toString().isNotEmpty()) kasar400Fiyat = viewDuzenle.et400KasarFiyat.text.toString().toDouble()

                                var kasar600 = "0"
                                if (viewDuzenle.et600Kasar.text.toString().isNotEmpty()) kasar600 = viewDuzenle.et600Kasar.text.toString()

                                var kasar600Fiyat = 0.0
                                if (viewDuzenle.et600KasarFiyat.text.toString().isNotEmpty()) kasar600Fiyat = viewDuzenle.et600KasarFiyat.text.toString().toDouble()

                                var yumurta = "0"
                                if (viewDuzenle.etYumurta.text.toString().isNotEmpty()) {
                                    yumurta = viewDuzenle.etYumurta.text.toString()
                                }
                                var yumurta_fiyat = 0.0
                                if (viewDuzenle.etYumurtaFiyat.text.toString().isNotEmpty()) {
                                    yumurta_fiyat = viewDuzenle.etYumurtaFiyat.text.toString().toDouble()
                                }
                                var yogurt = "0"
                                if (viewDuzenle.etYogurt.text.toString().isNotEmpty()) {
                                    yogurt = viewDuzenle.etYogurt.text.toString()
                                }
                                var yogurt_fiyat = 0.0
                                if (viewDuzenle.etYogurtFiyat.text.toString().isNotEmpty()) {
                                    yogurt_fiyat = viewDuzenle.etYogurtFiyat.text.toString().toDouble()
                                }
                                var yogurt3 = "0"
                                if (viewDuzenle.etYogurt3.text.toString().isNotEmpty()) {
                                    yogurt3 = viewDuzenle.etYogurt3.text.toString()
                                }
                                var yogurt3_fiyat = 0.0
                                if (viewDuzenle.etYogurt3Fiyat.text.toString().isNotEmpty()) {
                                    yogurt3_fiyat = viewDuzenle.etYogurt3Fiyat.text.toString().toDouble()
                                }
                                var sutCokelegi = "0"
                                if (viewDuzenle.etSutCokelegi.text.toString().isNotEmpty()) {
                                    sutCokelegi = viewDuzenle.etSutCokelegi.text.toString()
                                }
                                var sutCokelegiFiyat = 0.0
                                if (viewDuzenle.etSutCokelegiFiyat.text.toString().isNotEmpty()) {
                                    sutCokelegiFiyat = viewDuzenle.etSutCokelegiFiyat.text.toString().toDouble()
                                }
                                var cokertmePey = "0"
                                if (viewDuzenle.etCokertmePeyniri.text.toString().isNotEmpty()) {
                                    cokertmePey = viewDuzenle.etCokertmePeyniri.text.toString()
                                }
                                var cokertmePeyF = 0.0
                                if (viewDuzenle.etCokertmePeyniriFiyat.text.toString().isNotEmpty()) {
                                    cokertmePeyF = viewDuzenle.etCokertmePeyniriFiyat.text.toString().toDouble()
                                }
                                var dilPey = "0"
                                if (viewDuzenle.etDilPeyniri.text.toString().isNotEmpty()) {
                                    dilPey = viewDuzenle.etDilPeyniri.text.toString()
                                }
                                var dilPeyF = 0.0
                                if (viewDuzenle.etDilPeyniriFiyat.text.toString().isNotEmpty()) {
                                    dilPeyF = viewDuzenle.etDilPeyniriFiyat.text.toString().toDouble()
                                }
                                var beyazPey = "0"
                                if (viewDuzenle.etBeyazPeynir.text.toString().isNotEmpty()) {
                                    beyazPey = viewDuzenle.etBeyazPeynir.text.toString()
                                }
                                var beyazPeyF = 0.0
                                if (viewDuzenle.etBeyazPeynirFiyat.text.toString().isNotEmpty()) {
                                    beyazPeyF = viewDuzenle.etBeyazPeynirFiyat.text.toString().toDouble()
                                }
                                var beyazPey1000 = "0"
                                if (viewDuzenle.etBeyazPeynir1000.text.toString().isNotEmpty()) {
                                    beyazPey1000 = viewDuzenle.etBeyazPeynir1000.text.toString()
                                }
                                var beyazPey1000F = 0.0
                                if (viewDuzenle.etBeyazPeynir1000Fiyat.text.toString().isNotEmpty()) {
                                    beyazPey1000F = viewDuzenle.etBeyazPeynir1000Fiyat.text.toString().toDouble()
                                }
                                var cigSut = "0"
                                if (viewDuzenle.etCigsut.text.toString().isNotEmpty()) {
                                    cigSut = viewDuzenle.etCigsut.text.toString()
                                }
                                var cigSutF = 0.0
                                if (viewDuzenle.etCigsutFiyat.text.toString().isNotEmpty()) {
                                    cigSutF = viewDuzenle.etCigsutFiyat.text.toString().toDouble()
                                }
                                var kangal = "0"
                                if (viewDuzenle.etKangal.text.toString().isNotEmpty()) {
                                    kangal = viewDuzenle.etKangal.text.toString()
                                }
                                var kangalF = 0.0
                                if (viewDuzenle.etKangalFiyat.text.toString().isNotEmpty()) {
                                    kangalF = viewDuzenle.etKangalFiyat.text.toString().toDouble()
                                }
                                var sucuk = "0"
                                if (viewDuzenle.etSucuk.text.toString().isNotEmpty()) {
                                    sucuk = viewDuzenle.etSucuk.text.toString()
                                }
                                var sucukF = 0.0
                                if (viewDuzenle.etSucukFiyat.text.toString().isNotEmpty()) {
                                    sucukF = viewDuzenle.etSucukFiyat.text.toString().toDouble()
                                }
                                var kavurma = "0"
                                if (viewDuzenle.etKavurma.text.toString().isNotEmpty()) {
                                    kavurma = viewDuzenle.etKavurma.text.toString()
                                }
                                var kavurmaF = 0.0
                                if (viewDuzenle.etKavurmaFiyat.text.toString().isNotEmpty()) {
                                    kavurmaF = viewDuzenle.etKavurmaFiyat.text.toString().toDouble()
                                }
                                var kefir = "0"
                                if (viewDuzenle.etKefir.text.toString().isNotEmpty()) {
                                    kefir = viewDuzenle.etKefir.text.toString()
                                }
                                var kefirF = 0.0
                                if (viewDuzenle.etKefirFiyat.text.toString().isNotEmpty()) {
                                    kefirF = viewDuzenle.etKefirFiyat.text.toString().toDouble()
                                }


                                var siparisNotu = viewDuzenle.etSiparisNotu.text.toString()

                                var siparisData = SiparisData(
                                    item.musteri_zkonum, item.siparis_adres, item.siparis_apartman, item.siparis_key, item.siparis_mah, siparisNotu, item.siparis_tel, cal.timeInMillis, System.currentTimeMillis(),
                                    item.siparis_veren, System.currentTimeMillis(), kullaniciAdi.toString(), sucuk, sucukF, sutCokelegi, sutCokelegiFiyat, yogurt, yogurt_fiyat, yogurt3, yogurt3_fiyat,
                                    yumurta, yumurta_fiyat, beyazPey, beyazPeyF, beyazPey1000, beyazPey1000F, cigSut, cigSutF, cokertmePey, cokertmePeyF, dilPey, dilPeyF,
                                    kangal, kangalF, kasar400, kasar400Fiyat, kasar600, kasar600Fiyat, kavurma, kavurmaF, kefir, kefirF, item.musteri_zlat, item.musteri_zlong
                                )
                                ref.child("Siparisler").child(item.siparis_key.toString()).setValue(siparisData)
                                ref.child("Musteriler").child(item.siparis_veren!!).child("siparisleri").setValue(siparisData)

                                var intent = Intent(myContext, SiparislerActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                myContext.startActivity(intent)


                            }
                        })

                        var dialog: Dialog = builder.create()

                        dialog.show()
                    }
                    R.id.popSil -> {

                        var alert = AlertDialog.Builder(myContext)
                            .setTitle("Siparişi Sil")
                            .setMessage("Emin Misin ?")
                            .setPositiveButton("Sil", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {

                                    FirebaseDatabase.getInstance().reference.child("Siparisler").child(siparisler[position].siparis_key.toString()).removeValue().addOnCompleteListener {
                                        Toast.makeText(myContext, "Sipariş Silindi...", Toast.LENGTH_LONG).show()
                                    }

                                    FirebaseDatabase.getInstance().reference.child("Musteriler").child(siparisler[position].siparis_veren.toString()).child("siparisleri")
                                        .child(siparisler[position].siparis_key.toString()).removeValue()

                                    myContext.startActivity(Intent(myContext, SiparislerActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))

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

    inner class SiparisHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tumLayout = itemView.tumLayout
        val siparisVeren = itemView.tvSiparisVeren
        val siparisAdres = itemView.tvSiparisAdres
        val siparisTel = itemView.tvSiparisTel
        val tvZaman = itemView.tvZaman
        val tvTeslimZaman = itemView.tvTeslimZamani
        val tvNot = itemView.tvNot
        val tvFiyat = itemView.tvFiyat
        val siparisGiren = itemView.tvSiparisGiren


        val tv400gr = itemView.tv3lt
        val tb400gr = itemView.tb400gr
        val tv600gr = itemView.tv5lt
        val tb600gr = itemView.tb600gr
        val tvYumurta = itemView.tvYumurta
        val tbYumurta = itemView.tbYumurta
        val tvYogurt = itemView.tvYogurt
        val tbYogurt = itemView.tbYogurt
        val tvYogurt3 = itemView.tvYogurt3
        val tbYogurt3 = itemView.tbYogurt3
        val tvCokertme = itemView.tvCokertme
        val tbCokertme = itemView.tbCokertme
        val tvCokelek = itemView.tvCokelek
        val tbCokelek = itemView.tbCokelek
        val tvDil = itemView.tvDil
        val tbDil = itemView.tbDil
        val tvBeyaz = itemView.tvBeyazPeynir
        val tvBeyaz1000 = itemView.tvBeyazPeynir1000
        val tbBeyaz = itemView.tbBeyazPeynir
        val tbBeyaz1000 = itemView.tbBeyazPeynir1000
        val tvCig = itemView.tvCigsut
        val tbCig = itemView.tbCigsut
        val tvKangal = itemView.tvKangal
        val tbKangal = itemView.tbKangal
        val tvSucuk = itemView.tvSucuk
        val tbSucuk = itemView.tbSucuk
        val tvKavurma = itemView.tvKavurma
        val tbKavurma = itemView.tbKavurma
        val tvKefir = itemView.tvKefir
        val tbKefir = itemView.tbKefir


        fun setData(siparisData: SiparisData) {
            aramaAdres(siparisData)
            tableAyarlari(siparisData)
            siparisVeren.text = siparisData.siparis_veren
            siparisAdres.text = siparisData.siparis_mah + " mahallesi " + siparisData.siparis_adres + " " + siparisData.siparis_apartman
            siparisTel.text = siparisData.siparis_tel
            var fiyat = fiyatHesapla(siparisData)


            siparisHataMesajlariVeBosKontrol(siparisData)

        }


        private fun fiyatHesapla(siparisData: SiparisData): Double {
            var fiyat = 0.0

            try {
                fiyat = (siparisData.sucuk.toString().toInt() * siparisData.sucuk_fiyat.toString().toDouble()) +
                        (siparisData.sut_cokelegi.toString().toInt() * siparisData.sut_cokelegi_fiyat.toString().toDouble()) +
                        (siparisData.yogurt.toString().toInt() * siparisData.yogurt_fiyat.toString().toDouble()) +
                        (siparisData.yogurt3.toString().toInt() * siparisData.yogurt3_fiyat.toString().toDouble()) +
                        (siparisData.yumurta.toString().toInt() * siparisData.yumurta_fiyat.toString().toDouble()) +
                        (siparisData.yybeyaz_pey.toString().toInt() * siparisData.yybeyaz_pey_fiyat.toString().toDouble()) +
                        (siparisData.yybeyaz_pey1000.toString().toInt() * siparisData.yybeyaz_pey1000_fiyat.toString().toDouble()) +
                        (siparisData.yycig_sut.toString().toInt() * siparisData.yycig_sut_fiyat.toString().toDouble()) +
                        (siparisData.yycokertme_pey.toString().toInt() * siparisData.yycokertme_pey_fiyat.toString().toDouble()) +
                        (siparisData.yydil_pey.toString().toInt() * siparisData.yydil_pey_fiyat.toString().toDouble()) +
                        (siparisData.yykangal_sucuk.toString().toInt() * siparisData.yykangal_sucuk_fiyat.toString().toDouble()) +
                        (siparisData.yykasar_400.toString().toInt() * siparisData.yykasar_400_fiyat.toString().toDouble()) +
                        (siparisData.yykasar_600.toString().toInt() * siparisData.yykasar_600_fiyat.toString().toDouble()) +
                        (siparisData.yykavurma.toString().toInt() * siparisData.yykavurma_fiyat.toString().toDouble()) +
                        (siparisData.yykefir.toString().toInt() * siparisData.yykefir_fiyat.toString().toDouble())

                tvNot.text = "Fiyat:  $fiyat\n Not: " + siparisData.siparis_notu
            } catch (e: Exception) {
                Log.e("Sipariş Para Hatasi", e.message.toString())
            }

            return fiyat
        }

        fun siparisHataMesajlariVeBosKontrol(siparisData: SiparisData) {

            if (!siparisData.siparisi_giren.isNullOrEmpty()) {
                siparisGiren.text = siparisData.siparisi_giren.toString()
            } else {
                siparisGiren.visibility = View.GONE
            }

            if (siparisData.siparis_zamani != null) {
                tvZaman.text = TimeAgo.getTimeAgo(siparisData.siparis_zamani.toString().toLong())
            } else {
                tvZaman.text = "yok"
                hataMesajiYazdir("sipariş zamanı yok ${siparisData.siparis_key}", siparisData.siparis_veren.toString())
            }
            if (siparisData.siparis_teslim_tarihi != null) {
                tvTeslimZaman.text = formatDate(siparisData.siparis_teslim_tarihi).toString()
            } else {
                tvTeslimZaman.text = "yok"
                hataMesajiYazdir("teslim tarihi ${siparisData.siparis_key}", siparisData.siparis_veren.toString())
            }


            tvSucuk.text = siparisData.sucuk+ " - " + siparisData.sucuk_fiyat
            tvCokelek.text = siparisData.sut_cokelegi + " - " + siparisData.sut_cokelegi_fiyat
            tvYogurt.text = siparisData.yogurt + " - " + siparisData.yogurt_fiyat
            tvYogurt3.text = siparisData.yogurt3 + " - " + siparisData.yogurt3_fiyat
            tvYumurta.text = siparisData.yumurta + " - " + siparisData.yumurta_fiyat
            tvBeyaz.text = siparisData.yybeyaz_pey+ " - " + siparisData.yybeyaz_pey_fiyat
            tvBeyaz1000.text = siparisData.yybeyaz_pey1000+ " - " + siparisData.yybeyaz_pey1000_fiyat
            tvCig.text = siparisData.yycig_sut+ " - " + siparisData.yycig_sut_fiyat
            tvCokertme.text = siparisData.yycokertme_pey+ " - " + siparisData.yycokertme_pey_fiyat
            tvDil.text = siparisData.yydil_pey+ " - " + siparisData.yydil_pey_fiyat
            tvKangal.text = siparisData.yykangal_sucuk + " - " + siparisData.yykangal_sucuk_fiyat
            tv400gr.text = siparisData.yykasar_400 + " - " + siparisData.yykasar_400_fiyat
            tv600gr.text = siparisData.yykasar_600+ " - " + siparisData.yykasar_600_fiyat
            tvKavurma.text = siparisData.yykavurma + " - " + siparisData.yykavurma_fiyat
            tvKefir.text = siparisData.yykefir + " - " + siparisData.yykefir_fiyat


        }


        private fun aramaAdres(siparisData: SiparisData) {
            siparisTel.setOnClickListener {
                val arama = Intent(Intent.ACTION_DIAL)//Bu kod satırımız bizi rehbere telefon numarası ile yönlendiri.
                arama.data = Uri.parse("tel:" + siparisData.siparis_tel)
                myContext.startActivity(arama)
            }

            siparisAdres.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q= " + siparisData.siparis_mah + " mahallesi " + siparisData.siparis_adres + " Zonguldak"))
                myContext.startActivity(intent)
            }
        }

        private fun tableAyarlari(siparisData: SiparisData) {
            if (siparisData.yykasar_400 == "0") {
                tb400gr.visibility = View.GONE
            }
            if (siparisData.yykasar_600 == "0") {
                tb600gr.visibility = View.GONE
            }
            if (siparisData.sucuk == "0") {
                tbSucuk.visibility = View.GONE
            }
            if (siparisData.sut_cokelegi == "0") {
                tbCokelek.visibility = View.GONE
            }
            if (siparisData.yogurt == "0") {
                tbYogurt.visibility = View.GONE
            }
            if (siparisData.yogurt3 == "0") {
                tbYogurt3.visibility = View.GONE
            }
            if (siparisData.yumurta == "0") {
                tbYumurta.visibility = View.GONE
            }
            if (siparisData.yybeyaz_pey == "0") {
                tbBeyaz.visibility = View.GONE
            }
            if (siparisData.yybeyaz_pey1000.toString() == "0") {
                tbBeyaz1000.visibility = View.GONE
            }
            if (siparisData.yycig_sut == "0") {
                tbCig.visibility = View.GONE
            }
            if (siparisData.yycokertme_pey == "0") {
                tbCokertme.visibility = View.GONE
            }
            if (siparisData.yydil_pey == "0") {
                tbDil.visibility = View.GONE
            }
            if (siparisData.yykangal_sucuk == "0") {
                tbKangal.visibility = View.GONE
            }
            if (siparisData.yykavurma == "0") {
                tbKavurma.visibility = View.GONE
            }
            if (siparisData.yykefir == "0") {
                tbKefir.visibility = View.GONE
            }


        }

        fun hataMesajiYazdir(s: String, isim: String) {
            FirebaseDatabase.getInstance().reference.child("Hatalar/SiparisAdapter").push().setValue(s)
            Toast.makeText(myContext, "Bu Kişinin Siparişi Hatalı Lütfen Sil $isim", Toast.LENGTH_LONG).show()
        }

        fun formatDate(miliSecond: Long?): String? {
            if (miliSecond == null) return "0"
            val date = Date(miliSecond)
            val sdf = SimpleDateFormat("d MMM", Locale("tr"))
            return sdf.format(date)
        }


    }


}