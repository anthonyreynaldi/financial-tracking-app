package com.example.proyek_android.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyek_android.Classes.RupiahFormater
import com.example.proyek_android.Classes.SumberDana
import com.example.proyek_android.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class AdapterPilihGambar(private val listPath: ArrayList<String>) : RecyclerView.Adapter<AdapterPilihGambar.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallBack

    interface OnItemClickCallBack {
        fun onItemClicked(path : String)
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var _path : TextView = itemView.findViewById(R.id.tv_path_gambar)
        var _icon : ImageView = itemView.findViewById(R.id.item_icon)
        var _progressBar : ProgressBar = itemView.findViewById(R.id.progressBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.item_gambar, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var path = listPath[position]

        holder._path.setText(path)

        //image
        var storageRef: StorageReference = Firebase.storage.getReference()
        path.let {
            storageRef.child(path).downloadUrl.addOnSuccessListener {
                holder._progressBar.visibility = View.GONE
                Glide.with(holder.itemView).load(it).into(holder._icon)
//                Log.d("TAG FB", "onBindViewHolder: sukses get gambar")
            }
        }

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(path)
        }
    }

    override fun getItemCount(): Int {
        return listPath.size
    }

    fun setOnItemClickCallback(onItemClickCallBack: OnItemClickCallBack){
        this.onItemClickCallback = onItemClickCallBack
    }
}