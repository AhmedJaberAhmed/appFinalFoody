package com.example.finalcooking.activities

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.bumptech.glide.Glide
import com.example.finalcooking.R
import com.example.finalcooking.databinding.ActivityMealBinding
import com.example.finalcooking.db.MealDatabase
import com.example.finalcooking.fragments.HomeFragment
import com.example.finalcooking.pojo.Meal
import com.example.finalcooking.viewModel.MealViewModel
import com.example.finalcooking.viewModel.MealViewModelFactory

class MealActivity : AppCompatActivity() {
    //ahmed
    private lateinit var mealID: String
    private lateinit var mealName: String
    private lateinit var mealThumb: String
    private lateinit var binding: ActivityMealBinding
    private lateinit var mealMvvm: MealViewModel
    private lateinit var youtubeLink: String
    private lateinit var webView: WebView

    private var mealTOSave: Meal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealBinding.inflate(layoutInflater)
        val mealDatabase = MealDatabase.getInstance(this)
        val viewModelFactory = MealViewModelFactory(mealDatabase)
        mealMvvm = ViewModelProvider(this, viewModelFactory)[MealViewModel::class.java]
        setContentView(binding.root)

        getMealInformationFromIntent()
        setInformationInViews()
        LoadingCase()
        mealMvvm.getMealDetail(mealID)
        observerMealDetailsLiveData()

        onYoutubeImageClick()
        onFavouriteClick()
    }

    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    private fun onFavouriteClick() {
        binding.btnAddToFav.setOnClickListener {
            mealTOSave?.let {
                mealMvvm.insetMeal(it)
                Toast.makeText(this, "Meal saved", Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(this, "Meal not loaded yet", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onYoutubeImageClick() {
        binding.imgYoutube.setOnClickListener {
            binding.webview.visibility=View.VISIBLE
            binding.imgYoutube.visibility=View.INVISIBLE
            if (::youtubeLink.isInitialized) {
                webView = findViewById(R.id.webview)
                webView.webViewClient = WebViewClient()
                webView.loadUrl(youtubeLink)
                webView.settings.javaScriptEnabled = true
                webView.settings.setSupportZoom(true)
            } else {
                Toast.makeText(this, "Youtube link not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observerMealDetailsLiveData() {
        mealMvvm.observerMealDetailsLiveData().observe(this, Observer { meal ->
            onResponseCase()
            mealTOSave = meal

            binding.tvCategory.text = "Category : ${meal.strCategory}"
            binding.tvArea.text = "Area : ${meal.strArea}"
            binding.tvInstructionsSt.text = meal.strInstructions

            youtubeLink = meal.strYoutube.toString()
        })
    }

    private fun setInformationInViews() {
        Glide.with(applicationContext)
            .load(mealThumb)
            .into(binding.imgMealDetail)

        binding.collapsingToolbar.title = mealName
        binding.collapsingToolbar.setCollapsedTitleTextColor(resources.getColor(R.color.white))
        binding.collapsingToolbar.setExpandedTitleColor(resources.getColor(R.color.white))
    }

    private fun getMealInformationFromIntent() {
        val intent = intent
        mealID = intent.getStringExtra(HomeFragment.MEAL_ID) ?: ""
        mealName = intent.getStringExtra(HomeFragment.MEAL_NAME) ?: ""
        mealThumb = intent.getStringExtra(HomeFragment.MEAL_THUMB) ?: ""
    }

    private fun LoadingCase() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnAddToFav.visibility = View.INVISIBLE
        binding.tvInstructions.visibility = View.INVISIBLE
        binding.tvCategory.visibility = View.INVISIBLE
        binding.tvArea.visibility = View.INVISIBLE
        binding.imgYoutube.visibility = View.INVISIBLE
        binding.webview.visibility=View.INVISIBLE
    }

    private fun onResponseCase() {
        binding.progressBar.visibility = View.INVISIBLE
        binding.btnAddToFav.visibility = View.VISIBLE
        binding.tvInstructions.visibility = View.VISIBLE
        binding.tvCategory.visibility = View.VISIBLE
        binding.tvArea.visibility = View.VISIBLE
        binding.imgYoutube.visibility = View.VISIBLE
        binding.webview.visibility=View.INVISIBLE
    }
}