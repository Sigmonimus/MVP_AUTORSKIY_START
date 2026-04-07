package com.example.mvp_autorskiy_start.ui.authors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mvp_autorskiy_start.databinding.FragmentWorkFullTextBinding
import com.example.mvp_autorskiy_start.data.repository.AuthorsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        val workId = arguments?.getInt("workId") ?: return

        binding.progressBar.visibility = View.VISIBLE
        binding.webView.visibility = View.GONE

        // Настройка WebView
        binding.webView.settings.apply {
            builtInZoomControls = true      // позволяет увеличивать/уменьшать пальцами
            displayZoomControls = false     // скрываем кнопки зума (они и так есть)
            loadWithOverviewMode = true
            useWideViewPort = true
        }
        binding.webView.webViewClient = WebViewClient()

        lifecycleScope.launch {
            val fullText = withContext(Dispatchers.IO) {
                AuthorsRepository.loadFullText(requireContext(), workId)
            }
            // Конвертируем переносы строк в HTML <br> для читаемости
            val htmlText = fullText.replace("\n", "<br>")
            // Увеличиваем шрифт и добавляем удобные отступы
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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