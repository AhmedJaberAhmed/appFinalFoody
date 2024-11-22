package com.example.finalcooking.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalcooking.databinding.MealItemBinding
import com.example.finalcooking.pojo.MealsByCategory

class CategoryMealsAdapter : RecyclerView.Adapter<CategoryMealsAdapter.CategoryMealsViewHolder>() {


    var onItemClick: ((MealsByCategory) -> Unit)? = null


    private var mealsList = ArrayList<MealsByCategory>()


    fun setMealsList(mealsList: List<MealsByCategory>) {
        this.mealsList = mealsList as ArrayList<MealsByCategory>
        notifyDataSetChanged()
    }


    inner class CategoryMealsViewHolder(val binding: MealItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(meal: MealsByCategory) {
            Glide.with(binding.root).load(meal.strMealThumb).into(binding.imgMeal)
            binding.tvMealName.text = meal.strMeal


            binding.root.setOnClickListener {
                onItemClick?.invoke(meal)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryMealsViewHolder {
        val binding = MealItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryMealsViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CategoryMealsViewHolder, position: Int) {
        val meal = mealsList[position]
        holder.bind(meal)
    }

    override fun getItemCount(): Int {
        return mealsList.size
    }
}
