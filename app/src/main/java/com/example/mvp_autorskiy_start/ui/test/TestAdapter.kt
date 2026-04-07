package com.example.mvp_autorskiy_start.ui.test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mvp_autorskiy_start.R

class TestAdapter(
    private val tests: List<TestItem>,
    private val onItemClick: (TestItem) -> Unit
) : RecyclerView.Adapter<TestAdapter.TestViewHolder>() {

    class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcon: ImageView = itemView.findViewById(R.id.ivTestIcon)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTestTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvTestDescription)
        val tvCount: TextView = itemView.findViewById(R.id.tvQuestionCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_test, parent, false)
        return TestViewHolder(view)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        val test = tests[position]
        holder.tvTitle.text = test.title
        holder.tvDescription.text = test.description
        holder.tvCount.text = "${test.questionCount} вопросов"
        holder.ivIcon.setImageResource(test.iconRes)
        holder.itemView.setOnClickListener { onItemClick(test) }
    }

    override fun getItemCount() = tests.size
}