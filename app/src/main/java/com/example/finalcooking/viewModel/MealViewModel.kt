package com.example.finalcooking.viewModel

import RetrofitInstance
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalcooking.db.MealDatabase
import com.example.finalcooking.pojo.Meal
import com.example.finalcooking.pojo.MealList
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MealViewModel(
    val  mealDatabase:MealDatabase
) : ViewModel(


) {
    private val mealDetailsLiveData = MutableLiveData<Meal>()

    fun getMealDetail(id: String) {
        RetrofitInstance.api.getMealDetails(id).enqueue(object : Callback<MealList> {
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                if (response.body() != null) {
                    mealDetailsLiveData.value = response.body()!!.meals[0]

                }


            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.d("MEAL ACTIVITY", t.message.toString())
            }


        })


    }

    fun observerMealDetailsLiveData(): LiveData<Meal> {

        return mealDetailsLiveData
    }

    fun insetMeal(meal :Meal ){
        viewModelScope.launch {
            mealDatabase.mealDao().upsert(meal)
        }}

    fun deleteMeal(meal :Meal){viewModelScope.launch {
        mealDatabase.mealDao().delete(meal)

    }}


}