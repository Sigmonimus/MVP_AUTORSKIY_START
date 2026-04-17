package com.example.mvp_autorskiy_start.ui.profile

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.mvp_autorskiy_start.R

class AboutDialogFragment : DialogFragment() {

    private var currentTextSize = 18f
    private val MIN_TEXT_SIZE = 14f
    private val MAX_TEXT_SIZE = 28f

    companion object {
        const val TAG = "AboutDialog"
        private const val PREFS_NAME = "about_dialog_prefs"
        private const val KEY_TEXT_SIZE = "text_size"

        fun newInstance(): AboutDialogFragment {
            return AboutDialogFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Theme_AvtorskiyStart)
        loadTextSize()
    }

    private fun loadTextSize() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        currentTextSize = prefs.getFloat(KEY_TEXT_SIZE, 18f)
    }

    private fun saveTextSize(size: Float) {
        currentTextSize = size
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putFloat(KEY_TEXT_SIZE, size).apply()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val contentTextView = view.findViewById<TextView>(R.id.tvAboutContent)
        contentTextView.textSize = currentTextSize

        // Буквица – первая буква текста
        val fullText = contentTextView.text.toString()
        val firstChar = fullText.firstOrNull()?.uppercaseChar() ?: 'А'
        view.findViewById<TextView>(R.id.tvDropCap).text = firstChar.toString()

        view.findViewById<View>(R.id.btnIncreaseText).setOnClickListener {
            if (currentTextSize < MAX_TEXT_SIZE) {
                updateTextSize((currentTextSize + 2).coerceAtMost(MAX_TEXT_SIZE))
            }
        }
        view.findViewById<View>(R.id.btnDecreaseText).setOnClickListener {
            if (currentTextSize > MIN_TEXT_SIZE) {
                updateTextSize((currentTextSize - 2).coerceAtLeast(MIN_TEXT_SIZE))
            }
        }

        view.findViewById<Button>(R.id.btnClose).setOnClickListener { dismiss() }
        view.setOnClickListener { dismiss() }
        view.findViewById<View>(R.id.contentContainer).setOnClickListener { }
    }

    private fun updateTextSize(size: Float) {
        currentTextSize = size
        saveTextSize(size)
        view?.findViewById<TextView>(R.id.tvAboutContent)?.textSize = size
    }
}