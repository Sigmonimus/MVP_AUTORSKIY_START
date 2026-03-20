package com.example.mvp_autorskiy_start.data

data class ContentBlock(
    val type: String,           // "heading", "paragraph", "list", "quote", "warning", "highlight"
    val text: String? = null,
    val author: String? = null,
    val items: List<String>? = null
)