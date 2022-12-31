package com.example.proyek_android.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyek_android.Classes.RupiahFormater
import com.example.proyek_android.Classes.SumberDana
import com.example.proyek_android.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class AdapterSumberDana(private val listSumberDana: ArrayList<SumberDana>) : RecyclerView.Adapter<AdapterSumberDana.ListViewHolder>() {

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var _nama_sumber_dana : TextView = itemView.findViewById(R.id.tv_nama_sumber_dana)
        var _nominal : TextView = itemView.findViewById(R.id.tv_nominal)
        var _icon : de.hdodenhof.circleimageview.CircleImageView = itemView.findViewById(R.id.icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.item_sumber_dana, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var sumberDana = listSumberDana[position]

        val rupiahFormater = RupiahFormater()

        holder._nama_sumber_dana.setText(sumberDana.nama)
        holder._nominal.setText(rupiahFormater.format(sumberDana.jumlah))

        //image
        var storageRef: StorageReference = Firebase.storage.getReference()
        sumberDana.icon?.let {
            storageRef.child(sumberDana.icon!!).downloadUrl.addOnSuccessListener {
                Glide.with(holder.itemView).load(it).into(holder._icon)
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, com.example.proyek_android.sumberDana::class.java)
            intent.putExtra("status", "edit")
            intent.putExtra("index", position)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listSumberDana.size
    }
}