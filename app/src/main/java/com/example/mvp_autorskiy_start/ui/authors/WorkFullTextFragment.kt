package com.example.mvp_autorskiy_start.ui.authors

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.lifecycle.lifecycleScope
import com.example.mvp_autorskiy_start.databinding.FragmentWorkFullTextBinding
import com.example.mvp_autorskiy_start.data.repository.AuthorsRepository
import com.example.mvp_autorskiy_start.ui.common.BaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkFullTextFragment : BaseFragment<FragmentWorkFullTextBinding>(FragmentWorkFullTextBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val workId = arguments?.getInt("workId") ?: return

        binding.progressBar.visibility = View.VISIBLE
        binding.webView.visibility = View.GONE

        binding.webView.settings.apply {
            builtInZoomControls = true
            displayZoomControls = false
            loadWithOverviewMode = true
            useWideViewPort = true
        }
        binding.webView.webViewClient = WebViewClient()

        lifecycleScope.launch {
            val fullText = withContext(Dispatchers.IO) {
                AuthorsRepository.loadFullText(requireContext(), workId)
            }
            val htmlText = fullText.replace("\n", "<br>")
            val html = """
                <html>
                <head>
                    <meta charset='UTF-8'>
                    <style>
                        body {
                            font-size: 40px;
                            line-height: 1.5;
                            padding: 16px;
                            margin: 0;
                        }
                    </style>
                </head>
                <body>$htmlText</body>
                </html>
            """.trimIndent()
            binding.webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
            binding.progressBar.visibility = View.GONE
            binding.webView.visibility = View.VISIBLE
        }
    }

    companion object {
        fun newInstance(workId: Int): WorkFullTextFragment {
            val fragment = WorkFullTextFragment()
            val args = Bundle()
            args.putInt("workId", workId)
            fragment.arguments = args
            return fragment
        }
    }
}