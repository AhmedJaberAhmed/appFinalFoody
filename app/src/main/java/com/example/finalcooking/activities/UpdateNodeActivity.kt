package com.example.finalcooking.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.provider.CalendarContract
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.finalcooking.databinding.ActivityUpdateNodeBinding
import com.example.finalcooking.db.Plan
import com.example.finalcooking.db.PlansDatabaseHelper
import java.util.Calendar

class UpdatePlanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateNodeBinding
    private lateinit var db: PlansDatabaseHelper
    private var planId = -1
    private var selectedYear: Int = 0
    private var selectedMonth: Int = 0
    private var selectedDay: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateNodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = PlansDatabaseHelper(this)


        planId = intent.getIntExtra("plan_id", -1)
        if (planId == -1) {
            finish()
            return
        }


        val plan = db.getPlanByID(planId)
        binding.updateTitleEditText.setText(plan.title)
        binding.updateContentEditText.setText(plan.content)


        binding.updateContentEditText.inputType = 0


        binding.updateContentEditText.setOnClickListener {
            showDatePickerDialog()
        }


        binding.updateSaveButton.setOnClickListener {
            val newTitle = binding.updateTitleEditText.text.toString()
            val newContent = binding.updateContentEditText.text.toString()

            if (newTitle.isEmpty() || newContent.isEmpty()) {
                Toast.makeText(this, "Please enter a meal and select a date.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedPlan = Plan(planId, newTitle, newContent)
            db.updatePlan(updatedPlan)


            addEventToCalendar(newTitle)

            finish()
            Toast.makeText(this, "Changes Saved and Event Added to Calendar", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->

                this.selectedYear = selectedYear
                this.selectedMonth = selectedMonth
                this.selectedDay = selectedDay

                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.updateContentEditText.setText(selectedDate)
            },
            year, month, day
        )

        datePickerDialog.show()
    }


    private fun addEventToCalendar(title: String) {
        val calendar = Calendar.getInstance()
        calendar.set(selectedYear, selectedMonth, selectedDay)

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, title)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.timeInMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calendar.timeInMillis + 60 * 60 * 1000)
            putExtra(CalendarContract.Events.ALL_DAY, true)
        }


        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "No calendar app found.", Toast.LENGTH_SHORT).show()
        }
    }
}
