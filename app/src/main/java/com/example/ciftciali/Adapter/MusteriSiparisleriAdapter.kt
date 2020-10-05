package com.example.ciftciali.Adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

import com.example.ciftciali.Datalar.SiparisData
import com.example.ciftciali.R
import com.example.ciftciali.TimeAgo

import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.item_siparisler.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MusteriSiparisleriAdapter(val myContext: Context, val siparisler: ArrayList<SiparisData>) : RecyclerView.Adapter<MusteriSiparisleriAdapter.SiparisMusteriHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusteriSiparisleriAdapter.SiparisMusteriHolder {
        val view = LayoutInflater.from(myContext).inflate(R.layout.item_siparisler, parent, false)


        view.tvSiparisVeren.visibility = View.GONE
        view.tvSiparisTel.visibility = View.GONE
        view.tvSiparisAdres.visibility = View.GONE
        view.tvZaman.visibility = View.GONE
        view.tvNot.visibility = View.GONE
        view.tvTeslimZamani.visibility = View.GONE
        view.view.visibility = View.GONE


        return SiparisMusteriHolder(view)
    }

    override fun getItemCount(): Int {

        return siparisler.size
    }

    override fun onBindViewHolder(holder: MusteriSiparisleriAdapter.SiparisMusteriHolder, position: Int) {

        holder.setData(siparisler[position])

    }

    inner class SiparisMusteriHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tumLayout = itemView.tumLayout
        val siparisVeren = itemView.tvSiparisVeren
        val siparisAdres = itemView.tvSiparisAdres
        val siparisTel = itemView.tvSiparisTel
        val tvZaman = itemView.tvZaman
        val tvTeslimZaman = itemView.tvTeslimZamani
        val tvNot = itemView.tvNot
        val tvFiyat = itemView.tvFiyat


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
            tableAyarlari(siparisData)

            siparisVeren.text = siparisData.siparis_veren
            siparisAdres.text = siparisData.siparis_mah + " mahallesi " + siparisData.siparis_adres
            siparisTel.text = siparisData.siparis_tel
            tvNot.text = siparisData.siparis_notu
            tvZaman.text = TimeAgo.getTimeAgo(siparisData.siparis_zamani.toString().toLong())
            tvTeslimZaman.text = formatDate(siparisData.siparis_teslim_tarihi).toString()


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

        fun tableAyarlari(siparisData: SiparisData) {
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
            val sdf = SimpleDateFormat(" d MMM yyyy ", Locale("tr"))
            return sdf.format(date)

        }

    }


}