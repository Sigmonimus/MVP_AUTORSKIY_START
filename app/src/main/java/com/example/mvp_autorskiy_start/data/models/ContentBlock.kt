package com.example.mvp_autorskiy_start.data.models

data class ContentBlock(
    val type: String,           // "heading", "paragraph", "list", "quote", "warning", "highlight"
    val text: String? = null,
    val author: String? = null,
    val items: List<String>? = null
)