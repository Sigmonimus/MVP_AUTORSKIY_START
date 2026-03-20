package com.example.mvp_autorskiy_start.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvp_autorskiy_start.R
import com.example.mvp_autorskiy_start.data.FavoritesRepository
import com.example.mvp_autorskiy_start.databinding.FragmentFavoriteEssaysBinding

class FavoriteEssaysFragment : Fragment() {

    private var _binding: FragmentFavoriteEssaysBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteEssaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadEssays()
    }

    override fun onResume() {
        super.onResume()
        loadEssays()
    }

    private fun loadEssays() {
        val essays = FavoritesRepository.getSavedEssays()
        if (essays.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvEssays.visibility = View.GONE
            binding.tvCount.text = getString(R.string.essays_count, 0)
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.rvEssays.visibility = View.VISIBLE
            binding.tvCount.text = getString(R.string.essays_count, essays.size)

            binding.rvEssays.layoutManager = LinearLayoutManager(requireContext())
            val adapter = FavoriteEssaysAdapter(essays) { essay ->
                Toast.makeText(requireContext(), essay.title, Toast.LENGTH_SHORT).show()
                // Здесь можно открыть детальный просмотр сочинения
            }
            binding.rvEssays.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}