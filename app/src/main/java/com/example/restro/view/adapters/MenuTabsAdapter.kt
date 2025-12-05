package com.example.restro.view.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.restro.view.CategoryAddFragment
import com.example.restro.view.MenuItemAddFragment

class MenuTabsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CategoryAddFragment()
            1 -> MenuItemAddFragment()
            else -> CategoryAddFragment()
        }
    }
}