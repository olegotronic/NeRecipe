package ru.netology.nerecipe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.databinding.RecipeBinding
import ru.netology.nerecipe.dto.Recipe

class RecipeAdapter(
    private val interactionListener: RecipeInteractionListener,
    private val itemTouchHelper: ItemTouchHelper
) : ListAdapter<Recipe, RecipeAdapter.ViewHolder>(DiffCallback) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), interactionListener, itemTouchHelper)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecipeBinding.inflate(inflater, parent, false)

        return ViewHolder(
            binding,
        )
    }

    class ViewHolder(
        private val binding: RecipeBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var recipe: Recipe

        fun bind(
            recipe: Recipe,
            listener: RecipeInteractionListener,
            itemTouchHelper: ItemTouchHelper
        ) {
            this.recipe = recipe
            binding.bind(recipe)
            binding.listen(recipe, listener, itemTouchHelper, this)
        }

    }

    private object DiffCallback : DiffUtil.ItemCallback<Recipe>() {

        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe) =
            oldItem == newItem

    }

}