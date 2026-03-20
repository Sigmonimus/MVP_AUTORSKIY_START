package com.example.mvp_autorskiy_start.ui.authors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mvp_autorskiy_start.databinding.FragmentWorkFullTextBinding

class WorkFullTextFragment : Fragment() {

    private var _binding: FragmentWorkFullTextBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkFullTextBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fullText = arguments?.getString("fullText") ?: ""
        binding.tvFullText.text = fullText
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(fullText: String): WorkFullTextFragment {
            val fragment = WorkFullTextFragment()
            val args = Bundle()
            args.putString("fullText", fullText)
            fragment.arguments = args
            return fragment
        }
    }
}