package com.example.mvp_autorskiy_start.ui.arguments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvp_autorskiy_start.databinding.FragmentArgumentsListBinding
import com.example.mvp_autorskiy_start.data.models.Argument
import com.example.mvp_autorskiy_start.data.repository.FavoritesRepository

class ArgumentsListFragment : Fragment() {

    private var _binding: FragmentArgumentsListBinding? = null
    private val binding get() = _binding!!

    private var categoryId: Int = 0
    private var categoryName: String = ""
    private var allArguments: List<Argument> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryId = it.getInt("categoryId", 0)
            categoryName = it.getString("categoryName", "")
            allArguments = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                it.getParcelableArrayList("arguments", Argument::class.java) ?: emptyList()
            } else {
                @Suppress("DEPRECATION")
                it.getParcelableArrayList("arguments") ?: emptyList()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArgumentsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as? androidx.appcompat.app.AppCompatActivity)?.supportActionBar?.title = categoryName

        binding.rvArguments.layoutManager = LinearLayoutManager(requireContext())
        updateAdapter()
    }

    private fun updateAdapter() {
        val filteredArguments = allArguments.filter { it.categoryIds.contains(categoryId) }
        val favoriteIds = FavoritesRepository.getFavoriteArguments()
        val adapter = ArgumentsAdapter(   // исправлено имя
            arguments = filteredArguments,
            favoriteIds = favoriteIds,
            onItemClick = { argument ->
                // TODO: открыть детальный экран аргумента
            },
            onFavoriteClick = { argument, isFavorite ->
                if (isFavorite) {
                    FavoritesRepository.addFavoriteArgument(argument.id)
                } else {
                    FavoritesRepository.removeFavoriteArgument(argument.id)
                }
                updateAdapter()
            }
        )
        binding.rvArguments.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(categoryId: Int, categoryName: String, allArguments: List<Argument>): ArgumentsListFragment {
            val fragment = ArgumentsListFragment()
            val args = Bundle()
            args.putInt("categoryId", categoryId)
            args.putString("categoryName", categoryName)
            args.putParcelableArrayList("arguments", ArrayList(allArguments))
            fragment.arguments = args
            return fragment
        }
    }
}