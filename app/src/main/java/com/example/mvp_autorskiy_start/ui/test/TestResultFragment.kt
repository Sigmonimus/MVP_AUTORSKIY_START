package com.example.mvp_autorskiy_start.ui.test

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.databinding.FragmentTestResultBinding

class TestResultFragment : Fragment() {

    private var _binding: FragmentTestResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val score = arguments?.getInt("score") ?: 0
        val total = arguments?.getInt("total") ?: 0
        val moduleId = arguments?.getInt("moduleId", -1) ?: -1

        // Анимированный счёт
        ValueAnimator.ofInt(0, score).apply {
            duration = 1000
            interpolator = DecelerateInterpolator()
            addUpdateListener { animator ->
                binding.tvScore.text = "${animator.animatedValue}/$total"
            }
            start()
        }

        val percent = (score.toFloat() / total * 100).toInt()
        binding.tvMessage.text = when {
            percent >= 80 -> "Отлично! Ты хорошо подготовлен."
            percent >= 60 -> "Неплохо, но стоит повторить."
            else -> "Нужно ещё позаниматься. Не сдавайся!"
        }

        binding.btnFinish.setOnClickListener {
            goToTestMenu()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                goToTestMenu()
            }
        })
    }

    private fun goToTestMenu() {
        requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, TestMenuFragment())
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(score: Int, total: Int, moduleId: Int = -1): TestResultFragment {
            val fragment = TestResultFragment()
            val args = Bundle()
            args.putInt("score", score)
            args.putInt("total", total)
            args.putInt("moduleId", moduleId)
            fragment.arguments = args
            return fragment
        }
    }
}