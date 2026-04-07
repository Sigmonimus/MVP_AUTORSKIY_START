package com.example.mvp_autorskiy_start.ui.theory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.repository.TheoryRepository
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
        val parser = Parser.builder().build()
        val document: Node = parser.parse(markdown)
        val renderer = HtmlRenderer.builder().build()
        val html = renderer.render(document)
        val styledHtml = """
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="color-scheme" content="light dark">
            <style>
                body {
                    font-family: sans-serif;
                    line-height: 1.6;
                    padding: 16px;
                    margin: 0;
                    background-color: #F5F2EB;
                    color: #4A2E1A;
                }
                h1 { font-size: 28px; color: #3D2B1F; }
                h2 { font-size: 24px; color: #5C3E1F; margin-top: 24px; }
                h3 { font-size: 20px; color: #6B4E2A; }
                blockquote {
                    background-color: #E8DFD1;
                    padding: 16px;
                    border-radius: 8px;
                    border-left: 4px solid #A67C27;
                    margin: 16px 0;
                    color: #4A2E1A;
                }
                blockquote p { margin: 0; }
                ul, ol { padding-left: 24px; }
                li { margin-bottom: 4px; }
                code {
                    background-color: #E8DFD1;
                    padding: 2px 4px;
                    border-radius: 4px;
                    font-family: monospace;
                }
                pre {
                    background-color: #E8DFD1;
                    padding: 12px;
                    border-radius: 8px;
                    overflow-x: auto;
                }

                /* Тёмная тема */
                @media (prefers-color-scheme: dark) {
                    body {
                        background-color: #1E1A16;
                        color: #F5F2EB;
                    }
                    h1 { color: #F5F2EB; }
                    h2 { color: #D9B48B; }
                    h3 { color: #C0A06F; }
                    blockquote {
                        background-color: #2D2924;
                        border-left-color: #D9B48B;
                        color: #F5F2EB;
                    }
                    code, pre {
                        background-color: #2D2924;
                        color: #F5F2EB;
                    }
                }
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