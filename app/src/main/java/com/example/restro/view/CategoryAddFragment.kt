package com.example.restro.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.restro.R
import com.example.restro.databinding.FragmentCategoryAddBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryAddFragment : Fragment(R.layout.fragment_category_add) {

    private var _binding: FragmentCategoryAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryAddBinding.inflate(inflater, container, false)
        return binding.root
    }


}