import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalcooking.db.MealDatabase
import com.example.finalcooking.pojo.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(
    private val mealDatabase: MealDatabase
) : ViewModel() {

    private val randomMealLiveData = MutableLiveData<Meal>()
    private val popularItemsLiveData = MutableLiveData<List<MealsByCategory>>()
    private val categoriesLiveData = MutableLiveData<List<Category>>()
    private val favouritesMealsLiveData = mealDatabase.mealDao().getAllMeals()
    private val bottomSheetMealLiveData = MutableLiveData<Meal>()
    private val searchedMealsLiveData = MutableLiveData<List<Meal>>()
    private var saveStateRandomMeal: Meal? = null

    fun getRandomMeal() {
        saveStateRandomMeal?.let {
            randomMealLiveData.postValue(it)
            return
        }

        RetrofitInstance.api.getRandomMeal().enqueue(object : Callback<MealList> {
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val randomMeal = it.meals[0]
                        randomMealLiveData.value = randomMeal
                        saveStateRandomMeal = randomMeal
                    } ?: run {
                        Log.d("HomeViewModel", "Response body is null")
                    }
                } else {
                    Log.d("HomeViewModel", "Response unsuccessful: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.d("HomeViewModel", "Error: ${t.message}")
            }
        })
    }

    fun observeRandomMealLiveData(): LiveData<Meal> = randomMealLiveData

    fun getPopularItems() {
        RetrofitInstance.api.getPopularItems("Seafood").enqueue(object : Callback<MealsByCategoryList> {
            override fun onResponse(call: Call<MealsByCategoryList>, response: Response<MealsByCategoryList>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        popularItemsLiveData.value = it.meals
                    } ?: run {
                        Log.d("HomeViewModel", "Response body is null")
                    }
                } else {
                    Log.d("HomeViewModel", "Response unsuccessful: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<MealsByCategoryList>, t: Throwable) {
                Log.d("HomeViewModel", "Error: ${t.message}")
            }
        })
    }

    fun getCategories() {
        RetrofitInstance.api.getCategories().enqueue(object : Callback<CategoryList> {
            override fun onResponse(call: Call<CategoryList>, response: Response<CategoryList>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        categoriesLiveData.postValue(it.categories)
                    } ?: run {
                        Log.d("HomeViewModel", "Response body is null")
                    }
                } else {
                    Log.d("HomeViewModel", "Response unsuccessful: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<CategoryList>, t: Throwable) {
                Log.e("HomeViewModel", "Error: ${t.message}")
            }
        })
    }

    fun observeCategoriesLiveData(): LiveData<List<Category>> = categoriesLiveData

    fun getMealById(id: String) {
        RetrofitInstance.api.getMealDetails(id).enqueue(object : Callback<MealList> {
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        bottomSheetMealLiveData.postValue(it.meals.firstOrNull())
                    } ?: run {
                        Log.d("HomeViewModel", "Response body is null")
                    }
                } else {
                    Log.d("HomeViewModel", "Response unsuccessful: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.e("HomeViewModel", "Error: ${t.message}")
            }
        })
    }

    fun observePopularMealLiveData(): LiveData<List<MealsByCategory>> = popularItemsLiveData

    fun observeFavouritesLiveData(): LiveData<List<Meal>> = favouritesMealsLiveData

    fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            mealDatabase.mealDao().delete(meal)
        }
    }

    fun insertMeal(meal: Meal) {
        viewModelScope.launch {
            mealDatabase.mealDao().upsert(meal)
        }
    }

    fun searchMeals(searchQuery: String) {
        RetrofitInstance.api.searchMeals(searchQuery).enqueue(object : Callback<MealList> {
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        searchedMealsLiveData.postValue(it.meals)
                    } ?: run {
                        Log.d("HomeViewModel", "Response body is null")
                    }
                } else {
                    Log.d("HomeViewModel", "Response unsuccessful: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.e("HomeViewModel", "Error: ${t.message}")
            }
        })
    }

    fun observeSearchedMealsLiveData(): LiveData<List<Meal>> = searchedMealsLiveData

    fun observeBottomSheetMeal(): LiveData<Meal> = bottomSheetMealLiveData
}
