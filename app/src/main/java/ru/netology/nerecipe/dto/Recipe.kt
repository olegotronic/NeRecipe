package ru.netology.nerecipe.dto

data class Recipe(
    val dishName: String = "",
    val author: String = "",
    val categoryId: String = "",
    val content: String = "",
    val pictureUri: String = "",
    val favorite: Boolean = false,
    val orderIndex: Int = 0,
    val id: Long = NEW_RECIPE_ID,
) {
    companion object {
        const val NEW_RECIPE_ID = 0L
    }
}


