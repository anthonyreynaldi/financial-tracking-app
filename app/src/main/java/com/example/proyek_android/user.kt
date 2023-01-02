package com.example.proyek_android

import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import com.example.proyek_android.Classes.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.parcelize.Parcelize

class user(
    var Name: String = "",
    var Email: String = "",
    var Phone: String = "",
    var Pass: String = "",
    var SumberDana : ArrayList<SumberDana> = ArrayList(),
    var targetPengeluaran : Int = 0,
    var targetTabungan : Int = 0,
    var totalSumberDana : Int = 0,
    var listPengeluaran : ArrayList<Pengeluaran> = ArrayList(),
    var listPemasukkan : ArrayList<Pemasukkan> = ArrayList(),
    var kategoriPengeluaran : ArrayList<KategoriPengeluaran> = ArrayList(),
    var kategoriPemasukkan : ArrayList<KategoriPemasukkan> = ArrayList(),
) {
    init {
        this.SumberDana.add(SumberDana("TABUNGAN", 0))

        this.kategoriPengeluaran.add(KategoriPengeluaran("Makan", "🍽️"))
        this.kategoriPengeluaran.add(KategoriPengeluaran("Rumah Tangga", "🛒"))
        this.kategoriPengeluaran.add(KategoriPengeluaran("Pendidikan", "📖"))
        this.kategoriPengeluaran.add(KategoriPengeluaran("Donasi", "🙏"))
        this.kategoriPengeluaran.add(KategoriPengeluaran("Cicilan", "💰"))
        this.kategoriPengeluaran.add(KategoriPengeluaran("Lain-lain", "😱"))

        this.kategoriPemasukkan.add(KategoriPemasukkan("Gaji", "💸"))
        this.kategoriPemasukkan.add(KategoriPemasukkan("Toko Online", "🏪"))
        this.kategoriPemasukkan.add(KategoriPemasukkan("Lain-lain", "😱"))
    }
     fun save(): Boolean {
         var success = false
         val db = FirebaseFirestore.getInstance()
         db.collection(homepage.collectionName).document(homepage.documentName).set(this)
             .addOnSuccessListener { documentReference ->
                CoroutineScope(Dispatchers.Main).async{
                    success = true
                }
                 Log.d(
                     "TAG FIREBASE", "DocumentSnapshot added with ID: $documentReference"
                 )
             }
             .addOnFailureListener { e -> Log.w("TAG FIREBASE", "Error adding document", e) }

         return success
     }

    fun isExistSumberDana(nama: String): Boolean{
         for (sumberDana in SumberDana){
             if (sumberDana.nama == nama){
                 return true
             }
         }
         return false
     }

     fun addSumberDana(nama : String, nominal : Int, iconPath: String?): Pair<Boolean, String> {

         //check ada yang kembar gak namae
         if (isExistSumberDana(nama)){
            return Pair(false, "$nama sudah ada di daftar sumber dana Anda")
         }

         //add new object
         this.SumberDana.add(SumberDana(nama, nominal, iconPath))

         //update on fb
         this.save()
         return Pair(true, "Berhasil menambahkan sumber dana")
//         if (this.save()){
//         }else{
//             this.SumberDana.removeAt(this.SumberDana.size-1)
//             return Pair(false, "Terjadi kesalahan")
//         }
     }

    fun editSumberDana(index : Int, nama : String, nominal : Int, iconPath : String?): Pair<Boolean, String>{
        val sumberDana = this.SumberDana[index]

        //check ada yang kembar gak namae
        if (isExistSumberDana(nama) && nama != sumberDana.nama){
            return Pair(false, "$nama sudah ada di daftar sumber dana Anda")
        }

        val namaOld = sumberDana.nama

        //update nama di semua transaksi
        //update pengeluaran
        for (transaksi in listPengeluaran){
            if (transaksi.sumberDana == namaOld){
                transaksi.sumberDana = nama
            }
        }
        //update pemasukan
        for (transaksi in listPemasukkan){
            if (transaksi.sumberDana == namaOld){
                transaksi.sumberDana = nama
            }
        }

        //update nama nominal
        sumberDana.nama = nama
        sumberDana.jumlah = nominal
        sumberDana.icon = iconPath

        this.save()
        return Pair(true, "Berhasil merubah sumber dana")
    }

    fun deleteSumberDana(index: Int){
        this.SumberDana.removeAt(index)
        this.save()
    }

    @JvmName("getTotalSumberDana1")
    fun getTotalSumberDana(): Int {
        var sum = 0
        for (sumberDana in this.SumberDana){
            sum += sumberDana.jumlah
        }
        return sum
    }

    @JvmName("setTargetPengeluaran1")
    fun setTargetPengeluaran(jumlah : Int){
        this.targetPengeluaran = jumlah
        this.save()
    }

    @JvmName("setTargetTabungan1")
    fun setTargetTabungan(jumlah: Int){
        this.targetTabungan = jumlah
        this.save()
    }

    fun isExsistKategori(jenis: String, nama: String): Boolean {
        var listCari = kategoriPengeluaran

        if (jenis == "pemasukan") {
            listCari = kategoriPengeluaran
        }

        for (kategori in listCari){
            if (kategori.nama.lowercase() == nama.lowercase()){
                return true
            }
        }

        return false
    }

    fun addKategori(jenis : String, nama: String, icon : String){

        if (jenis == "pengeluaran"){
            val kategori = KategoriPengeluaran(nama, icon)
            this.kategoriPengeluaran.add(kategori)
        }else if (jenis == "pemasukan"){
            val kategori = KategoriPemasukkan(nama, icon)
            this.kategoriPemasukkan.add(kategori)
        }

        this.save()
    }

    fun editKategoriPengeluaran(index: Int, nama: String, icon: String){
        val kategori = this.kategoriPengeluaran[index]

        val namaOld = kategori.nama

        //update pengeluaran
        for (transaksi in listPengeluaran){
            if (transaksi.sumberDana == namaOld){
                transaksi.sumberDana = nama
            }
        }

        kategori.nama = nama
        kategori.icon = icon

        this.save()
    }

    fun editKategoriPemasukan(index: Int, nama: String, icon: String){
        val kategori = this.kategoriPemasukkan[index]

        val namaOld = kategori.nama

        //update pemasukan
        for (transaksi in listPemasukkan){
            if (transaksi.sumberDana == namaOld){
                transaksi.sumberDana = nama
            }
        }

        kategori.nama = nama
        kategori.icon = icon

        this.save()
    }
}
