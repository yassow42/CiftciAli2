package com.example.ciftciali.Adapter


import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.example.ciftciali.Datalar.SiparisData
import com.example.ciftciali.R
import com.example.ciftciali.TimeAgo
import com.google.firebase.database.FirebaseDatabase

import kotlinx.android.synthetic.main.item_teslim.view.*
import kotlinx.android.synthetic.main.item_teslim.view.tv3lt
import kotlinx.android.synthetic.main.item_teslim.view.tv5lt
import kotlinx.android.synthetic.main.item_teslim.view.tvCokelek
import kotlinx.android.synthetic.main.item_teslim.view.tvCokertme
import kotlinx.android.synthetic.main.item_teslim.view.tvDil
import kotlinx.android.synthetic.main.item_teslim.view.tvKangal
import kotlinx.android.synthetic.main.item_teslim.view.tvSucuk
import kotlinx.android.synthetic.main.item_teslim.view.tvYumurta
import kotlinx.android.synthetic.main.item_teslim.view.tvZaman
import java.lang.Exception

class TeslimEdilenlerAdapter(val myContext: Context, val siparisler: ArrayList<SiparisData>) : RecyclerView.Adapter<TeslimEdilenlerAdapter.SiparisHolder>() {
    val ref = FirebaseDatabase.getInstance().reference
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeslimEdilenlerAdapter.SiparisHolder {
        val view = LayoutInflater.from(myContext).inflate(R.layout.item_teslim, parent, false)


        return SiparisHolder(view)
    }

    override fun getItemCount(): Int {

        return siparisler.size
    }

    override fun onBindViewHolder(holder: SiparisHolder, position: Int) {
        holder.setData(siparisler[position])
        holder.itemView.setOnLongClickListener {
            var alert = AlertDialog.Builder(myContext)
                .setTitle("Siparişi Sil")
                .setMessage("Emin Misin ?")
                .setPositiveButton("Sil", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {

                        ref.child("Teslim_siparisler").child(siparisler[position].siparis_key.toString()).removeValue()
                        ref.child("Musteriler").child(siparisler[position].siparis_veren.toString()).child("siparisleri").child(siparisler[position].siparis_key.toString()).removeValue()

                        notifyDataSetChanged()
                    }
                })
                .setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        p0!!.dismiss()
                    }
                }).create()

            alert.show()

            return@setOnLongClickListener true
        }

    }

    inner class SiparisHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val musteriAdSoyad = itemView.tvMusteriAdSoyad
        val teslimEden = itemView.tvSiparisGiren
        val zaman = itemView.tvZaman


        val sutCokelegi = itemView.tvCokelek
        val dil = itemView.tvDil
        val beyaz = itemView.tvBeyaz
        val beyaz1000 = itemView.tvBeyaz1000
        val kasar400 = itemView.tv3lt
        val kasar600 = itemView.tv5lt
        val cokertme = itemView.tvCokertme
        val sucuk = itemView.tvSucuk
        val kangal = itemView.tvKangal
        val yumurta = itemView.tvYumurta
        val yogurt = itemView.tvYogurt
        val yogurt3 = itemView.tvYogurt3
        val cigsut = itemView.tvCigsut
        val kavurma = itemView.tvKavurma
        val kefir = itemView.tvKefir
        val tvFiyat = itemView.tvFiyat


        val llCokelek = itemView.llCokelek
        val llDil = itemView.llDil
        val llBeyaz = itemView.llBeyaz
        val llBeyaz1000 = itemView.llBeyaz1000
        val llCokertme = itemView.llCokertme
        val ll600 = itemView.ll600
        val ll400 = itemView.ll400
        val llSucuk = itemView.llSucuk
        val llKangal = itemView.llKangal
        val llYumurta = itemView.llYumurta
        val llYogurt = itemView.llYogurt
        val llYogurt3 = itemView.llYogurt3
        val llCigsut = itemView.llCigsut
        val llKavurma = itemView.llKavurma
        val llKefir = itemView.llKefir

        fun setData(siparisData: SiparisData) {
            musteriAdSoyad.text = siparisData.siparis_veren
            zaman.text = TimeAgo.getTimeAgo(siparisData.siparis_teslim_zamani.toString().toLong())

            bosKontrol(siparisData)
            fiyatHesapla(siparisData)

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

                tvFiyat.text = "Fiyat:  $fiyat"
            } catch (e: Exception) {
                Log.e("Sipariş Para Hatasi", e.message.toString())
            }

            return fiyat
        }

        fun bosKontrol(siparisData: SiparisData) {

            if (!siparisData.siparisi_giren.isNullOrEmpty()) {
                teslimEden.text = siparisData.siparisi_giren.toString()
            } else {
                teslimEden.visibility = View.GONE
            }

            UrunGizleme(siparisData.sut_cokelegi.toString(), sutCokelegi, llCokelek)
            UrunGizleme(siparisData.yydil_pey.toString(), dil, llDil)
            UrunGizleme(siparisData.yybeyaz_pey.toString(), beyaz, llBeyaz)
            UrunGizleme(siparisData.yybeyaz_pey1000.toString(), beyaz1000, llBeyaz1000)
            UrunGizleme(siparisData.yycokertme_pey.toString(), cokertme, llCokertme)
            UrunGizleme(siparisData.yykasar_400.toString(), kasar400, ll400)
            UrunGizleme(siparisData.yykasar_600.toString(), kasar600, ll600)
            UrunGizleme(siparisData.sucuk.toString(), sucuk, llSucuk)
            UrunGizleme(siparisData.yykangal_sucuk.toString(), kangal, llKangal)
            UrunGizleme(siparisData.yumurta.toString(), yumurta, llYumurta)
            UrunGizleme(siparisData.yogurt.toString(), yogurt, llYogurt)
            UrunGizleme(siparisData.yogurt3.toString(), yogurt3, llYogurt3)
            UrunGizleme(siparisData.yykavurma.toString(), kavurma, llKavurma)
            UrunGizleme(siparisData.yykefir.toString(), kefir, llKefir)
            UrunGizleme(siparisData.yycig_sut.toString(), cigsut, llCigsut)

        }
    }

    fun UrunGizleme(gelenUrun: String, textView: TextView, linearLayout: LinearLayout) {
        if (gelenUrun != "0") {
            textView.text = gelenUrun
        } else {
            linearLayout.visibility = View.GONE
        }
    }

}


