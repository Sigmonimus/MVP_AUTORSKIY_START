package com.example.mvp_autorskiy_start.ui.test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.models.Module

class TestModuleAdapter(
    private val modules: List<Module>,
    private val onItemClick: (Module) -> Unit
) : RecyclerView.Adapter<TestModuleAdapter.ModuleViewHolder>() {

    class ModuleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcon: ImageView = itemView.findViewById(R.id.ivModuleIcon)
        val tvTitle: TextView = itemView.findViewById(R.id.tvModuleTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvModuleDescription)
        val tvProgress: TextView = itemView.findViewById(R.id.tvModuleProgress)
        val ivLock: ImageView = itemView.findViewById(R.id.ivModuleLock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_test_module, parent, false)
        return ModuleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        val module = modules[position]
        holder.tvTitle.text = module.title
        holder.tvDescription.text = module.description
        holder.ivIcon.setImageResource(module.iconRes)

        if (module.isCompleted) {
            holder.tvProgress.text = "✅ Пройден: ${module.bestScore}%"
            holder.tvProgress.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.correct_green))
        } else if (module.isUnlocked) {
            holder.tvProgress.text = "0% (для прохода нужно ≥${module.passingScore}%)"
            holder.tvProgress.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.primary))
        } else {
            holder.tvProgress.text = "🔒 Закрыто"
            holder.tvProgress.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.wrong_red))
        }

        holder.ivLock.visibility = if (module.isUnlocked) View.GONE else View.VISIBLE

        holder.itemView.setOnClickListener {
            if (module.isUnlocked) {
                onItemClick(module)
            } else {
                // Показать сообщение, что модуль заблокирован
                android.widget.Toast.makeText(holder.itemView.context, "Сначала пройдите предыдущий модуль", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = modules.size
}