package com.example.mvp_autorskiy_start.ui.authors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mvp_autorskiy_start.databinding.FragmentWorkSummaryBinding

class WorkSummaryFragment : Fragment() {

    private var _binding: FragmentWorkSummaryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val summary = arguments?.getString("summary") ?: ""
        binding.tvSummary.text = summary
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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