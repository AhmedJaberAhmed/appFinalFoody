package com.example.finalcooking.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.finalcooking.R
import com.example.finalcooking.activities.UpdatePlanActivity
import com.example.finalcooking.db.Plan
import com.example.finalcooking.db.PlansDatabaseHelper

class PlansAdapter(private var plans: List<Plan>, context: Context) :
    RecyclerView.Adapter<PlansAdapter.PlanViewHolder>() {

    private val db: PlansDatabaseHelper = PlansDatabaseHelper(context)

    class PlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.plan_item, parent, false)
        return PlanViewHolder(view)
    }

    override fun getItemCount(): Int {
        return plans.size
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val plan = plans[position]
        holder.titleTextView.text = plan.title
        holder.contentTextView.text = plan.content

        holder.updateButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdatePlanActivity::class.java).apply {
                putExtra("plan_id", plan.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            db.deletePlan(plan.id)
            refreshData(db.getAllPlans())
            Toast.makeText(holder.itemView.context, "Meal Deleted", Toast.LENGTH_SHORT).show()
        }

    }

    fun refreshData(newPlanes: List<Plan>) {
        plans = newPlanes
        notifyDataSetChanged()
    }

}
