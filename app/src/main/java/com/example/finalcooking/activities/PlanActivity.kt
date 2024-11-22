package com.example.finalcooking.activities





import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalcooking.databinding.ActivityPlanBinding
import com.example.finalcooking.adapters.PlansAdapter
import com.example.finalcooking.db.PlansDatabaseHelper

class PlanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlanBinding
    private lateinit var db: PlansDatabaseHelper
    private lateinit var plansAdapter: PlansAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = PlansDatabaseHelper(this)
        plansAdapter = PlansAdapter(db.getAllPlans(), this)

        binding.palnsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.palnsRecyclerView.adapter = plansAdapter


        showLoading(true)


        fetchPlansData()

        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddPlanActivity::class.java)
            startActivity(intent)
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun fetchPlansData() {

        binding.palnsRecyclerView.postDelayed({

            plansAdapter.refreshData(db.getAllPlans())
            showLoading(false)
        }, 2000)
    }

    private fun showLoading(show: Boolean) {
        binding.loading.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()

        plansAdapter.refreshData(db.getAllPlans())
        showLoading(false)
    }
}
