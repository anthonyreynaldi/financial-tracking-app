package com.example.proyek_android

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.proyek_android.Adapter.AdapterPilihGambar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.time.LocalDateTime

class pilihGambar : AppCompatActivity() {

    var storageRef: StorageReference = Firebase.storage.getReference()
    private lateinit var listPath : ArrayList<String>
    private lateinit var adapterPilihGambar : AdapterPilihGambar

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilih_gambar)

        val rv_image = findViewById<RecyclerView>(R.id.rv_image)
        val btn_upload_gambar = findViewById<Button>(R.id.btn_upload_gambar)

        listPath = ArrayList<String>()

        rv_image.layoutManager = StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL)

        adapterPilihGambar = AdapterPilihGambar(listPath)
        rv_image.adapter = adapterPilihGambar

        //load all image
        storageRef.child("common").listAll().addOnSuccessListener {
            for (file in it.items){
                listPath.add("common" + "/" + file.name)
            }
            adapterPilihGambar.notifyDataSetChanged()
        }
        storageRef.child(homepage.documentName).listAll().addOnSuccessListener {
            for (file in it.items){
                listPath.add(homepage.documentName + "/" + file.name)
            }
            adapterPilihGambar.notifyDataSetChanged()
        }


        //return path gambar
        adapterPilihGambar.setOnItemClickCallback(object : AdapterPilihGambar.OnItemClickCallBack{
            override fun onItemClicked(path: String) {
                val intent = Intent(this@pilihGambar, sumberDana::class.java)
                intent.putExtra("path", path)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        })

        btn_upload_gambar.setOnClickListener {
            selectImageFromGallery()
        }
    }


    fun selectImageFromGallery() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Please select..."
            ),
            123
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (requestCode == 123
            && resultCode == Activity.RESULT_OK
            && data != null
            && data.data != null
        ) {

            // Get the Uri of data
            val file_uri = data.data
            Log.d("RESULT IMGAE", "onActivityResult: ${file_uri}")

            uploadImageToFirebase(file_uri)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    fun uploadImageToFirebase(file_uri: Uri?){
        // extract the file name with extension
        val sd = getFileName(applicationContext, file_uri!!)

        val date = LocalDateTime.now().toString()
        val pathName = "${homepage.documentName}/$date"

        // Upload Task with upload to directory sesuai emailnya
        // and name of the file adalah waktu saat itu supaya unique
        val uploadTask = storageRef.child(pathName).putFile(file_uri).addOnSuccessListener {
            listPath.add(pathName)
            adapterPilihGambar.notifyDataSetChanged()
        }
    }

    @SuppressLint("Range")
    fun getFileName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null) {
                    if(cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
    }
}