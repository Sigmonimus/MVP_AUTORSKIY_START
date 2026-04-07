package com.example.mvp_autorskiy_start.ui.authors

import android.os.Bundle
import android.view.View
import com.example.mvp_autorskiy_start.databinding.FragmentWorkSummaryBinding
import com.example.mvp_autorskiy_start.ui.common.BaseFragment

class WorkSummaryFragment : BaseFragment<FragmentWorkSummaryBinding>(FragmentWorkSummaryBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val summary = arguments?.getString("summary") ?: ""
        binding.tvSummary.text = summary
    }

    companion object {
        fun newInstance(summary: String): WorkSummaryFragment {
            val fragment = WorkSummaryFragment()
            val args = Bundle()
            args.putString("summary", summary)
            fragment.arguments = args
            return fragment
        }
    }
}