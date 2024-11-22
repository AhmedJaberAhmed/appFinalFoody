package com.example.finalcooking.fragments

import HomeViewModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager

import com.example.finalcooking.activities.MainActivity
import com.example.finalcooking.activities.MealActivity
import com.example.finalcooking.adapters.MealsAdapter
import com.example.finalcooking.databinding.FragmentSearchBinding
import com.example.finalcooking.fragments.HomeFragment.Companion.MEAL_ID
import com.example.finalcooking.fragments.HomeFragment.Companion.MEAL_NAME
import com.example.finalcooking.fragments.HomeFragment.Companion.MEAL_THUMB
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var searchRecyclerViewAdapter: MealsAdapter
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareRecyclerView()
        observeSearchedMealsLiveData()

        binding.imageSearchArrow.setOnClickListener {
            searchMeals()
        }

        binding.edSearchBox.addTextChangedListener { searchQuery ->
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                delay(300)
                val query = searchQuery?.toString().orEmpty()
                Log.d("SearchFragment", "Searching for: $query")
                if (query.isNotEmpty()) {

                    viewModel.searchMeals(query)
                } else {

                    searchRecyclerViewAdapter.differ.submitList(emptyList())
                    binding.notFound.visibility = View.INVISIBLE
                    binding.rvSearchedMeals.visibility = View.VISIBLE
                    binding.notFound.cancelAnimation()
                }
            }
        }
    }

    private fun observeSearchedMealsLiveData() {
        viewModel.observeSearchedMealsLiveData().observe(viewLifecycleOwner, Observer { mealsList ->
            if (mealsList.isNullOrEmpty()) {

                binding.notFound.visibility = View.VISIBLE
                binding.rvSearchedMeals.visibility = View.GONE
                binding.notFound.playAnimation()
                Log.d("SearchFragment", "No meals found, showing loading animation")
            } else {

                binding.notFound.visibility = View.INVISIBLE
                binding.rvSearchedMeals.visibility = View.VISIBLE
                binding.notFound.cancelAnimation()
                Log.d("SearchFragment", "Meals found, hiding loading animation")
            }
            searchRecyclerViewAdapter.differ.submitList(mealsList)
        })
    }

    private fun searchMeals() {
        val searchQuery = binding.edSearchBox.text.toString()
        if (searchQuery.isNotEmpty()) {

            binding.notFound.visibility = View.VISIBLE
            binding.rvSearchedMeals.visibility = View.GONE
            binding.notFound.playAnimation()
            Log.d("SearchFragment", "Showing loading animation")
            viewModel.searchMeals(searchQuery)
        } else {

            searchRecyclerViewAdapter.differ.submitList(emptyList())
            binding.notFound.visibility = View.INVISIBLE
            binding.rvSearchedMeals.visibility = View.VISIBLE
            binding.notFound.cancelAnimation()
            Log.d("SearchFragment", "Hiding loading animation")
        }
    }

    private fun prepareRecyclerView() {
        searchRecyclerViewAdapter = MealsAdapter()
        binding.rvSearchedMeals.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            adapter = searchRecyclerViewAdapter
        }

        searchRecyclerViewAdapter.onItemClick = { meal ->
            val intent = Intent(activity, MealActivity::class.java).apply {
                putExtra(MEAL_ID, meal.idMeal)
                putExtra(MEAL_NAME, meal.strMeal)
                putExtra(MEAL_THUMB, meal.strMealThumb)
            }
            startActivity(intent)
        }
    }
}
