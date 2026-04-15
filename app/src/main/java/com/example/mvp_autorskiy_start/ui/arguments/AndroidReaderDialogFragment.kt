package com.example.mvp_autorskiy_start.ui.arguments

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
import com.example.mvp_autorskiy_start.data.models.Argument

class ArgumentReaderDialogFragment : DialogFragment() {

    private var argument: Argument? = null
    private var currentTextSize = 18f
    private val MIN_TEXT_SIZE = 14f
    private val MAX_TEXT_SIZE = 28f

    companion object {
        const val TAG = "ArgumentReaderDialog"
        private const val PREFS_NAME = "argument_reader_prefs"
        private const val KEY_TEXT_SIZE = "text_size"

        fun newInstance(argument: Argument): ArgumentReaderDialogFragment {
            return ArgumentReaderDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("argument", argument)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Theme_AvtorskiyStart)
        loadTextSize()
        argument = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("argument", Argument::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("argument")
        }
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
            // Если стиль анимации не определён, закомментируйте следующую строку
            // window?.attributes?.windowAnimations = R.style.DialogAnimation
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_argument_reader, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        argument?.let { arg ->
            view.findViewById<TextView>(R.id.tvArgumentTitle).text = arg.title
            view.findViewById<TextView>(R.id.tvWorkAuthor).text = "${arg.workTitle} — ${arg.author}"

            val contentTextView = view.findViewById<TextView>(R.id.tvArgumentContent)
            contentTextView.text = arg.description
            contentTextView.textSize = currentTextSize

            val firstChar = arg.description.firstOrNull()?.uppercaseChar() ?: 'А'
            view.findViewById<TextView>(R.id.tvDropCap).text = firstChar.toString()
        }

        // Закрытие
        view.findViewById<Button>(R.id.btnClose).setOnClickListener { dismiss() }

        // Управление шрифтом (если кнопки есть в layout)
        view.findViewById<View>(R.id.btnIncreaseText)?.setOnClickListener {
            if (currentTextSize < MAX_TEXT_SIZE) {
                updateTextSize((currentTextSize + 2).coerceAtMost(MAX_TEXT_SIZE))
            }
        }
        view.findViewById<View>(R.id.btnDecreaseText)?.setOnClickListener {
            if (currentTextSize > MIN_TEXT_SIZE) {
                updateTextSize((currentTextSize - 2).coerceAtLeast(MIN_TEXT_SIZE))
            }
        }

        // Закрытие по клику на фон (весь диалог)
        view.setOnClickListener { dismiss() }
        // Чтобы контент не закрывал диалог (если есть контейнер contentContainer)
        view.findViewById<View>(R.id.contentContainer)?.setOnClickListener { }
    }

    private fun updateTextSize(size: Float) {
        currentTextSize = size
        saveTextSize(size)
        view?.findViewById<TextView>(R.id.tvArgumentContent)?.textSize = size
    }
}