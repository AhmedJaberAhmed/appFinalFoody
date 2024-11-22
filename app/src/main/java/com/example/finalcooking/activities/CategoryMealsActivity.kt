package com.example.finalcooking.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.finalcooking.adapters.CategoryMealsAdapter
import com.example.finalcooking.databinding.ActivityCategoryMealsBinding
import com.example.finalcooking.fragments.HomeFragment
import com.example.finalcooking.viewModel.CategoryMealsViewModel

class CategoryMealsActivity : AppCompatActivity() {
    lateinit var binding: ActivityCategoryMealsBinding
    lateinit var categoryMealsViewModel: CategoryMealsViewModel
    lateinit var categoryMealsAdapter: CategoryMealsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryMealsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareRecyclerView()
        setupViewModel()
        observeMealsLiveData()
        setMealClickListener()
    }

    private fun prepareRecyclerView() {
        categoryMealsAdapter = CategoryMealsAdapter()
        binding.rvMeals.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            adapter = categoryMealsAdapter
        }
    }

    private fun setupViewModel() {
        categoryMealsViewModel = ViewModelProvider(this)[CategoryMealsViewModel::class.java]
        val categoryName = intent.getStringExtra(HomeFragment.CATEGORY_NAME)

        if (categoryName != null) {
            categoryMealsViewModel.getMealsByCategory(categoryName)
            showLoading()
        } else {
            hideLoading()

            binding.tvCategoryCount.text = "No category selected"
        }
    }

    private fun observeMealsLiveData() {
        categoryMealsViewModel.observeMealsLiveData().observe(this, Observer { mealsList ->
            hideLoading()

            if (!mealsList.isNullOrEmpty()) {
                binding.tvCategoryCount.text = mealsList.size.toString()
                categoryMealsAdapter.setMealsList(mealsList)
            } else {
                binding.tvCategoryCount.text = "No meals found"

            }
        })
    }

    private fun setMealClickListener() {
        categoryMealsAdapter.onItemClick = { meal ->
            val intent = Intent(this, MealActivity::class.java).apply {
                putExtra(HomeFragment.MEAL_ID, meal.idMeal)
                putExtra(HomeFragment.MEAL_NAME, meal.strMeal)
                putExtra(HomeFragment.MEAL_THUMB, meal.strMealThumb)
            }
            startActivity(intent)
        }
    }

    private fun showLoading() {
        binding.loadingAnimation.visibility = View.VISIBLE
        binding.no.visibility = View.VISIBLE
        binding.rvMeals.visibility = View.INVISIBLE
    }

    private fun hideLoading() {
        binding.loadingAnimation.visibility = View.GONE
        binding.no.visibility = View.GONE
        binding.rvMeals.visibility = View.VISIBLE
    }
}
