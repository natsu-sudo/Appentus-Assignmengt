package com.assignment.appentus.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.assignment.appentus.R
import com.assignment.appentus.databinding.FragmentPlacesBinding
import com.assignment.appentus.viemodel.ImageViewModel
import com.assignment.appentus.viemodel.ViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class Places : Fragment() {

    private  val TAG = "Places"
    private var _binding:FragmentPlacesBinding?=null
    private val binding get() = _binding!!
    private lateinit var imageAdapter:ImageAdapter
    lateinit var listViewModel: ImageViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listViewModel=activity?.run {
            ViewModelProvider(viewModelStore, ViewModelFactory(this))[ImageViewModel::class.java]
        }!!
        Log.d(TAG, "onCreate: ")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentPlacesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: ")
        super.onViewCreated(view, savedInstanceState)
        imageAdapter= ImageAdapter()
        listViewModel.fetchFromNetwork(1)
        binding.imageUrlRecyclerView.apply {
            layoutManager=GridLayoutManager(activity,2)
            adapter=imageAdapter
            setHasFixedSize(true);
            hasFixedSize()
        }
        lifecycleScope.launch {
            listViewModel.imageURL.collect {
               imageAdapter.submitData(it)
                if (imageAdapter.itemCount==0){
                    Log.d(TAG, "onViewCreated: bitch")
                }
            }
        }

    }


}