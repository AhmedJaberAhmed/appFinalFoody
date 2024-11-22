import com.example.finalcooking.pojo.CategoryList
import com.example.finalcooking.pojo.MealsByCategoryList
import com.example.finalcooking.pojo.MealList
import retrofit2.Call


import retrofit2.http.GET
import retrofit2.http.Query

interface MealApi {
    @GET ( value = "random.php")
    fun getRandomMeal() : Call<MealList>

    @GET ( value = "lookup.php")
    fun getMealDetails(@Query ( value = "i") id:String) : Call<MealList>

    @GET ( value = "filter.php")
    fun getPopularItems(@Query ( value = "c") categoryName:String) : Call<MealsByCategoryList>

    @GET ( value = "categories.php")
    fun getCategories() : Call<CategoryList>

    @GET ( value = "filter.php")
    fun getMealsByCategory(@Query ( value = "c") categoryName: String) : Call<MealsByCategoryList>

    @GET ( value = "search.php")
    fun searchMeals(@Query ( value = "s") searchQuery:String) : Call<MealList>






}
