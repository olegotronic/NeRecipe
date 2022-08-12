package ru.netology.nerecipe.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.MotionEvent
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.RecipeBinding
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.util.Filters


@SuppressLint("ClickableViewAccessibility")
fun RecipeBinding.listen(
    recipe: Recipe,
    listener: RecipeInteractionListener,
    itemTouchHelper: ItemTouchHelper?,
    recyclerView: RecyclerView.ViewHolder?,
) {
    buttonFavorites.setOnClickListener { listener.onButtonFavoriteClicked(recipe.id) }
    recipePicture.setOnClickListener { listener.onContentClicked(recipe.id) }

    itemTouchHelper?.let {
        dragImage.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                itemTouchHelper.startDrag(recyclerView!!)
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }
    }
}

fun RecipeBinding.bind(recipe: Recipe, showFull: Boolean = false) {
    dishName.text = recipe.dishName
    author.text = recipe.author
    categoryText.text = Filters.getPresentationForCategoryId(recipe.categoryId)
    content.text = recipe.content
    buttonFavorites.isChecked = recipe.favorite
    groupContent.visibility =
        if (showFull) android.view.View.VISIBLE else android.view.View.GONE
    dragImage.visibility =
        if (showFull) android.view.View.GONE else android.view.View.VISIBLE

    val visibleView = if (showFull) recipePictureFull else recipePicture
    val invisibleView = if (showFull) recipePicture else recipePictureFull

    visibleView.visibility = android.view.View.VISIBLE
    invisibleView.visibility = android.view.View.GONE

    Glide.with(visibleView)
        .asDrawable()
        .load(if (recipe.pictureUri.isNotBlank()) Uri.parse(recipe.pictureUri) else R.drawable.ic_meal)
        .into(visibleView)
}
