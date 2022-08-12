package ru.netology.nerecipe.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.RecipeEditFragmentBinding
import ru.netology.nerecipe.util.Filters
import ru.netology.nerecipe.viewModel.RecipeViewModel

class RecipeEditFragment : Fragment() {

    private val viewModel: RecipeViewModel by viewModels(::requireParentFragment) //
    private val args by navArgs<RecipeEditFragmentArgs>()
    private val pictureUriData = MutableLiveData<String>(null)
    private lateinit var photoPicker: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = RecipeEditFragmentBinding.inflate(layoutInflater, container, false).also { binding ->

        val recipe = viewModel.getRecipeById(args.recipeId)
        recipe?.let {
            binding.dishName.setText(recipe.dishName)
            binding.author.setText(recipe.author)
            binding.categoryText.setText(
                Filters.getPresentationForCategoryId(recipe.categoryId)
            )
            binding.content.setText(recipe.content)

            pictureUriData.value = recipe.pictureUri
        }
        showRecipePicture(binding)

        val arrayAdapter =
            ArrayAdapter(requireContext(), R.layout.list_item, Filters.getListOfPresentations())
        binding.categoryText.setAdapter(arrayAdapter)

        photoPicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            pictureUriData.value = uri.toString()
            showRecipePicture(binding)
        }

        binding.recipePicture.setOnClickListener {
            photoPicker.launch("image/*")
        }

        binding.save.setOnClickListener {
            onSaveButtonClicked(binding)
        }

        binding.dishName.requestFocus()

    }.root

    private fun onSaveButtonClicked(binding: RecipeEditFragmentBinding) {

        arrayListOf(
            binding.dishName,
            binding.author,
            binding.categoryText,
            binding.content,
        ).forEach {
            if (it.text.isNullOrBlank()) {
                Snackbar.make(
                    it,
                    "${it.hint} ${getString(R.string.alert_recipe_empty_field)}",
                    Snackbar.LENGTH_SHORT
                )
                    .setAnchorView(it)
                    .show()
                it.requestFocus()
                return
            }
        }

        val resultBundle = Bundle(1)
        resultBundle.putLong(ID_KEY, args.recipeId)
        resultBundle.putString(DISH_NAME_KEY, binding.dishName.text.toString())
        resultBundle.putString(AUTHOR_KEY, binding.author.text.toString())
        resultBundle.putString(
            CATEGORY_KEY,
            Filters.getCategoryIdForPresentation(binding.categoryText.text.toString())
        )
        resultBundle.putString(CONTENT_KEY, binding.content.text.toString())
        resultBundle.putString(PICTURE_KEY, pictureUriData.value ?: "")

        setFragmentResult(REQUEST_KEY, resultBundle)
        findNavController().popBackStack()
    }

    private fun showRecipePicture(binding: RecipeEditFragmentBinding) {
        if (!pictureUriData.value.isNullOrBlank()) {
            Glide.with(this)
                .asDrawable()
                .load(Uri.parse(pictureUriData.value))
                .error(R.drawable.ic_meal)
                .into(binding.recipePicture)
            binding.chooseImage.visibility = android.view.View.GONE
        } else {
            Glide.with(this)
                .asDrawable()
                .load(R.drawable.ic_meal)
                .into(binding.recipePicture)
            binding.chooseImage.visibility = android.view.View.VISIBLE
        }
    }

    companion object {
        const val ID_KEY = "id"
        const val DISH_NAME_KEY = "dishName"
        const val AUTHOR_KEY = "author"
        const val CATEGORY_KEY = "categoryId"
        const val CONTENT_KEY = "content"
        const val PICTURE_KEY = "pictureUri"
        const val REQUEST_KEY = "request"
    }

}