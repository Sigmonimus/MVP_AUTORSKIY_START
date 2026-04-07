package com.example.mvp_autorskiy_start.ui.authors

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mvp_autorskiy_start.data.models.Work

class WorkPagerAdapter(activity: FragmentActivity, private val work: Work) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> WorkSummaryFragment.newInstance(work.summary)
            1 -> WorkFullTextFragment.newInstance(work.id)
            2 -> ArgumentsListFragment.newInstance(work.arguments)
            else -> throw IllegalStateException("Invalid position")
        }
    }
}