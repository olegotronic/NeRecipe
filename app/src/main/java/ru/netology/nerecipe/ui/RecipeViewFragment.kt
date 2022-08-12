package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.bind
import ru.netology.nerecipe.adapter.listen
import ru.netology.nerecipe.databinding.RecipeViewFragmentBinding
import ru.netology.nerecipe.viewModel.RecipeViewModel

class RecipeViewFragment : Fragment() {

    private val args by navArgs<RecipeViewFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = RecipeViewFragmentBinding.inflate(layoutInflater, container, false).also { binding ->

        val viewModel: RecipeViewModel by viewModels(::requireParentFragment)

        viewModel.data.observe(viewLifecycleOwner) {
            with(viewModel.getRecipeById(args.recipeId)) {
                this?.let {
                    binding.recipeContent.bind(it, showFull = true)
                    binding.recipeContent.listen(it, viewModel, null, null)
                }
            }
        }

        viewModel.navigateToRecipeEditorScreen.observe(viewLifecycleOwner) { initialContent ->
            val direction = RecipeViewFragmentDirections.fromViewToEdit(initialContent)
            findNavController().navigate(direction)
        }

        viewModel.removePost.observe(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        binding.topActionBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.remove -> {
                    viewModel.onButtonRemoveClicked(args.recipeId)
                    true
                }
                R.id.edit -> {
                    viewModel.onButtonEditClicked(args.recipeId)
                    true
                }
                else -> {
                    false
                }
            }
        }

        setFragmentResultListener(
            requestKey = RecipeEditFragment.REQUEST_KEY
        ) { requestKey, bundle ->
            if (requestKey != RecipeEditFragment.REQUEST_KEY) return@setFragmentResultListener
            viewModel.onButtonSaveClicked(bundle)
        }

    }.root

}