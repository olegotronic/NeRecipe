package ru.netology.nerecipe.db

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "recipes")
class RecipeEntity(
    @ColumnInfo(name = "dishName")
    @NotNull
    val dishName: String,

    @ColumnInfo(name = "author")
    @NotNull
    val author: String,

    @ColumnInfo(name = "categoryId")
    @Nullable
    val categoryId: String,

    @ColumnInfo(name = "favorite")
    val favorite: Boolean,

    @ColumnInfo(name = "content")
    @NotNull
    val content: String,

    @ColumnInfo(name = "pictureUri")
    @NotNull
    val pictureUri: String,

    @ColumnInfo(name = "orderIndex")
    @NotNull
    val orderIndex: Int,

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    @NotNull
    val id: Long,
)