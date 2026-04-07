package com.example.mvp_autorskiy_start.ui.authors

import android.os.Bundle
import android.view.View
import com.example.mvp_autorskiy_start.databinding.FragmentAuthorBioBinding
import com.example.mvp_autorskiy_start.ui.common.BaseFragment

class AuthorBioFragment : BaseFragment<FragmentAuthorBioBinding>(FragmentAuthorBioBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bio = arguments?.getString("bio") ?: ""
        binding.tvBio.text = bio
    }

    companion object {
        fun newInstance(bio: String): AuthorBioFragment {
            val fragment = AuthorBioFragment()
            val args = Bundle()
            args.putString("bio", bio)
            fragment.arguments = args
            return fragment
        }
    }
}