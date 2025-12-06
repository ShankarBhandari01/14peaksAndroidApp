package com.example.restro.view.adapters

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.restro.view.reservations.OldReservationFragments
import com.example.restro.viewmodel.SalesViewModel


class ReservationTabAdapter(
    fragment: Fragment
) :
    FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OldReservationFragments.newInstance("new")
            1 -> OldReservationFragments.newInstance("old")
            else -> OldReservationFragments.newInstance("new")
        }
    }

    override fun getItemCount(): Int = 2

}