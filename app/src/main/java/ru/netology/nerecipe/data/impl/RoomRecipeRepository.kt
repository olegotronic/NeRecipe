package ru.netology.nerecipe.data.impl

import androidx.lifecycle.map
import ru.netology.nerecipe.data.RecipeRepository
import ru.netology.nerecipe.db.RecipeDao
import ru.netology.nerecipe.db.toEntity
import ru.netology.nerecipe.db.toModel
import ru.netology.nerecipe.dto.Recipe

class RoomRecipeRepository(
    private val dao: RecipeDao
) : RecipeRepository {

    override var data =
        dao.getAll().map { entities -> entities.map { it.toModel() } }

    override fun getAll(): List<Recipe> {
        return dao.getAll().value?.map { it.toModel() } ?: emptyList()
    }

    override fun get(recipeId: Long): Recipe? {
        return data.value?.find { it.id == recipeId }
    }

    override fun remove(recipeId: Long) {
        var index = 0
        data.value?.forEach { recipe ->
            if (recipe.id == recipeId) dao.remove(recipeId)
            else {
                if (recipe.orderIndex != index) {
                    dao.update(recipe.copy(orderIndex = index).toEntity())
                }
                index++
            }
        }
    }

    override fun save(recipe: Recipe) {
        if (recipe.id == RecipeRepository.NEW_RECIPE_ID) {
            val orderIndex = data.value?.count() ?: 0
            dao.add(recipe.copy(orderIndex = orderIndex).toEntity())
        } else {
            val orderIndex = get(recipe.id)?.orderIndex ?: 0
            val favorite = get(recipe.id)?.favorite ?: false
            dao.update(recipe.copy(orderIndex = orderIndex, favorite = favorite).toEntity())
        }
    }

    override fun favorite(recipeId: Long) {
        dao.favorite(recipeId)
    }

    override fun moveItem(fromPosition: Int, toPosition: Int) {
        if (!fromPosition.equals(toPosition)) {
            val fromRecipe = data.value?.get(fromPosition)?.copy(orderIndex = toPosition)
            val toRecipe = data.value?.get(toPosition)?.copy(orderIndex = fromPosition)
            fromRecipe?.let { dao.update(it.toEntity()) }
            toRecipe?.let { dao.update(it.toEntity()) }
        }
    }

}