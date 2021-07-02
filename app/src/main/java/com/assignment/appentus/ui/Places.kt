package com.assignment.appentus.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.assignment.appentus.util.Constants
import com.assignment.appentus.R
import com.assignment.appentus.util.UserSharedPreferences
import com.assignment.appentus.databinding.FragmentPlacesBinding
import com.assignment.appentus.pojo.ErrorCode
import com.assignment.appentus.pojo.Status
import com.assignment.appentus.viemodel.ImageViewModel
import com.assignment.appentus.viemodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar


class Places : Fragment() {

    private  val TAG = "Places"
    private var _binding:FragmentPlacesBinding?=null
    private val binding get() = _binding!!
    private lateinit var imageAdapter:ImageAdapter
    private var isScrolling=false
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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: ")
        super.onViewCreated(view, savedInstanceState)
        imageAdapter= ImageAdapter()

        //setting up Recycler View
        settingUpRecyclerView()
        settingUpStatusListener()
        settingUpPagination()
        swipeUpToRefresh()
    }

    private fun settingUpPagination() {
        listViewModel.getList.observe(viewLifecycleOwner, Observer {
            (binding.imageUrlRecyclerView.adapter as ImageAdapter).submitList(it)
            Log.d(TAG, "settingUpPagination: " + it.size)
            if (it.isEmpty()) {
                listViewModel.fetchFromNetwork(Constants.ONE)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun swipeUpToRefresh() {
        binding.swipeUp.setOnRefreshListener {
            if (listViewModel.getTotalCount.value!=0 && isOnline(requireActivity())){
                listViewModel.deleteData()
                val sharedPreferences= UserSharedPreferences.initializeSharedPreferencesForSavedPage(
                        requireActivity()
                )
                sharedPreferences.edit().putInt(Constants.SAVED_PAGE, 1).apply()
            }else{
                Snackbar.make(binding.root, getString(R.string.network_error), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.ok)) {

                    }
                    .show()
                binding.swipeUp.isRefreshing=false
            }

        }
    }

    private fun settingUpStatusListener() {
        listViewModel.status.observe(viewLifecycleOwner, Observer { loadingStatus ->
            when (loadingStatus?.status) {
                (Status.LOADING) -> {

                    Log.d(TAG, "onViewCreated: Status loading")
                }
                (Status.SUCCESS) -> {
                    Log.d(TAG, "onViewCreated: Status Success")
                    val sharedPreferences =
                            UserSharedPreferences.initializeSharedPreferencesForSavedPage(
                                    requireActivity()
                            )
                    val lastSavedPageNumber = sharedPreferences.getInt(Constants.SAVED_PAGE, 1)
                    sharedPreferences.edit().putInt(Constants.SAVED_PAGE, lastSavedPageNumber + 1)
                            .apply()
                    binding.loadingStatusText.visibility = View.GONE
                }
                (Status.ERROR) -> {
                    binding.loadingStatusText.visibility = View.VISIBLE
                    showError(loadingStatus.error, loadingStatus.message)
                }
            }
            binding.swipeUp.isRefreshing = false
        })
    }

    private fun settingUpRecyclerView() {
        binding.imageUrlRecyclerView.apply {
            layoutManager=GridLayoutManager(activity, 2)
            adapter=imageAdapter
            setHasFixedSize(true)

        }
        binding.imageUrlRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val totalCount = (recyclerView.layoutManager as GridLayoutManager).itemCount
                    val lastVisiblePosition = (recyclerView.layoutManager as GridLayoutManager).findLastVisibleItemPosition()
                    Log.d(TAG, "onScrolled: $totalCount  $lastVisiblePosition")
                    if (!isScrolling && ((totalCount - (lastVisiblePosition + 1)) == 0)) {
                        isScrolling = true
                        val position = lastVisiblePosition-20
                        binding.progressCircular.visibility = View.VISIBLE
                        Handler().postDelayed({
                            binding.progressCircular.visibility = View.GONE
                            Log.d(TAG, "onScrolled: position $position")
                            binding.imageUrlRecyclerView.scrollToPosition(position)
                            isScrolling = false
                        }, Constants.TEN_SECOND)
                        val pageNumber = UserSharedPreferences.initializeSharedPreferencesForSavedPage(
                                requireActivity()
                        ).getInt(Constants.SAVED_PAGE, 1)
                        Log.d(TAG, "onScrolled: $pageNumber")
                        listViewModel.fetchFromNetwork(pageNumber)
                    }
                }
            }
        })
    }

    private fun showError(errorCode: ErrorCode?, message: String?) {
        Log.d(TAG, "showError: ")
        when (errorCode) {
            ErrorCode.NO_DATA -> binding.loadingStatusText.text = getString(R.string.error_no_data)
            ErrorCode.NETWORK_ERROR -> binding.loadingStatusText.text =
                    getString(R.string.error_network)
            ErrorCode.UNKNOWN_ERROR -> binding.loadingStatusText.text =
                    getString(R.string.error_unknown, message)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }


}