package com.example.proyek_android.Fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import com.example.proyek_android.Classes.KategoriPengeluaran
import com.example.proyek_android.R
import com.example.proyek_android.homepage
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.google.GoogleEmojiProvider

class KategoriPengeluaranFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kategori_pengeluaran, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = homepage.user

        val lv_kategori_pengeluaran= view.findViewById<ListView>(R.id.lv_kategori_pengeluaran)

        val listKategoriPengeluaran = ArrayList<String>()

        for (kategori in user.kategoriPengeluaran){
            listKategoriPengeluaran.add(kategori.icon + " " + kategori.nama)
        }

        val adapter = ArrayAdapter(view.context, android.R.layout.simple_list_item_1, listKategoriPengeluaran)
        lv_kategori_pengeluaran.adapter = adapter

        lv_kategori_pengeluaran.setOnItemClickListener { adapterView, view, i, l ->
            //add kategori dialog
            val kategori_dialog = Dialog(view.context)
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
            val tv_judul_dialog_kategori = kategori_dialog.findViewById<TextView>(R.id.tv_judul_dialog_kategori)

            tv_judul_dialog_kategori.setText("Edit Kategori")
            et_nama_kategori.requestFocus()

            //set emoji popup keyboard
            EmojiManager.install(GoogleEmojiProvider())
            val popup : EmojiPopup = EmojiPopup(kategori_dialog.findViewById(R.id.rootViewDialog), et_emoji_helper, onEmojiClickListener = {
                Log.d("TAG EMOJI", "onCreate: ${it.unicode}")
                tv_emoji.setText(it.unicode)
            })

            tv_emoji.setOnClickListener {
                popup.toggle()
            }

            //set text
            val pengeluaran = user.kategoriPengeluaran[i]
            tv_emoji.setText(pengeluaran.icon)
            et_nama_kategori.setText(pengeluaran.nama)
            rd_pengeluaran.isChecked = true
            radio_grup.visibility = View.GONE
            btn_tambah_kategori.setText("simpan")

            //set tambah kategori action
            btn_tambah_kategori.setOnClickListener {
                var kembar = false
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
                }else{
                    if (user.kategoriPengeluaran[i].nama != nama_kategori){
                        kembar = user.isExsistKategori(jenis, nama_kategori)

                        if (kembar){
                            et_nama_kategori.setError("Nama kategori harus unique")
                        }
                    }

                }


                // add kategori to user
                if (nama_kategori.isNotBlank() && radio_grup.checkedRadioButtonId != -1 && !kembar){
                    //tambah kategori ke user
                    user.editKategoriPengeluaran(i, nama_kategori, icon)
                    load_list_adapter(user.kategoriPengeluaran, listKategoriPengeluaran)
                    adapter.notifyDataSetChanged()

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

            kategori_dialog.show()
        }
    }

    fun load_list_adapter(allKategori : ArrayList<KategoriPengeluaran>, listKategoriPengeluaran : ArrayList<String>){
        listKategoriPengeluaran.clear()
        for (kategori in allKategori){
            listKategoriPengeluaran.add(kategori.icon + " " + kategori.nama)
        }
    }


}