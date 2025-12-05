package com.example.restro.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.restro.R
import com.example.restro.databinding.FragmentMenuItemsFragmentsBinding
import com.example.restro.view.adapters.MenuTabsAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuItemsFragments : Fragment(R.layout.fragment_menu_items_fragments) {
    private var _binding: FragmentMenuItemsFragmentsBinding? = null

    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = MenuTabsAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Add Category"
                1 -> "Add Menu Item"
                else -> ""
            }
        }.attach()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuItemsFragmentsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}