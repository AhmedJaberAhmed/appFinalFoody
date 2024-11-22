package com.example.finalcooking.fragments

import HomeViewModel
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager

import com.example.finalcooking.activities.CategoryMealsActivity
import com.example.finalcooking.activities.MainActivity
import com.example.finalcooking.adapters.CategoriesAdapter
import com.example.finalcooking.databinding.FragmentCategoriesBinding

class CategoriesFragment : Fragment() {

    private lateinit var binding: FragmentCategoriesBinding
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var viewModel: HomeViewModel

    companion object {
        const val CATEGORY_NAME = "com.example.finalcooking.fragments.categoryName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
        categoriesAdapter = CategoriesAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkNetworkAndUpdateUI()
    }

    private fun checkNetworkAndUpdateUI() {
        if (isConnectedToInternet()) {
            showProgressBar()
            binding.rvCategories.visibility = View.VISIBLE
            binding.noInternet.visibility = View.GONE
            setupUI()
        } else {
            binding.rvCategories.visibility = View.GONE
            binding.noInternet.visibility = View.VISIBLE
        }


        binding.btnRetry.setOnClickListener {
            checkNetworkAndUpdateUI()
        }
    }

    private fun isConnectedToInternet(): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        return networkCapabilities != null &&
                (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
    }

    private fun setupUI() {
        prepareRecyclerView()
        observeCategories()
        onCategoryClick()
    }

    private fun prepareRecyclerView() {
        binding.rvCategories.apply {
            layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
            adapter = categoriesAdapter
        }
    }

    private fun observeCategories() {
        viewModel.observeCategoriesLiveData().observe(viewLifecycleOwner, Observer { categories ->
            categoriesAdapter.setCategoryList(categories)
            hideProgressBar()
        })
    }

    private fun onCategoryClick() {
        categoriesAdapter.onItemClick = { category ->
            val intent = Intent(activity, CategoryMealsActivity::class.java)
            intent.putExtra(CATEGORY_NAME, category.strCategory)
            startActivity(intent)
        }
    }
    private fun showProgressBar() {
        binding.loading.visibility = View.VISIBLE
        binding.rvCategories.visibility = View.INVISIBLE
    }

    private fun hideProgressBar() {
        binding.loading.visibility = View.GONE
        binding.rvCategories.visibility = View.VISIBLE
    }

}
