package com.example.mvp_autorskiy_start.ui.authors

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.example.mvp_autorskiy_start.data.repository.AuthorsRepository
import com.example.mvp_autorskiy_start.databinding.FragmentWorkFullTextBinding
import com.example.mvp_autorskiy_start.ui.common.BaseFragment
import kotlinx.coroutines.launch

class WorkFullTextFragment : BaseFragment<FragmentWorkFullTextBinding>(FragmentWorkFullTextBinding::inflate) {

    private var workId: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workId = arguments?.getInt("work_id") ?: -1
        if (workId == -1) return

        binding.progressBar.visibility = View.VISIBLE
        binding.webView.visibility = View.GONE
        loadFullText()
    }

    private fun loadFullText() {
        lifecycleScope.launch {
            val html = AuthorsRepository.loadFullText(requireContext(), workId)
            binding.webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
            binding.progressBar.visibility = View.GONE
            binding.webView.visibility = View.VISIBLE
        }
    }

    companion object {
        fun newInstance(workId: Int, title: String): WorkFullTextFragment {
            val fragment = WorkFullTextFragment()
            val args = Bundle()
            args.putInt("work_id", workId)
            args.putString("title", title)
            fragment.arguments = args
            return fragment
        }
    }
}