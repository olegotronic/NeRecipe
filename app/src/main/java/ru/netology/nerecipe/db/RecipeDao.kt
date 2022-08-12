package ru.netology.nerecipe.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RecipeDao {

    @Insert
    fun add(recipe: RecipeEntity)

    @Update
    fun update(recipe: RecipeEntity)

    @Query("DELETE FROM recipes WHERE id = :id")
    fun remove(id: Long)

    @Query("SELECT * FROM recipes ORDER BY orderIndex")
    fun getAll(): LiveData<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id= :id")
    fun get(id: Long): RecipeEntity

    @Query(
        """
        UPDATE recipes SET
        favorite = CASE WHEN favorite THEN 0 ELSE 1 END
        WHERE id = :id
        """
    )
    fun favorite(id: Long)

}