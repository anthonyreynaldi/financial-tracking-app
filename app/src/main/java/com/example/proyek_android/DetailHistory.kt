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
import com.example.proyek_android.Classes.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DetailHistory : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    companion object {
        var item = ItemHistory()
    }

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
    private var selectedKategori : String? = item.kategori
    private var selectedSumberDana : String? = item.sumberDana

    private var selectedIndexSumberDana : Int = 0

    var day = 0; var month = 0; var year = 0; var hour = 0; var minute = 0;
    var savedDay = 0; var savedMonth = 0; var savedYear = 0; var savedHour = 0; var savedMinute = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_history)
        supportActionBar?.hide() // hide toolbar

        val db : FirebaseFirestore = FirebaseFirestore.getInstance()

        // get list sumber dana dan kategori
        siapkanListSumberDana()
        siapkanListKategori()

        // input nama
        _etNama = findViewById(R.id.etNama)
        _etNama.setText(item.nama)

        // input nominal
        _etNominal = findViewById(R.id.etNominal)
        _etNominal.addTextChangedListener(MoneyTextWatcher(_etNominal))
        val nominalString = RupiahFormater().format(item.nominal).toString()
        _etNominal.setText(nominalString.subSequence(3, nominalString.length))

        // input tanggal
        _etTanggal = findViewById(R.id.etTanggal)
        _etTanggal.setOnClickListener {
            getDateTimeCalendar()
            DatePickerDialog(this, R.style.MyDatePickerDialogTheme,this, year, month, day).show()
        }
        _etTanggal.setText(item.tanggal)

        // input deskripsi
        _etDeskrispi = findViewById(R.id.etDeskripsi)
        _etDeskrispi.setText(item.deskripsi)

        // save edited item
        val btnSave = findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener {
            // cek data sudah lengkap atau belum
            if (_etNama.text.toString() == "" ||
                _etNominal.text.toString() == "" || _etNominal.text.toString() == "0" ||
                selectedSumberDana == "" || _etTanggal.text.toString() == "") {
                Toast.makeText(this, "Data belum lengkap", Toast.LENGTH_SHORT).show()
            } else {
                val nominal = _etNominal.text.toString()
                val nominalInt = nominal.replace(".", "").toInt()

                // get index sumber dana
                val indexSumberDana: Int = itemSumberDana.indexOf(selectedSumberDana)

                if (item.jenis == "Pengeluaran") {
                    // create object pengeluaran
                    val pengeluaran = Pengeluaran(_etNama.text.toString(), nominalInt,
                        selectedKategori, selectedSumberDana, _etTanggal.text.toString(), _etDeskrispi.text.toString())

                    val saldo : Int = homepage.user.SumberDana[indexSumberDana].jumlah
                    val (needWarning, msg) = cekPengeluaran(saldo, nominalInt, getTotalPengeluaran(), homepage.user.targetPengeluaran)
                    if (needWarning) {
                        openWarningDialog(db, pengeluaran, item.index, msg)
                    } else {
                        saveItemPengeluaran(db, pengeluaran, item.index)
                    }
                } else if (item.jenis == "Pemasukkan") {
                    // create object pemasukkan
                    val pemasukkan = Pemasukkan(_etNama.text.toString(), nominalInt,
                        selectedKategori, selectedSumberDana, _etTanggal.text.toString(), _etDeskrispi.text.toString())

                    // jika sumber dana yang dipilih adalah Tabungan
                    if (homepage.user.SumberDana[indexSumberDana].nama == "Tabungan") {
                        val nominalTabungan = homepage.user.SumberDana[indexSumberDana].jumlah + pemasukkan.nominal

                        // show dialog kalau sudah mencapai target menabung
                        if (nominalTabungan >= homepage.user.targetTabungan) {
                            openCongratsDialog(db, pemasukkan, item.index)
                        } else {
                            saveItemPemasukkan(db, pemasukkan,  item.index)
                        }
                    } else {
                        saveItemPemasukkan(db, pemasukkan,  item.index)
                    }
                }
            }
        }

        // delete item
        val btnDelete = findViewById<Button>(R.id.btnDelete)
        btnDelete.setOnClickListener {
            openDeleteDialog(db, item.index)
        }

        // btn back
        val btn_back = findViewById<ImageView>(R.id.btn_back)
        btn_back.setOnClickListener{
            startActivity(Intent(this, History::class.java))
            finishAffinity()
        }
    }

    // function untuk save item pemasukkan di database
    fun saveItemPemasukkan(db: FirebaseFirestore, pemasukkan: Pemasukkan, index: Int) {
        // create map
        val map = mutableMapOf<String, Any>()

        // get index sumber dana
        val indexSumberDanaLama: Int = itemSumberDana.indexOf(item.sumberDana)
        val indexSumberDanaBaru: Int = itemSumberDana.indexOf(pemasukkan.sumberDana)

        // replace item
        homepage.user.listPemasukkan[index] = pemasukkan

        // update saldo sumber dana
        // cek apakah ganti sumber dana atau nggak
        if (indexSumberDanaLama == indexSumberDanaBaru) {
            homepage.user.SumberDana[indexSumberDanaBaru].jumlah -= item.nominal // reset nominal
            homepage.user.SumberDana[indexSumberDanaBaru].jumlah += pemasukkan.nominal
        } else {
            // update saldo di sumber dana lama
            homepage.user.SumberDana[indexSumberDanaLama].jumlah -= item.nominal // reset nominal
            homepage.user.SumberDana[indexSumberDanaBaru].jumlah += pemasukkan.nominal
        }

        // update total sumber dana
        homepage.user.totalSumberDana = homepage.user.getTotalSumberDana()

        map["listPemasukkan"] = homepage.user.listPemasukkan
        map["sumberDana"] = homepage.user.SumberDana
        map["totalSumberDana"] = homepage.user.totalSumberDana

        // add map to database
        db.collection(homepage.collectionName).document(homepage.documentName)
            .set(map, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("TAG FIREBASE", "Data berhasil dihapus!")
                openSuccessDialog("Data item berhasil diubah")
            }
            .addOnFailureListener {
                Log.d("TAG FIREBASE", it.message.toString())
            }
    }

    // function untuk save item pengeluaran di database
    fun saveItemPengeluaran(db: FirebaseFirestore, pengeluaran: Pengeluaran, index: Int) {
        // create map
        val map = mutableMapOf<String, Any>()

        // get index sumber dana
        val indexSumberDanaLama: Int = itemSumberDana.indexOf(item.sumberDana)
        val indexSumberDanaBaru: Int = itemSumberDana.indexOf(pengeluaran.sumberDana)

        // replace item
        homepage.user.listPengeluaran[index] = pengeluaran

        // update saldo sumber dana
        // cek apakah ganti sumber dana atau nggak
        if (indexSumberDanaLama == indexSumberDanaBaru) {
            homepage.user.SumberDana[indexSumberDanaBaru].jumlah += item.nominal // reset nominal
            homepage.user.SumberDana[indexSumberDanaBaru].jumlah -= pengeluaran.nominal
        } else {
            // update saldo di sumber dana lama
            homepage.user.SumberDana[indexSumberDanaLama].jumlah += item.nominal // reset nominal
            homepage.user.SumberDana[indexSumberDanaBaru].jumlah -= pengeluaran.nominal
        }

        // update total sumber dana
        homepage.user.totalSumberDana = homepage.user.getTotalSumberDana()

        map["listPengeluaran"] = homepage.user.listPengeluaran
        map["sumberDana"] = homepage.user.SumberDana
        map["totalSumberDana"] = homepage.user.totalSumberDana

        // add map to database
        db.collection(homepage.collectionName).document(homepage.documentName)
            .set(map, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("TAG FIREBASE", "Data berhasil dihapus!")
                openSuccessDialog("Data item berhasil diubah")
            }
            .addOnFailureListener {
                Log.d("TAG FIREBASE", it.message.toString())
            }
    }

    // function untuk hapus item di database
    fun deleteItem(db: FirebaseFirestore, index: Int) {
        // create map
        val map = mutableMapOf<String, Any>()

        // get index sumber dana
        val indexSumberDana: Int = itemSumberDana.indexOf(item.sumberDana)

        if (item.jenis == "Pengeluaran") {
            // hapus item
            homepage.user.listPengeluaran.removeAt(index)
            map["listPengeluaran"] = homepage.user.listPengeluaran

            // update saldo sumber dana
            homepage.user.SumberDana[indexSumberDana].jumlah += item.nominal
        } else if (item.jenis == "Pemasukkan") {
            // hapus item
            homepage.user.listPemasukkan.removeAt(index)
            map["listPemasukkan"] = homepage.user.listPemasukkan

            // update saldo sumber dana
            homepage.user.SumberDana[indexSumberDana].jumlah -= item.nominal
        }

        // update total sumber dana
        homepage.user.totalSumberDana = homepage.user.getTotalSumberDana()

        map["sumberDana"] = homepage.user.SumberDana
        map["totalSumberDana"] = homepage.user.totalSumberDana

        // add map to database
        db.collection(homepage.collectionName).document(homepage.documentName)
            .set(map, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("TAG FIREBASE", "Data berhasil dihapus!")
                openSuccessDialog("Data item berhasil dihapus")
            }
            .addOnFailureListener {
                Log.d("TAG FIREBASE", it.message.toString())
            }
    }

    // function untuk cek pengeluaran user
    fun cekPengeluaran(saldo: Int, nominal: Int, total: Int, target: Int): Pair<Boolean, String>{
        var needWarning = true
        var msg = ""

        if ((nominal > saldo) && (total >= (0.75 * target) && total < target) && target != 0) {
            msg = "Pengeluaran melebih pemasukkan dan mendekati target pengeluaran"
        }

        else if ((nominal > saldo) && (total >= target) && target != 0) {
            msg = "Pengeluaran melebih pemasukkan dan target pengeluaran"
        }

        // cek total pengeluaran dengan target pengeluaran
        // target != 0 --> user harus set target pengeluaran
        // 3/4 target <= total pengeluaran < target --> artinya mendekati target
        else if ((total >= (0.75 * target) && total < target) && target != 0) {
            msg = "Pengeluaran anda telah mendekati target pengeluaran"
        }

        // total pengeluaran sudah sama/melebihi target
        else if ((total >= target) && target != 0) {
            msg = "Pengeluaran anda sudah melebihi target pengeluaran"
        }

        // cek nominal pengeluaran lebih besar dari saldo sumber dana
        else if (nominal > saldo) {
            msg = "Pengeluaran anda lebih besar dari pemasukkan anda"
        }

        else {
            needWarning = false
        }

        return needWarning to msg
    }

    // function untuk menghitung total pengeluaran user
    fun getTotalPengeluaran() : Int {
        var total = 0
        for (pengeluaran in homepage.user.listPengeluaran) {
            total += pengeluaran.nominal
        }

        return total
    }

    private fun openSuccessDialog(msg: String) {
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
        desc.text = msg

        val btnOke = dialogBinding.findViewById<AppCompatButton>(R.id.btnOke)
        btnOke.setBackgroundResource(R.drawable.dialogcontinuebutton)
        btnOke.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.success)))
        btnOke.setOnClickListener {
            // clearInput()
            dialog.dismiss()

            // intent to History
            startActivity(Intent(this, History::class.java))
            finishAffinity()
        }
    }

    private fun openDeleteDialog(db: FirebaseFirestore, index: Int) {
        val dialogBinding = layoutInflater.inflate(R.layout.custom_dialog_negative, null)

        val dialog = Dialog(this)
        dialog.setContentView(dialogBinding)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val desc = dialogBinding.findViewById<TextView>(R.id.textView7)
        desc.text = "Apakah anda ingin menghapus item ini?"

        val btnCancel = dialogBinding.findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        val btnContinue = dialogBinding.findViewById<Button>(R.id.btnContinue)
        btnContinue.setOnClickListener {
            dialog.dismiss()
            deleteItem(db, index)
        }
    }

    private fun openCongratsDialog(db: FirebaseFirestore, pemasukkan: Pemasukkan, index: Int) {
        val dialogBinding = layoutInflater.inflate(R.layout.custom_dialog_positive, null)

        val dialog = Dialog(this)
        dialog.setContentView(dialogBinding)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val btnOke = dialogBinding.findViewById<Button>(R.id.btnOke)
        btnOke.setOnClickListener {
            dialog.dismiss()
            saveItemPemasukkan(db, pemasukkan, index)
        }
    }

    private fun openWarningDialog(db: FirebaseFirestore, pengeluaran: Pengeluaran, index: Int, description: String) {
        val dialogBinding = layoutInflater.inflate(R.layout.custom_dialog_negative, null)

        val dialog = Dialog(this)
        dialog.setContentView(dialogBinding)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val desc = dialogBinding.findViewById<TextView>(R.id.textView7)
        desc.text = description

        val btnCancel = dialogBinding.findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        val btnContinue = dialogBinding.findViewById<Button>(R.id.btnContinue)
        btnContinue.setOnClickListener {
            dialog.dismiss()
            saveItemPengeluaran(db, pengeluaran, index)
        }
    }

    // function untuk get list sumber dana yang dimiliki user
    fun siapkanListSumberDana() {
        for (sumberDana in homepage.user.SumberDana) {
            itemSumberDana.add(sumberDana.nama)
        }

        // select sumber dana
        _selectSumberDana = findViewById(R.id.selectSumberDana)
        _selectSumberDana.setText(item.sumberDana)
        listAdapter2 = ArrayAdapter(this, R.layout.list_select_menu, itemSumberDana)
        _selectSumberDana.setAdapter(listAdapter2)
        _selectSumberDana.setOnItemClickListener{ adapterView, view, i, l ->
            selectedSumberDana = adapterView.getItemAtPosition(i).toString()
            selectedIndexSumberDana = i
        }
    }

    // function untuk get list kategori yang dimiliki user
    fun siapkanListKategori() {
        for (kategori in homepage.user.kategoriPengeluaran) {
            itemKategori.add(kategori.nama)
        }

        // select kategori
        _selectKategori = findViewById(R.id.selectKategori)
        _selectKategori.setText(item.kategori)
        listAdapter1 = ArrayAdapter(this, R.layout.list_select_menu, itemKategori)
        _selectKategori.setAdapter(listAdapter1)
        _selectKategori.setOnItemClickListener{ adapterView, view, i, l ->
            selectedKategori = adapterView.getItemAtPosition(i).toString()
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