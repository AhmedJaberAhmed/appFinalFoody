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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.finalcooking.R
import com.example.finalcooking.activities.CategoryMealsActivity
import com.example.finalcooking.activities.MainActivity
import com.example.finalcooking.activities.MealActivity
import com.example.finalcooking.adapters.CategoriesAdapter
import com.example.finalcooking.adapters.MostPopularAdapter
import com.example.finalcooking.databinding.FragmentHomeBinding
import com.example.finalcooking.fragments.bottomsheet.MealBottomSheetFragment
import com.example.finalcooking.pojo.Meal

class HomeFragment : Fragment() {

    private lateinit var randomMeal: Meal
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var popularItemsAdapter: MostPopularAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter
    private var isRandomMealLoaded = false
    private var isPopularMealsLoaded = false

    companion object {
        const val MEAL_ID = "com.example.finalcooking.fragments.idMeal"
        const val MEAL_NAME = "com.example.finalcooking.fragments.nameMeal"
        const val MEAL_THUMB = "com.example.finalcooking.fragments.thumbMeal"
        const val CATEGORY_NAME = "com.example.finalcooking.fragments.categoryName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        popularItemsAdapter = MostPopularAdapter()
        categoriesAdapter = CategoriesAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkNetworkAndUpdateUI()
    }

    private fun checkNetworkAndUpdateUI() {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        val isConnected = networkCapabilities != null &&
                (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))

        if (isConnected) {
            binding.nestedScrollView.visibility = View.VISIBLE
            binding.noInternet.visibility = View.GONE
            binding.loading.visibility = View.VISIBLE
            showProgressBar()
            setupUI()
        } else {
            binding.nestedScrollView.visibility = View.GONE
            binding.noInternet.visibility = View.VISIBLE
            binding.loading.visibility = View.GONE
        }


        binding.btnRetry.setOnClickListener {
            checkNetworkAndUpdateUI()
        }
    }

    private fun setupUI() {
        prepareCategoriesRecyclerView()
        preparePopularItemsRecyclerView()

        viewModel.getRandomMeal()
        observeRandomMeal()
        onRandomMealClick()

        viewModel.getPopularItems()
        observePopularItemsLiveData()
        onPopularItemClick()

        viewModel.getCategories()
        observeCategoriesLiveData()
        onCategoryClick()

        onPopularItemLongClick()
        onSearchIconClick()
    }

    private fun showProgressBar() {

        binding.tvHome.visibility = View.INVISIBLE
        binding.tvCategories.visibility = View.INVISIBLE
        binding.randomMealCard.visibility = View.INVISIBLE
        binding.recViewCategories.visibility = View.INVISIBLE
        binding.tvOverPopular.visibility = View.INVISIBLE
        binding.recViewMealsPopular.visibility = View.INVISIBLE
        binding.imgSearch.visibility = View.INVISIBLE
        binding.tvWouldLikeToEat.visibility = View.INVISIBLE
    }

    private fun hideProgressBar() {

        if (isRandomMealLoaded && isPopularMealsLoaded) {
            binding.loading.visibility = View.GONE
            binding.tvHome.visibility = View.VISIBLE
            binding.tvCategories.visibility = View.VISIBLE
            binding.randomMealCard.visibility = View.VISIBLE
            binding.recViewCategories.visibility = View.VISIBLE
            binding.tvOverPopular.visibility = View.VISIBLE
            binding.recViewMealsPopular.visibility = View.VISIBLE
            binding.imgSearch.visibility = View.VISIBLE
            binding.tvWouldLikeToEat.visibility = View.VISIBLE
        }
    }

    private fun onSearchIconClick() {
        binding.imgSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun onPopularItemLongClick() {
        popularItemsAdapter.onLongItemClick = { meal ->
            val mealBottomSheetFragment = MealBottomSheetFragment.newInstance(meal.idMeal)
            mealBottomSheetFragment.show(childFragmentManager, "Meal Info")
        }
    }

    private fun onCategoryClick() {
        categoriesAdapter.onItemClick = { category ->
            val intent = Intent(activity, CategoryMealsActivity::class.java)
            intent.putExtra(CATEGORY_NAME, category.strCategory)
            startActivity(intent)
        }
    }

    private fun prepareCategoriesRecyclerView() {
        binding.recViewCategories.apply {
            layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
            adapter = categoriesAdapter
        }
    }

    private fun observeCategoriesLiveData() {
        viewModel.observeCategoriesLiveData().observe(viewLifecycleOwner, Observer { categories ->
            categoriesAdapter.setCategoryList(categories)
        })
    }

    private fun onPopularItemClick() {
        popularItemsAdapter.onItemClick = { meal ->
            val intent = Intent(activity, MealActivity::class.java)
            intent.putExtra(MEAL_ID, meal.idMeal)
            intent.putExtra(MEAL_NAME, meal.strMeal)
            intent.putExtra(MEAL_THUMB, meal.strMealThumb)
            startActivity(intent)
        }
    }

    private fun preparePopularItemsRecyclerView() {
        binding.recViewMealsPopular.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            adapter = popularItemsAdapter
        }
    }

    private fun observePopularItemsLiveData() {
        viewModel.observePopularMealLiveData().observe(viewLifecycleOwner, Observer { mealList ->
            popularItemsAdapter.setMeals(ArrayList(mealList))
            isPopularMealsLoaded = true
            hideProgressBar()
        })
    }

    private fun onRandomMealClick() {
        binding.randomMealCard.setOnClickListener {
            if (::randomMeal.isInitialized) {
                val intent = Intent(activity, MealActivity::class.java)
                intent.putExtra(MEAL_NAME, randomMeal.strMeal)
                intent.putExtra(MEAL_ID, randomMeal.idMeal)
                intent.putExtra(MEAL_THUMB, randomMeal.strMealThumb)
                startActivity(intent)
            }
        }
    }

    private fun observeRandomMeal() {
        viewModel.observeRandomMealLiveData().observe(viewLifecycleOwner, Observer { meal ->
            meal?.let {
                Glide.with(this@HomeFragment)
                    .load(it.strMealThumb)
                    .into(binding.imgRandomMeal)
                this.randomMeal = it
                isRandomMealLoaded = true
                hideProgressBar()
            }
        })
    }
}
