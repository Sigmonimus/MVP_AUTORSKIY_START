package com.example.mvp_autorskiy_start.ui.authors

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mvp_autorskiy_start.data.models.Author

class AuthorPagerAdapter(activity: FragmentActivity, private val author: Author) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AuthorBioFragment.newInstance(author.bio)
            1 -> AuthorWorksFragment.newInstance(author.works)
            2 -> AuthorArgumentsFragment.newInstance(author.works.flatMap { it.arguments })
            else -> throw IllegalStateException("Invalid position")
        }
    }
}