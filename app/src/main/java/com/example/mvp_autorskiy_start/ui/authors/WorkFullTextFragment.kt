package com.example.mvp_autorskiy_start.ui.authors

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.lifecycleScope
import com.example.mvp_autorskiy_start.databinding.FragmentWorkFullTextBinding
import com.example.mvp_autorskiy_start.data.repository.AuthorsRepository
import com.example.mvp_autorskiy_start.ui.common.BaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkFullTextFragment : BaseFragment<FragmentWorkFullTextBinding>(FragmentWorkFullTextBinding::inflate) {

    private var workId: Int = -1
    private var workTitle: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workId = arguments?.getInt("workId") ?: return
        workTitle = arguments?.getString("workTitle") ?: ""

        binding.progressBar.visibility = View.VISIBLE
        binding.webView.visibility = View.GONE

        setupWebView()
        loadFullText()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        binding.webView.settings.apply {
            javaScriptEnabled = true
            builtInZoomControls = true
            displayZoomControls = false
            loadWithOverviewMode = true
            useWideViewPort = true
        }
        binding.webView.webViewClient = WebViewClient()
    }

    private fun loadFullText() {
        lifecycleScope.launch {
            val html = withContext(Dispatchers.IO) {
                AuthorsRepository.loadFullText(requireContext(), workId)
            }
            binding.webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
            binding.progressBar.visibility = View.GONE
            binding.webView.visibility = View.VISIBLE
        }
    }

    companion object {
        fun newInstance(workId: Int, workTitle: String): WorkFullTextFragment {
            val fragment = WorkFullTextFragment()
            val args = Bundle()
            args.putInt("workId", workId)
            args.putString("workTitle", workTitle)
            fragment.arguments = args
            return fragment
        }
    }
}