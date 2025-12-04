package com.example.restro.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.restro.R
import com.example.restro.databinding.FragmentMenuItemsFragmentsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuItemsFragments : Fragment(R.layout.fragment_menu_items_fragments) {
    private var _binding: FragmentMenuItemsFragmentsBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuItemsFragmentsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}