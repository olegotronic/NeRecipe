package ru.netology.nerecipe.adapter

import android.os.Bundle

interface RecipeInteractionListener {

    fun onButtonAddClicked()
    fun onButtonSaveClicked(bundle: Bundle)
    fun onButtonEditClicked(recipeId: Long)
    fun onButtonRemoveClicked(recipeId: Long)
    fun onButtonFavoriteClicked(recipeId: Long)
    fun onContentClicked(recipeId: Long)
    fun onMove(fromPosition: Int, toPosition: Int)

}