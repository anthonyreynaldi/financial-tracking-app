package com.example.proyek_android

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.proyek_android.Adapter.AdapterPage
import com.example.proyek_android.Fragment.KategoriPendapatanFragment
import com.example.proyek_android.Fragment.KategoriPengeluaranFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.google.GoogleEmojiProvider

class kategori : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kategori)

        val user = homepage.user

        //view pager
        val fragmentList = arrayListOf<Fragment>(KategoriPengeluaranFragment(), KategoriPendapatanFragment())

        val tab_layout = findViewById<TabLayout>(R.id.tab_layout)
        val viewpager_kategori = findViewById<ViewPager2>(R.id.viewpager_kategori)

        val adapterPage = AdapterPage(fragmentList, supportFragmentManager, lifecycle)
        viewpager_kategori.adapter = adapterPage

        TabLayoutMediator(tab_layout, viewpager_kategori){ tab, position ->
            if (position == 0){
                tab.text = "Pengeluaran"
            }else{
                tab.text = "Pemasukan"
            }
        }.attach()

        //add kategori dialog
        val kategori_dialog = Dialog(this)
        kategori_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        kategori_dialog.setContentView(R.layout.custom_dialog_kategori)
        kategori_dialog.setCancelable(true)

        val radio_grup = kategori_dialog.findViewById<RadioGroup>(R.id.radioGroup)
        val rd_pengeluaran = kategori_dialog.findViewById<RadioButton>(R.id.rd_pengeluaran)
        val rd_pemasukan = kategori_dialog.findViewById<RadioButton>(R.id.rd_pemasukan)
        val tv_emoji = kategori_dialog.findViewById<TextView>(R.id.tv_emoji)
        val et_emoji_helper = kategori_dialog.findViewById<EditText>(R.id.et_emoji_helper)
        val et_nama_kategori = kategori_dialog.findViewById<EditText>(R.id.et_nama_kategori)
        val btn_tambah_kategori = kategori_dialog.findViewById<Button>(R.id.btn_tambah_kategori)

        //set emoji popup keyboard
        EmojiManager.install(GoogleEmojiProvider())
        val popup : EmojiPopup = EmojiPopup(kategori_dialog.findViewById(R.id.rootViewDialog), et_emoji_helper, onEmojiClickListener = {
            Log.d("TAG EMOJI", "onCreate: ${it.unicode}")
            tv_emoji.setText(it.unicode)
        })

        tv_emoji.setOnClickListener {
            popup.toggle()
        }

        //set tambah kategori action
        btn_tambah_kategori.setOnClickListener {
            val icon = tv_emoji.text.toString()
            val nama_kategori = et_nama_kategori.text.toString()

            var jenis = ""
            if (rd_pemasukan.isChecked){
                jenis = "pemasukan"
            }else if (rd_pengeluaran.isChecked){
                jenis = "pengeluaran"
            }

            //check input
            if (nama_kategori.isBlank()){
                et_nama_kategori.setError("Nama kategori harus diisi")
            }

            if (radio_grup.checkedRadioButtonId == -1){
                rd_pemasukan.setError("Pilih item")
            }

            // add kategori to user
            if (nama_kategori.isNotBlank() && radio_grup.checkedRadioButtonId != -1){
                //tambah kategori ke user


                //reset dialog
                radio_grup.clearCheck()
                tv_emoji.setText("😀")
                et_emoji_helper.setText("")
                et_nama_kategori.setText("")
                rd_pemasukan.setError(null)

                //dissmis dialog
                kategori_dialog.dismiss()
            }

        }

        val fab_tambah_kategori = findViewById<FloatingActionButton>(R.id.fab_tambah_kategori)

        fab_tambah_kategori.setOnClickListener {
            kategori_dialog.show()
        }


    }
}