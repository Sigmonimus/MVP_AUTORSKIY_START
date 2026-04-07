package com.example.mvp_autorskiy_start.ui.test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.models.Quiz

class TestQuizAdapter(
    private val quizzes: List<Quiz>,
    private val onItemClick: (Quiz) -> Unit
) : RecyclerView.Adapter<TestQuizAdapter.QuizViewHolder>() {

    class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvQuizTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvQuizDescription)
        val tvProgress: TextView = itemView.findViewById(R.id.tvQuizProgress)
        val ivDifficulty: ImageView = itemView.findViewById(R.id.ivDifficulty)
        val ivLock: ImageView = itemView.findViewById(R.id.ivLock)   // добавим замок
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_test_quiz, parent, false)
        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val quiz = quizzes[position]
        holder.tvTitle.text = quiz.title
        holder.tvDescription.text = quiz.description

        // Прогресс
        if (quiz.isCompleted) {
            holder.tvProgress.text = "✅ Пройден: ${quiz.bestScore}%"
            holder.tvProgress.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.correct_green))
        } else if (quiz.bestScore > 0) {
            holder.tvProgress.text = "🏆 Лучший: ${quiz.bestScore}% (для прохода ≥${quiz.passingScore}%)"
            holder.tvProgress.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.primary))
        } else {
            holder.tvProgress.text = "⭐ Не пройден (для зачёта ≥${quiz.passingScore}%)"
            holder.tvProgress.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.primary_light))
        }

        // Уровень сложности
        when (quiz.difficulty.value) {
            1 -> holder.ivDifficulty.setImageResource(R.drawable.ic_star_easy)
            2 -> holder.ivDifficulty.setImageResource(R.drawable.ic_star_medium)
            3 -> holder.ivDifficulty.setImageResource(R.drawable.ic_star_hard)
        }

        // Замок
        if (quiz.isUnlocked) {
            holder.ivLock.visibility = View.GONE
            holder.itemView.isEnabled = true
        } else {
            holder.ivLock.visibility = View.VISIBLE
            holder.itemView.isEnabled = false
        }

        holder.itemView.setOnClickListener {
            if (quiz.isUnlocked) {
                onItemClick(quiz)
            } else {
                Toast.makeText(holder.itemView.context, "Сначала пройдите предыдущий тест", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = quizzes.size
}