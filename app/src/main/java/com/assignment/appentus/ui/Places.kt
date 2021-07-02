package com.assignment.appentus.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.assignment.appentus.Constants
import com.assignment.appentus.R
import com.assignment.appentus.UserSharedPreferences
import com.assignment.appentus.databinding.FragmentPlacesBinding
import com.assignment.appentus.pojo.ErrorCode
import com.assignment.appentus.pojo.Status
import com.assignment.appentus.viemodel.ImageViewModel
import com.assignment.appentus.viemodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


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
        settingUpTotalCountInDb()
        settingUpStatusListener()
        settingUpPagination()
        swipeUpToRefresh()
    }

    private fun settingUpPagination() {
        lifecycleScope.launch {
            listViewModel.imageURL.collect {
                imageAdapter.submitData(it)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun swipeUpToRefresh() {
        binding.swipeUp.setOnRefreshListener {
            if (listViewModel.getTotalCount.value!=0 && isOnline(requireActivity())){
                listViewModel.deleteData()
                val sharedPreferences=UserSharedPreferences.initializeSharedPreferencesForSavedPage(
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
                    isScrolling = false
                }
                (Status.ERROR) -> {
                    binding.loadingStatusText.visibility = View.VISIBLE
                    showError(loadingStatus.error, loadingStatus.message)
                }
            }
            binding.swipeUp.isRefreshing = false
        })
    }

    private fun settingUpTotalCountInDb() {
        listViewModel.getTotalCount.observe(viewLifecycleOwner, Observer {
            if (it == 0) {
                val pageNumber = UserSharedPreferences.initializeSharedPreferencesForSavedPage(
                        requireActivity()
                )
                        .getInt(Constants.SAVED_PAGE, 1)
                listViewModel.fetchFromNetwork(pageNumber)
            }
        })
    }

    private fun settingUpRecyclerView() {
        binding.imageUrlRecyclerView.apply {
            layoutManager=GridLayoutManager(activity, 2)
            adapter=imageAdapter
            setHasFixedSize(false)
            isNestedScrollingEnabled=false
            hasFixedSize()
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val totalItemCount = (layoutManager as GridLayoutManager).itemCount
                    val lastVisibleItem = (layoutManager as GridLayoutManager).findLastVisibleItemPosition()+1
                    if (dy>0){
                        if (isScrolling && lastVisibleItem >= totalItemCount) {
                            isScrolling = false
                            Log.d(TAG, "onScrolled: wah $totalItemCount")
                            val pageNumber = UserSharedPreferences.initializeSharedPreferencesForSavedPage(
                                    requireActivity()
                            ).getInt(Constants.SAVED_PAGE, 1)
                            listViewModel.fetchFromNetwork(pageNumber)
                        }
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        isScrolling = true
                    }
                }

            })
        }
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