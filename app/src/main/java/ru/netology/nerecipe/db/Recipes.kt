package ru.netology.nerecipe.db

import ru.netology.nerecipe.dto.Recipe

fun RecipeEntity.toModel(): Recipe = Recipe(
    id = id,
    dishName = dishName,
    author = author,
    categoryId = categoryId,
    content = content,
    pictureUri = pictureUri,
    favorite = favorite,
    orderIndex = orderIndex,
)

fun Recipe.toEntity() = RecipeEntity(
    id = id,
    dishName = dishName,
    author = author,
    categoryId = categoryId,
    content = content,
    pictureUri = pictureUri,
    favorite = favorite,
    orderIndex = orderIndex,
)