package com.example.mvp_autorskiy_start.utils

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader

object FB2Parser {

    fun parseToHtml(fb2Content: String): String {
        val parser = Xml.newPullParser()
        parser.setInput(StringReader(fb2Content))
        var eventType = parser.eventType
        val htmlBody = StringBuilder()
        var insideBody = false

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    val tagName = parser.name.lowercase()
                    if (tagName == "body") {
                        insideBody = true
                    } else if (insideBody) {
                        when (tagName) {
                            "section" -> htmlBody.append("<div class='section'>")
                            "title" -> htmlBody.append("<h3>")
                            "p" -> htmlBody.append("<p>")
                            "poem" -> htmlBody.append("<pre>")
                            "stanza" -> htmlBody.append("<div class='stanza'>")
                            "v" -> htmlBody.append("<span class='verse'>")
                            "strong", "b" -> htmlBody.append("<b>")
                            "emphasis", "i" -> htmlBody.append("<i>")
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    val tagName = parser.name.lowercase()
                    if (tagName == "body") {
                        insideBody = false
                    } else if (insideBody) {
                        when (tagName) {
                            "section" -> htmlBody.append("</div>")
                            "title" -> htmlBody.append("</h3>")
                            "p" -> htmlBody.append("</p>")
                            "poem" -> htmlBody.append("</pre>")
                            "stanza" -> htmlBody.append("</div>")
                            "v" -> htmlBody.append("</span><br/>")
                            "strong", "b" -> htmlBody.append("</b>")
                            "emphasis", "i" -> htmlBody.append("</i>")
                        }
                    }
                }
                XmlPullParser.TEXT -> {
                    if (insideBody) {
                        val text = parser.text.trim()
                        if (text.isNotEmpty()) {
                            htmlBody.append(text)
                        }
                    }
                }
            }
            eventType = parser.next()
        }

        val htmlContent = htmlBody.toString()
        return """
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
                body {
                    font-size: 40px !important;
                    line-height: 1.6;
                    padding: 16px;
                }
                h3 {
                    font-size: 1.5em;
                    margin-top: 1.2em;
                    margin-bottom: 0.5em;
                }
                p {
                    margin-bottom: 0.8em;
                }
                pre {
                    background-color: #E8DFD1;
                    padding: 12px;
                    border-radius: 8px;
                    font-family: monospace;
                    white-space: pre-wrap;
                }
                .verse {
                    display: block;
                    margin-left: 1em;
                }
                .stanza {
                    margin-bottom: 0.8em;
                }
                .subtitle {
                    font-style: italic;
                    margin-bottom: 0.5em;
                }
                .cite {
                    font-style: italic;
                    margin-left: 1em;
                }
                .epigraph {
                    margin-left: 2em;
                    margin-bottom: 1em;
                    font-style: italic;
                }
                .annotation {
                    margin: 1em 0;
                    padding: 0.5em;
                    background-color: #E8DFD1;
                    border-radius: 8px;
                }
                @media (prefers-color-scheme: dark) {
                    body {
                        background-color: #1E1A16;
                        color: #F5F2EB;
                    }
                    .annotation {
                        background-color: #2D2924;
                    }
                    pre {
                        background-color: #2D2924;
                    }
                    .highlight {
                        background-color: #FFEB3B !important;
                        border-radius: 4px;
                        padding: 0 2px;
                    }
                }
            </style>
        </head>
        <body>
            $htmlContent
        </body>
        </html>
    """.trimIndent()
    }
}