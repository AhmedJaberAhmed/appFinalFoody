package com.example.finalcooking.fragments

import HomeViewModel
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.finalcooking.activities.MainActivity
import com.example.finalcooking.activities.MealActivity
import com.example.finalcooking.adapters.MealsAdapter
import com.example.finalcooking.databinding.FragmentFavoritesBinding
import com.google.android.material.snackbar.Snackbar

class FavoritesFragment : Fragment() {
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var favouritesAdapter: MealsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (activity as? MainActivity)?.viewModel ?: throw IllegalStateException("ViewModel not found")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareRecyclerView()
        observeFavourites()
        onFavItemClick()

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val mealToDelete = favouritesAdapter.differ.currentList[position]

                viewModel.deleteMeal(mealToDelete)

                Snackbar.make(requireView(), "Meal Deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        viewModel.insertMeal(mealToDelete)
                    }.show()
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.rvFavourites)
    }

    private fun prepareRecyclerView() {
        favouritesAdapter = MealsAdapter()
        binding.rvFavourites.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = favouritesAdapter
        }
    }

    private fun observeFavourites() {
        viewModel.observeFavouritesLiveData().observe(viewLifecycleOwner) { meals ->
            if (meals.isNullOrEmpty()) {
                binding.rvFavourites.visibility = View.GONE
                binding.loadingAnimation.visibility = View.VISIBLE
                binding.noFav.visibility = View.VISIBLE
            } else {
                binding.rvFavourites.visibility = View.VISIBLE
                binding.loadingAnimation.visibility = View.GONE
                binding.noFav.visibility = View.GONE
                favouritesAdapter.differ.submitList(meals)
            }
        }
    }

    private fun onFavItemClick() {
        favouritesAdapter.onItemClick = { meal ->

            val intent = Intent(activity, MealActivity::class.java).apply {
                putExtra(HomeFragment.MEAL_ID, meal.idMeal ?: return@apply)
                putExtra(HomeFragment.MEAL_NAME, meal.strMeal ?: return@apply)
                putExtra(HomeFragment.MEAL_THUMB, meal.strMealThumb ?: return@apply)
            }
            startActivity(intent)
        }
    }
}
