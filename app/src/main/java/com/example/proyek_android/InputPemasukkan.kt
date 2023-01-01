package com.example.proyek_android

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.proyek_android.Classes.Pemasukkan
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class InputPemasukkan : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
//    companion object{
//        var collectionName = "user"
//        var documentName = "wendy@gmail.com"
//        var user = user()
//    }

    private val months = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

    var itemKategori : ArrayList<String> = ArrayList()
    var itemSumberDana : ArrayList<String> = ArrayList()

    private lateinit var _selectKategori : AutoCompleteTextView
    private lateinit var _selectSumberDana : AutoCompleteTextView

    private lateinit var _etNama : EditText
    private lateinit var _etNominal : EditText
    private lateinit var _etTanggal : EditText
    private lateinit var _etDeskrispi : EditText

    lateinit var listAdapter1 : ArrayAdapter<String>
    lateinit var listAdapter2 : ArrayAdapter<String>

    // buat nyimpen option yang dipilih user
    private var selectedKategori : String = ""
    private var selectedSumberDana : String = ""

    private var selectedIndexSumberDana : Int = 0

    var day = 0; var month = 0; var year = 0; var hour = 0; var minute = 0;
    var savedDay = 0; var savedMonth = 0; var savedYear = 0; var savedHour = 0; var savedMinute = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_pemasukkan)
        supportActionBar?.hide() // hide toolbar

        val db : FirebaseFirestore = FirebaseFirestore.getInstance()
        // getDataUser(db, homepage.documentName)

        // get list sumber dana dan kategori
        siapkanListSumberDana()
        siapkanListKategori()

        // input nama
        _etNama = findViewById(R.id.etNama)

        // input nominal
        _etNominal = findViewById(R.id.etNominal)

        // input tanggal
        _etTanggal = findViewById(R.id.etTanggal)
        _etTanggal.setOnClickListener {
            getDateTimeCalendar()
            DatePickerDialog(this, R.style.MyDatePickerDialogTheme,this, year, month, day).show()
        }

        // input deskripsi
        _etDeskrispi = findViewById(R.id.etDeskripsi)

        // button input
        val _btnInputPemasukkan= findViewById<Button>(R.id.btnInputPemasukkan)
        _btnInputPemasukkan.setOnClickListener {
            // cek data sudah lengkap atau belum
            if (_etNama.text.toString() == "" ||
                _etNominal.text.toString() == "" || _etNominal.text.toString() == "0" ||
                selectedSumberDana == "" || _etTanggal.text.toString() == "") {
                Toast.makeText(this, "Data belum lengkap", Toast.LENGTH_SHORT).show()
            } else {
                // add data ke database
                val pemasukkan = Pemasukkan(_etNama.text.toString(), Integer.parseInt(_etNominal.text.toString()),
                    selectedKategori, selectedSumberDana, _etTanggal.text.toString(), _etDeskrispi.text.toString())

                if (homepage.user.SumberDana.get(selectedIndexSumberDana).nama == "Tabungan") {
                    val nominalTabungan = homepage.user.SumberDana.get(selectedIndexSumberDana).jumlah + pemasukkan.nominal

                    // show dialog kalau sudah mencapai target menabung
                    if (nominalTabungan >= homepage.user.targetTabungan) {
                        openCongratsDialog(db, pemasukkan)
                    } else {
                        addDataPemasukkan(db, pemasukkan)
                    }
                } else {
                    addDataPemasukkan(db, pemasukkan)
                }
            }
        }
    }

    // function untuk get list sumber dana yang dimiliki user
    fun siapkanListSumberDana() {
        for (sumberDana in homepage.user.SumberDana) {
            itemSumberDana.add(sumberDana.nama)
        }

        // select sumber dana
        _selectSumberDana = findViewById(R.id.selectSumberDana)
        listAdapter2 = ArrayAdapter(this, R.layout.list_select_menu, itemSumberDana)
        _selectSumberDana.setAdapter(listAdapter2)
        _selectSumberDana.setOnItemClickListener{ adapterView, view, i, l ->
            selectedSumberDana = adapterView.getItemAtPosition(i).toString()
            selectedIndexSumberDana = i
        }
    }

    // function untuk get list kategori yang dimiliki user
    fun siapkanListKategori() {
        for (kategori in homepage.user.kategoriPemasukkan) {
            itemKategori.add(kategori.nama)
        }

        // select kategori
        _selectKategori = findViewById(R.id.selectKategori)
        listAdapter1 = ArrayAdapter(this, R.layout.list_select_menu, itemKategori)
        _selectKategori.setAdapter(listAdapter1)
        _selectKategori.setOnItemClickListener{ adapterView, view, i, l ->
            selectedKategori = adapterView.getItemAtPosition(i).toString()
        }
    }

    // function untuk tambah data pemasukkan baru ke database
    fun addDataPemasukkan(db: FirebaseFirestore, pemasukkan: Pemasukkan) {
        // add object Pemasukkan
        homepage.user.listPemasukkan.add(pemasukkan)

        // update saldo sumber dana
        homepage.user.SumberDana.get(selectedIndexSumberDana).jumlah += pemasukkan.nominal

        // create map
        val map = mutableMapOf<String, Any>()
        map["pemasukkan"] = homepage.user.listPemasukkan
        map["sumberDana"] = homepage.user.SumberDana

        // add map to database
        db.collection(homepage.collectionName).document(homepage.documentName)
            .set(map, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("Firebase", "Simpan Data Berhasil!")
                openSuccessDialog()
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }

    // function untuk get data user dari database
//    fun getDataUser(db: FirebaseFirestore, email: String){
//        db.collection(homepage.collectionName).document(email)
//            .get()
//            .addOnCompleteListener {
//                if (it.isSuccessful) {
//                    val document = it.result
//                    if (document != null) {
//                        homepage.user = document.toObject(user::class.java)!!
//
//                        // get list sumber dana dan kategori
//                        // dikasih delay supaya data user terambil dulu sebelum ditampilkan di view
//                        runBlocking {
//                            launch {
//                                delay(1000L)
//                                siapkanListSumberDana()
//                                siapkanListKategori()
//                            }
//                        }
//                    } else {
//                        Log.d("Firebase", "No such document")
//                    }
//                } else {
//                    Log.d("Firebase", "get failed with ", it.exception)
//                }
//            }
//            .addOnFailureListener {
//                Log.d("Firebase", it.message.toString())
//            }
//    }

    // function untuk clear input
    fun clearInput() {
        _etNama.setText("")
        _etNominal.setText("")
        _etDeskrispi.setText("")
        _etTanggal.setText("")

        _selectKategori.setText("")
        _selectSumberDana.setText("")

        selectedKategori = ""
        selectedSumberDana = ""
        selectedIndexSumberDana = 0
    }

    private fun openSuccessDialog() {
        val dialogBinding = layoutInflater.inflate(R.layout.custom_dialog_positive, null)

        val dialog = Dialog(this)
        dialog.setContentView(dialogBinding)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val dialogBody = dialogBinding.findViewById<ConstraintLayout>(R.id.dialogBody)
        dialogBody.setBackgroundColor(Color.argb(255, 205, 242, 202))

        val image = dialogBinding.findViewById<ImageView>(R.id.imageView)
        image.setImageResource(R.drawable.icon_success)

        val title = dialogBinding.findViewById<TextView>(R.id.textView7)
        title.text = "Success!"

        val desc = dialogBinding.findViewById<TextView>(R.id.textView8)
        desc.text = "Data pemasukkan berhasil ditambahkan"

        val btnOke = dialogBinding.findViewById<AppCompatButton>(R.id.btnOke)
        btnOke.setBackgroundResource(R.drawable.dialogcontinuebutton)
        btnOke.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.success)))
        btnOke.setOnClickListener {
            clearInput()
            dialog.dismiss()

            // intent to homepage
            startActivity(Intent(this, homepage::class.java))
            finishAffinity()
        }
    }

    private fun openCongratsDialog(db: FirebaseFirestore, pemasukkan: Pemasukkan) {
        val dialogBinding = layoutInflater.inflate(R.layout.custom_dialog_positive, null)

        val dialog = Dialog(this)
        dialog.setContentView(dialogBinding)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val btnOke = dialogBinding.findViewById<Button>(R.id.btnOke)
        btnOke.setOnClickListener {
            dialog.dismiss()
            addDataPemasukkan(db, pemasukkan)
        }
    }

    // function untuk get current date dan time
    private fun getDateTimeCalendar() {
        val cal : Calendar = Calendar.getInstance()

        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }

    // function untuk set tanggal yang dipilih
    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        savedDay = p3
        savedMonth = p2
        savedYear = p1

        // convert tanggal menjadi hari
        val simpleDateFormat = SimpleDateFormat("EEEE")
        val date = Date(savedYear, savedMonth, savedDay - 1)
        val dayString = simpleDateFormat.format(date)

        _etTanggal.setText("$dayString, $savedDay ${months.get(savedMonth)} $savedYear")

        // getDateTimeCalendar()
        // TimePickerDialog(this, this, hour, minute, true).show()
    }

    // function untuk set waktu yang dipilih
    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        savedHour = p1
        savedMinute = p2

        _etTanggal.setText("$savedDay/$savedMonth/$savedYear $savedHour:$savedMinute")
    }
}