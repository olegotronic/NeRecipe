package ru.netology.nerecipe.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.MenuInflater
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.children
import ru.netology.nerecipe.dto.Recipe

object Filters {

    var filterFavorite = false
    var searchString = ""
    val filterCategories = mutableListOf<Category>()

    @SuppressLint("RestrictedApi")
    fun initCategoriesFromResource(context: Context?, resourceMenu: Int) {

        val menu = MenuBuilder(context)
        MenuInflater(context).inflate(resourceMenu, menu)

        filterCategories.clear()
        menu.children.forEach {
            filterCategories.add(Category(it.itemId.toString(), it.title.toString(), true))
        }

    }

    fun getPresentationForCategoryId(categoryId: String): String {
        return filterCategories.find { it.categoryId == categoryId }?.presentation ?: ""
    }

    fun getCategoryIdForPresentation(presentation: String): String {
        return filterCategories.find { it.presentation == presentation }?.categoryId ?: ""
    }

    fun getListOfPresentations(): List<String> {
        return filterCategories.map { it.presentation }
    }

    fun getRecipesListAfterFilter(
        recipeList: List<Recipe>?
    ): List<Recipe> {

        return recipeList!!.filter { recipe ->
            (filterCategories.find { it.selected && it.categoryId == recipe.categoryId } != null)
                    && (!filterFavorite || recipe.favorite == filterFavorite)
                    && (searchString.isEmpty()
                    || recipe.dishName.contains(searchString, true)
                    || recipe.author.contains(searchString, true))
        }

    }

}
