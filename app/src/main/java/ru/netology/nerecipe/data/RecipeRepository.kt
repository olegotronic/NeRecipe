package ru.netology.nerecipe.data

import androidx.lifecycle.LiveData
import ru.netology.nerecipe.dto.Recipe

interface RecipeRepository {

    val data: LiveData<List<Recipe>>

    fun getAll(): List<Recipe>
    fun get(recipeId: Long): Recipe?
    fun remove(recipeId: Long)
    fun save(recipe: Recipe)
    fun favorite(recipeId: Long)
    fun moveItem(fromPosition: Int, toPosition: Int)

    companion object {
        const val NEW_RECIPE_ID = 0L
    }

}