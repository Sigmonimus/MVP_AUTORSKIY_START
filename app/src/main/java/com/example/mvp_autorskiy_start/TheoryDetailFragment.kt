package com.example.mvp_autorskiy_start.ui.theory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.TheoryRepository
import org.commonmark.node.Node
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

class TheoryDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_theory_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val subsectionKey = arguments?.getString("subsectionKey") ?: return
        val markdown = TheoryRepository.getMarkdown(requireContext(), subsectionKey)

        // Конвертируем Markdown в HTML
        val parser = Parser.builder().build()
        val document: Node = parser.parse(markdown)
        val renderer = HtmlRenderer.builder().build()
        val html = renderer.render(document)

        // Добавляем базовый CSS для красивого отображения
        val styledHtml = """
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: sans-serif; line-height: 1.6; padding: 16px; }
                    h1 { font-size: 28px; color: #333; }
                    h2 { font-size: 24px; color: #555; margin-top: 24px; }
                    h3 { font-size: 20px; color: #777; }
                    blockquote { background-color: #F0E6FF; padding: 16px; border-radius: 8px; border-left: 4px solid #9B59B6; margin: 16px 0; }
                    blockquote p { margin: 0; }
                    ul, ol { padding-left: 24px; }
                    li { margin-bottom: 4px; }
                </style>
            </head>
            <body>
                $html
            </body>
            </html>
        """.trimIndent()

        val webView = view.findViewById<WebView>(R.id.webView)
        webView.settings.apply {
            builtInZoomControls = false
            displayZoomControls = false
            defaultTextEncodingName = "UTF-8"
        }
        webView.webViewClient = WebViewClient()
        webView.loadDataWithBaseURL(null, styledHtml, "text/html", "UTF-8", null)
    }

    companion object {
        fun newInstance(subsectionKey: String): TheoryDetailFragment {
            val fragment = TheoryDetailFragment()
            val args = Bundle()
            args.putString("subsectionKey", subsectionKey)
            fragment.arguments = args
            return fragment
        }
    }
}