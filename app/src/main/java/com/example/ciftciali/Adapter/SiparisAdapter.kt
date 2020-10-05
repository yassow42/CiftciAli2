package com.example.ciftciali.Adapter


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
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

                                    var siparisData = SiparisData(
                                        siparisler[position].siparis_zamani,
                                        siparisler[position].siparis_teslim_zamani,
                                        siparisler[position].siparis_teslim_tarihi,
                                        siparisler[position].siparis_adres,
                                        siparisler[position].siparis_apartman,
                                        siparisler[position].siparis_tel,
                                        siparisler[position].siparis_veren,
                                        siparisler[position].siparis_mah,
                                        siparisler[position].siparis_notu,
                                        siparisler[position].siparis_key,
                                        siparisler[position].yumurta,
                                        siparisler[position].yykasar_400,
                                        siparisler[position].yykasar_600,
                                        siparisler[position].yogurt,
                                        siparisler[position].sut_cokelegi,
                                        siparisler[position].yycokertme_pey,
                                        siparisler[position].yydil_pey,
                                        siparisler[position].yybeyaz_pey,
                                        siparisler[position].yycig_sut,
                                        siparisler[position].yykangal_sucuk,
                                        siparisler[position].sucuk,
                                        siparisler[position].yykavurma,
                                        siparisler[position].musteri_zkonum,
                                        siparisler[position].musteri_zlat,
                                        siparisler[position].musteri_zlong,
                                        kullaniciAdi.toString()
                                    )

                                    var key = siparisler[position].siparis_key.toString()
                                    ref.child("Musteriler").child(siparisler[position].siparis_veren.toString()).child("siparisleri").child(key).setValue(siparisData)

                                    ref.child("Teslim_siparisler").child(key).setValue(siparisData)
                                        .addOnCompleteListener {

                                            myContext.startActivity(Intent(myContext,SiparislerActivity::class.java))
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

                        viewDuzenle.et3lt.setText(siparisler[position].yykasar_400)
                        viewDuzenle.et5lt.setText(siparisler[position].yykasar_600)
                        viewDuzenle.etYumurta.setText(siparisler[position].yumurta)
                        viewDuzenle.etSucuk.setText(siparisler[position].sucuk)
                        viewDuzenle.etSutCokelegi.setText(siparisler[position].sut_cokelegi)
                        viewDuzenle.etYogurt.setText(siparisler[position].yogurt)
                        viewDuzenle.etBeyazPeynir.setText(siparisler[position].yybeyaz_pey)
                        viewDuzenle.etCigsut.setText(siparisler[position].yycig_sut)
                        viewDuzenle.etCokertmePeyniri.setText(siparisler[position].yycokertme_pey)
                        viewDuzenle.etDilPeyniri.setText(siparisler[position].yydil_pey)
                        viewDuzenle.etKangal.setText(siparisler[position].yykangal_sucuk)
                        viewDuzenle.etKavurma.setText(siparisler[position].yykavurma)



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
                            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            cal.set(Calendar.MINUTE, minute)
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

                                var sut3lt = "0"
                                if (viewDuzenle.et3lt.text.isNotEmpty()) {
                                    sut3lt = viewDuzenle.et3lt.text.toString()
                                }
                                var sut5lt = "0"
                                if (viewDuzenle.et5lt.text.isNotEmpty()) {
                                    sut5lt = viewDuzenle.et5lt.text.toString()
                                }
                                var yumurta = "0"
                                if (viewDuzenle.etYumurta.text.isNotEmpty()) {
                                    yumurta = viewDuzenle.etYumurta.text.toString()
                                }
                                var yogurt = "0"
                                if (viewDuzenle.etYogurt.text.isNotEmpty()) {
                                    yogurt = viewDuzenle.etYogurt.text.toString()
                                }

                                var sutCokelegi = "0"
                                if (viewDuzenle.etSutCokelegi.text.toString().isNotEmpty()) {
                                    sutCokelegi = viewDuzenle.etSutCokelegi.text.toString()
                                }
                                var cokertmePey = "0"
                                if (viewDuzenle.etCokertmePeyniri.text.toString().isNotEmpty()) {
                                    cokertmePey = viewDuzenle.etCokertmePeyniri.text.toString()
                                }
                                var dilPey = "0"
                                if (viewDuzenle.etDilPeyniri.text.toString().isNotEmpty()) {
                                    dilPey = viewDuzenle.etDilPeyniri.text.toString()
                                }
                                var beyazPey = "0"
                                if (viewDuzenle.etBeyazPeynir.text.toString().isNotEmpty()) {
                                    beyazPey = viewDuzenle.etBeyazPeynir.text.toString()
                                }
                                var cigSut = "0"
                                if (viewDuzenle.etCigsut.text.toString().isNotEmpty()) {
                                    cigSut = viewDuzenle.etCigsut.text.toString()
                                }
                                var kangal = "0"
                                if (viewDuzenle.etKangal.text.toString().isNotEmpty()) {
                                    kangal = viewDuzenle.etKangal.text.toString()
                                }
                                var sucuk = "0"
                                if (viewDuzenle.etSucuk.text.toString().isNotEmpty()) {
                                    sucuk = viewDuzenle.etSucuk.text.toString()
                                }
                                var kavurma = "0"
                                if (viewDuzenle.etKavurma.text.toString().isNotEmpty()) {
                                    kavurma = viewDuzenle.etKavurma.text.toString()
                                }


                                var ref = FirebaseDatabase.getInstance().reference
                                var not = viewDuzenle.etSiparisNotu.text.toString()
                                var siparisKey = siparisler[position].siparis_key.toString()
                                var siparisVeren = siparisler[position].siparis_veren.toString()
                                var siparisData = SiparisData(
                                    siparisler[position].siparis_zamani,
                                    siparisler[position].siparis_teslim_zamani,
                                    cal.timeInMillis,
                                    siparisler[position].siparis_adres,
                                    siparisler[position].siparis_apartman,
                                    siparisler[position].siparis_tel,
                                    siparisler[position].siparis_veren,
                                    siparisler[position].siparis_mah,
                                    not,
                                    siparisler[position].siparis_key,
                                    yumurta,
                                    sut3lt,
                                    sut5lt,
                                    yogurt,
                                    sutCokelegi,
                                    cokertmePey,
                                    dilPey,
                                    beyazPey,
                                    cigSut,
                                    kangal,
                                    sucuk,
                                    kavurma,
                                    siparisler[position].musteri_zkonum,
                                    siparisler[position].musteri_zlat,
                                    siparisler[position].musteri_zlong,
                                    kullaniciAdi.toString()
                                )
                                ref.child("Siparisler").child(siparisKey).setValue(siparisData)
                                ref.child("Musteriler").child(siparisVeren).child("siparisleri").setValue(siparisData)

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
        val tvCokertme = itemView.tvCokertme
        val tbCokertme = itemView.tbCokertme
        val tvCokelek = itemView.tvCokelek
        val tbCokelek = itemView.tbCokelek
        val tvDil = itemView.tvDil
        val tbDil = itemView.tbDil
        val tvBeyaz = itemView.tvBeyazPeynir
        val tbBeyaz = itemView.tbBeyazPeynir
        val tvCig = itemView.tvCigsut
        val tbCig = itemView.tbCigsut
        val tvKangal = itemView.tvKangal
        val tbKangal = itemView.tbKangal
        val tvSucuk = itemView.tvSucuk
        val tbSucuk = itemView.tbSucuk
        val tvKavurma = itemView.tvKavurma
        val tbKavurma = itemView.tbKavurma


        fun setData(siparisData: SiparisData) {
            aramaAdres(siparisData)
            tableAyarlari(siparisData)
            siparisVeren.text = siparisData.siparis_veren
            siparisAdres.text = siparisData.siparis_mah + " mahallesi " + siparisData.siparis_adres + " " + siparisData.siparis_apartman
            siparisTel.text = siparisData.siparis_tel
            tvNot.text = siparisData.siparis_notu

            siparisHataMesajlariVeBosKontrol(siparisData)
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


            if (!siparisData.yykasar_400.isNullOrEmpty()) {
                tv400gr.text = siparisData.yykasar_400
            } else {
                hataMesajiYazdir("sut3 yok ${siparisData.siparis_key}", siparisData.siparis_veren.toString())
            }

            if (!siparisData.yykasar_600.isNullOrEmpty()) {
                tv600gr.text = siparisData.yykasar_600
            } else {
                hataMesajiYazdir("sut5 yok ${siparisData.siparis_key}", siparisData.siparis_veren.toString())
            }


            if (!siparisData.yumurta.isNullOrEmpty()) {
                tvYumurta.text = siparisData.yumurta

            } else {
                hataMesajiYazdir("yumurta yok ${siparisData.siparis_key}", siparisData.siparis_veren.toString())
            }

            if (!siparisData.yogurt.isNullOrEmpty()) {
                tvYogurt.text = siparisData.yogurt

            } else {
                hataMesajiYazdir("yogurt yok ${siparisData.siparis_key}", siparisData.siparis_veren.toString())
            }

            if (!siparisData.sut_cokelegi.isNullOrEmpty()) {
                tvCokelek.text = siparisData.sut_cokelegi

            } else {
                hataMesajiYazdir("Cokelek yok ${siparisData.siparis_key}", siparisData.siparis_veren.toString())
            }

            if (!siparisData.yycokertme_pey.isNullOrEmpty()) {
                tvCokertme.text = siparisData.yycokertme_pey

            } else {
                hataMesajiYazdir("cokertme yok ${siparisData.siparis_key}", siparisData.siparis_veren.toString())
            }
            if (!siparisData.yydil_pey.isNullOrEmpty()) {
                tvDil.text = siparisData.yydil_pey

            } else {
                hataMesajiYazdir("dil yok ${siparisData.siparis_key}", siparisData.siparis_veren.toString())
            }
            if (!siparisData.yybeyaz_pey.isNullOrEmpty()) {
                tvBeyaz.text = siparisData.yybeyaz_pey

            } else {
                hataMesajiYazdir("beyaz yok ${siparisData.siparis_key}", siparisData.siparis_veren.toString())
            }
            if (!siparisData.yycig_sut.isNullOrEmpty()) {
                tvCig.text = siparisData.yycig_sut

            } else {
                hataMesajiYazdir("cig sut yok ${siparisData.siparis_key}", siparisData.siparis_veren.toString())
            }

            if (!siparisData.yykangal_sucuk.isNullOrEmpty()) {
                tvKangal.text = siparisData.yykangal_sucuk

            } else {
                hataMesajiYazdir("kangal yok ${siparisData.siparis_key}", siparisData.siparis_veren.toString())
            }

            if (!siparisData.sucuk.isNullOrEmpty()) {
                tvSucuk.text = siparisData.sucuk

            } else {
                hataMesajiYazdir("sucuk yok ${siparisData.siparis_key}", siparisData.siparis_veren.toString())
            }
            if (!siparisData.yykavurma.isNullOrEmpty()) {
                tvKavurma.text = siparisData.yykavurma
            } else {
                hataMesajiYazdir("kavurma yok ${siparisData.siparis_key}", siparisData.siparis_veren.toString())
            }


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
            if (siparisData.yumurta == "0") {
                tbYumurta.visibility = View.GONE
            }
            if (siparisData.yybeyaz_pey == "0") {
                tbBeyaz.visibility = View.GONE
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