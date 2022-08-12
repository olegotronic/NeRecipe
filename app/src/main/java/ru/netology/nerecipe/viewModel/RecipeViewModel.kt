package ru.netology.nerecipe.viewModel

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import ru.netology.nerecipe.adapter.RecipeInteractionListener
import ru.netology.nerecipe.data.RecipeRepository
import ru.netology.nerecipe.data.impl.RoomRecipeRepository
import ru.netology.nerecipe.db.AppDb
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.ui.RecipeEditFragment
import ru.netology.nerecipe.util.SingleLiveEvent

class RecipeViewModel(
    application: Application
) : AndroidViewModel(application),
    RecipeInteractionListener {

    private val repository: RecipeRepository = RoomRecipeRepository(
        dao = AppDb.getInstance(context = application).recipeDao
    )
    val data by repository::data

    val navigateToRecipeEditorScreen = SingleLiveEvent<Long>()
    val navigateToViewContentScreenEvent = SingleLiveEvent<Long>()
    val removePost = SingleLiveEvent<Unit>()

    fun getRecipeById(postId: Long): Recipe? =
        repository.get(postId)

    override fun onButtonSaveClicked(bundle: Bundle) {

        val id = bundle.getLong(RecipeEditFragment.ID_KEY)
        val dishName = bundle.getString(RecipeEditFragment.DISH_NAME_KEY) ?: ""
        val author = bundle.getString(RecipeEditFragment.AUTHOR_KEY) ?: ""
        val categoryId = bundle.getString(RecipeEditFragment.CATEGORY_KEY) ?: ""
        val content = bundle.getString(RecipeEditFragment.CONTENT_KEY) ?: ""
        val pictureUri = bundle.getString(RecipeEditFragment.PICTURE_KEY) ?: ""

        val recipe = Recipe(
            id = id,
            dishName = dishName,
            author = author,
            categoryId = categoryId,
            content = content,
            pictureUri = pictureUri,
        )
        repository.save(recipe)

    }

    override fun onButtonAddClicked() {
        navigateToRecipeEditorScreen.value = RecipeRepository.NEW_RECIPE_ID
    }

    override fun onButtonFavoriteClicked(recipeId: Long) {
        repository.favorite(recipeId)
    }

    override fun onButtonRemoveClicked(recipeId: Long) {
        repository.remove(recipeId)
        removePost.value = Unit
    }

    override fun onButtonEditClicked(recipeId: Long) {
        navigateToRecipeEditorScreen.value = recipeId
    }

    override fun onContentClicked(recipeId: Long) {
        navigateToViewContentScreenEvent.value = recipeId
    }

    override fun onMove(fromPosition: Int, toPosition: Int) {
        Log.d("TAG", "from: $fromPosition to $toPosition")
        repository.moveItem(fromPosition, toPosition)
    }

}