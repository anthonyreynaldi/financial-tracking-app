package com.example.proyek_android.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyek_android.Classes.ItemHistory
import com.example.proyek_android.Classes.RupiahFormater
import com.example.proyek_android.R
import com.example.proyek_android.homepage
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class AdapterHistory (
    private val listHistory: ArrayList<ItemHistory>
) : RecyclerView.Adapter<AdapterHistory.ListViewHolder>() {

    private lateinit var onItemClickCallback : OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data : ItemHistory)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemCard : CardView = itemView.findViewById(R.id.itemCard)

        var tv_kategoriItem : TextView = itemView.findViewById(R.id.tv_kategoriItem)
        var tv_sumberDanaItem : TextView = itemView.findViewById(R.id.tv_sumberDanaItem)
        var tv_namaItem : TextView = itemView.findViewById(R.id.tv_namaItem)
        var tv_tanggalItem : TextView = itemView.findViewById(R.id.tv_tanggalItem)
        var tv_nominalItem : TextView = itemView.findViewById(R.id.tv_nominalItem)

        var tagKategori : LinearLayout = itemView.findViewById(R.id.tagKategori)

        var iconItem : ImageView = itemView.findViewById(R.id.iconHistory)

        var icon_sumber_dana_history : de.hdodenhof.circleimageview.CircleImageView = itemView.findViewById(R.id.icon_sumber_dana_history)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var itemHistory = listHistory[position]

        holder.tv_kategoriItem.setText(itemHistory.kategori)
        holder.tv_sumberDanaItem.setText(itemHistory.sumberDana)
        holder.tv_namaItem.setText(itemHistory.nama)
        holder.tv_tanggalItem.setText(itemHistory.tanggal)

        val rupiahFormater = RupiahFormater()
        holder.tv_nominalItem.setText(rupiahFormater.format(itemHistory.nominal).toString())

        if (itemHistory.kategori == "") {
            holder.tagKategori.visibility = View.GONE
        }

        if (itemHistory.jenis == "Pengeluaran") {
            val context = holder.itemView.context
            val imageRes = context.resources.getIdentifier("icon_arrow_down", "drawable", context.packageName)
            holder.iconItem.setImageResource(imageRes)

            holder.tv_nominalItem.setTextColor(Color.parseColor("#AA0505"))
        } else {
            val context = holder.itemView.context
            val imageRes = context.resources.getIdentifier("icon_arrow_up", "drawable", context.packageName)
            holder.iconItem.setImageResource(imageRes)

            holder.tv_nominalItem.setTextColor(Color.parseColor("#255E31"))
        }

        //image
        //search path sumber dana
        for (sumberDana in homepage.user.SumberDana){
            if (sumberDana.nama == itemHistory.sumberDana){

                var storageRef: StorageReference = Firebase.storage.getReference()
                sumberDana.icon?.let {
                    storageRef.child(sumberDana.icon!!).downloadUrl.addOnSuccessListener {
                        Glide.with(holder.itemView).load(it).into(holder.icon_sumber_dana_history)
                    }
                }
                break
            }
        }

        holder.itemCard.setOnClickListener {
            onItemClickCallback.onItemClicked(itemHistory)
        }
    }

    override fun getItemCount(): Int {
        return listHistory.size
    }

}