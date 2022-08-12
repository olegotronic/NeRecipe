package ru.netology.nerecipe.util

data class Category(
    val categoryId: String,
    val presentation: String,
    var selected: Boolean = true,
)
