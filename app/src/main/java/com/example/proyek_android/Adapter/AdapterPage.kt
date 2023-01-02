package com.example.proyek_android.Adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.proyek_android.Fragment.KategoriPendapatanFragment
import com.example.proyek_android.Fragment.KategoriPengeluaranFragment

class AdapterPage(list : ArrayList<Fragment>, fm : FragmentManager, lifecycle : Lifecycle) : FragmentStateAdapter (fm, lifecycle) {

    val fragmentList = list

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    fun test(){
        this.notifyItemChanged(0)
    }
}