package com.example.proyek_android

import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import com.example.proyek_android.Classes.SumberDana
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
    var SumberDana : ArrayList<SumberDana> = ArrayList()
) {
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

        //update nama nominal
        sumberDana.nama = nama
        sumberDana.jumlah = nominal
        sumberDana.icon = iconPath

        //update nama di semua transaksi


        this.save()
        return Pair(true, "Berhasil merubah sumber dana")
    }

    fun deleteSumberDana(index: Int){
        this.SumberDana.removeAt(index)
        this.save()
    }

    fun getTotalSumberDana(): Int {
        var sum = 0
        for (sumberDana in this.SumberDana){
            sum += sumberDana.jumlah
        }
        return sum
    }
}
