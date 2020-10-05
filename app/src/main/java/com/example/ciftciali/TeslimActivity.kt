package com.example.ciftciali;

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ciftciali.Adapter.TeslimEdilenlerAdapter
import com.example.ciftciali.Datalar.SiparisData
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_siparisler.bottomNav
import kotlinx.android.synthetic.main.activity_teslim.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TeslimActivity : AppCompatActivity() {
    private val ACTIVITY_NO = 2
    var suankiTeslimList: ArrayList<SiparisData> = ArrayList()
    var butunTeslimList: ArrayList<SiparisData> = ArrayList()
    lateinit var progressDialog: ProgressDialog
    val hndler = Handler()

    val ref = FirebaseDatabase.getInstance().reference

    lateinit var TeslimAdapter: TeslimEdilenlerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teslim)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setupNavigationView()
        setupBtn()
        setupRecyclerView()

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Yükleniyor...")
        progressDialog.setCancelable(false)
        progressDialog.show()


        setupVeri()
        hndler.postDelayed(Runnable { progressDialog.dismiss() }, 3000)


    }


    private fun setupBtn() {
        tvZamandan.text = SimpleDateFormat("HH:mm dd.MM.yyyy").format(System.currentTimeMillis())
        tvZamana.text = SimpleDateFormat("HH:mm dd.MM.yyyy").format(System.currentTimeMillis())

        var calZamandan = Calendar.getInstance()
        val dateSetListenerZamandan = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calZamandan.set(Calendar.YEAR, year)
            calZamandan.set(Calendar.MONTH, monthOfYear)
            calZamandan.set(Calendar.DAY_OF_MONTH, dayOfMonth)


            val myFormat = "HH:mm dd.MM.yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale("tr"))
            tvZamandan.text = sdf.format(calZamandan.time)
        }

        val timeSetListenerZamandan = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            calZamandan.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calZamandan.set(Calendar.MINUTE, minute)
        }

        var calZamana = Calendar.getInstance()
        val dateSetListenerZamana = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calZamana.set(Calendar.YEAR, year)
            calZamana.set(Calendar.MONTH, monthOfYear)
            calZamana.set(Calendar.DAY_OF_MONTH, dayOfMonth)


            val myFormat = "HH:mm dd.MM.yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale("tr"))
            tvZamana.text = sdf.format(calZamana.time)
        }

        val timeSetListenerZamana = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            calZamana.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calZamana.set(Calendar.MINUTE, minute)
        }



        tvZamandan.setOnClickListener {
            DatePickerDialog(this, dateSetListenerZamandan, calZamandan.get(Calendar.YEAR), calZamandan.get(Calendar.MONTH), calZamandan.get(Calendar.DAY_OF_MONTH)).show()
            TimePickerDialog(this, timeSetListenerZamandan, calZamandan.get(Calendar.HOUR_OF_DAY), calZamandan.get(Calendar.MINUTE), true).show()

        }
        tvZamana.setOnClickListener {
            DatePickerDialog(this, dateSetListenerZamana, calZamana.get(Calendar.YEAR), calZamana.get(Calendar.MONTH), calZamana.get(Calendar.DAY_OF_MONTH)).show()
            TimePickerDialog(this, timeSetListenerZamana, calZamana.get(Calendar.HOUR_OF_DAY), calZamana.get(Calendar.MINUTE), true).show()
        }


        imgTarih.setOnClickListener {
            suankiTeslimList.clear()

            for (ds in butunTeslimList) {


                if (calZamandan.timeInMillis < ds.siparis_teslim_zamani!!.toLong() && ds.siparis_teslim_zamani!!.toLong() < calZamana.timeInMillis) {
                    suankiTeslimList.add(ds)
                }

                suankiTeslimList.sortByDescending { it.siparis_teslim_zamani }
                TeslimAdapter.notifyDataSetChanged()

            }

        }


    }

    private fun setupVeri() {


        ref.child("Zaman").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                var gece3GelenZaman = p0.child("gece3").value.toString().toLong()
                var suankıZaman = System.currentTimeMillis()
                if (gece3GelenZaman < suankıZaman) {
                    var guncelGece3 = gece3GelenZaman + 86400000
                    FirebaseDatabase.getInstance().reference.child("Zaman").child("gece3").setValue(guncelGece3)
                }


                ref.child("Teslim_siparisler").orderByChild("siparis_teslim_zamani").addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(data: DataSnapshot) {
                        butunTeslimList.clear()
                        suankiTeslimList.clear()
                        if (data.hasChildren()) {

                            for (ds in data.children) {
                                var gelenData = ds.getValue(SiparisData::class.java)!!
                                butunTeslimList.add(gelenData)

                                if (gece3GelenZaman - 86400000 < gelenData.siparis_teslim_zamani!!.toLong() && gelenData.siparis_teslim_zamani!!.toLong() < gece3GelenZaman) {
                                    suankiTeslimList.add(gelenData)
                                }
                            }

                            progressDialog.dismiss()
                            suankiTeslimList.sortByDescending { it.siparis_teslim_zamani }
                            TeslimAdapter.notifyDataSetChanged()

                        } else {
                            progressDialog.setMessage("Veriler alınıyor...")
                            hndler.postDelayed(Runnable { progressDialog.dismiss() }, 2000)
                        }
                    }
                })

            }
        })


    }





private fun setupRecyclerView() {
    rcTeslimEdilenler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    TeslimAdapter = TeslimEdilenlerAdapter(this, suankiTeslimList)
    rcTeslimEdilenler.adapter = TeslimAdapter
    rcTeslimEdilenler.setHasFixedSize(true)
}

fun setupNavigationView() {

    BottomNavigationViewHelper.setupBottomNavigationView(bottomNav)
    BottomNavigationViewHelper.setupNavigation(this, bottomNav) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
    var menu = bottomNav.menu
    var menuItem = menu.getItem(ACTIVITY_NO)
    menuItem.setChecked(true)
}
}
