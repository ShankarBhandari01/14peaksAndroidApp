package com.example.restro.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.restro.R
import com.example.restro.databinding.FragmentMenuItemAddBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuItemAddFragment : Fragment(R.layout.fragment_menu_item_add) {
    private var _binding: FragmentMenuItemAddBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuItemAddBinding.inflate(inflater, container, false)
        return binding.root
    }

}