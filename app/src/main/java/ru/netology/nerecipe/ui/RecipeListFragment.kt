package ru.netology.nerecipe.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.RecipeAdapter
import ru.netology.nerecipe.databinding.RecipeListFragmentBinding
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.util.Filters
import ru.netology.nerecipe.viewModel.RecipeViewModel

class RecipeListFragment : Fragment() {

    private val viewModel: RecipeViewModel by viewModels()
    lateinit var adapter: RecipeAdapter
    private var lastFromPosition = Int.MAX_VALUE
    private var lastToPosition = Int.MAX_VALUE
    private var lastTimeMillis = 0L

    private val itemTouchHelper by lazy {
        val itemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPosition = viewHolder.adapterPosition
                    val toPosition = target.adapterPosition

                    if (fromPosition != toPosition
                        && (System.currentTimeMillis() - lastTimeMillis > 500
                                || lastFromPosition != fromPosition
                                || lastToPosition != toPosition)
                    ) {
                        with(adapter.currentList) {
                            viewModel.onMove(
                                get(fromPosition).orderIndex,
                                get(toPosition).orderIndex
                            )
                        }
                        adapter.notifyItemMoved(fromPosition, toPosition)

                        lastTimeMillis = System.currentTimeMillis()
                        lastFromPosition = fromPosition
                        lastToPosition = toPosition
                    }
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                }

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)
                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.scaleY = 1.0f
                        viewHolder?.itemView?.alpha = 0.5f
                    }
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder.itemView.scaleY = 1.0f
                    viewHolder.itemView.alpha = 1.0f
                }

            }
        ItemTouchHelper(itemTouchCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = RecipeAdapter(viewModel, itemTouchHelper)

        viewModel.navigateToRecipeEditorScreen.observe(this) { recipeId ->
            val direction = RecipeListFragmentDirections.fromListToEdit(recipeId)
            findNavController().navigate(direction)
        }

        viewModel.navigateToViewContentScreenEvent.observe(this) { recipeId ->
            val direction = RecipeListFragmentDirections.fromListToView(recipeId)
            findNavController().navigate(direction)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = RecipeListFragmentBinding.inflate(layoutInflater, container, false).also { binding ->

        itemTouchHelper.attachToRecyclerView(binding.recipeRecyclerView)

        binding.recipeRecyclerView.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) {
            val listAfterFilter = Filters.getRecipesListAfterFilter(viewModel.data.value)
            adapter.submitList(listAfterFilter)
            showEmptyListMessage(binding.emptyList, listAfterFilter)
        }

        setFragmentResultListener(
            requestKey = RecipeEditFragment.REQUEST_KEY
        ) { requestKey, bundle ->
            if (requestKey != RecipeEditFragment.REQUEST_KEY) return@setFragmentResultListener
            viewModel.onButtonSaveClicked(bundle)
        }

        binding.addItem.setOnClickListener {
            viewModel.onButtonAddClicked()
        }

        binding.bottomToolbar.setOnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.favorites -> Filters.filterFavorite = true
                R.id.list -> Filters.filterFavorite = false
                else -> return@setOnItemSelectedListener false
            }

            val listAfterFilter = Filters.getRecipesListAfterFilter(viewModel.data.value)
            adapter.submitList(listAfterFilter)
            showEmptyListMessage(binding.emptyList, listAfterFilter)

            return@setOnItemSelectedListener true

        }

        binding.topActionBar.setOnMenuItemClickListener {

            val categoriesArray = Filters.filterCategories
                .map { it.presentation }
                .toList()
                .toTypedArray()

            val checkedItems = Filters.filterCategories
                .map { it.selected }
                .toList()
                .toBooleanArray()

            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.category_filter))
                .setMultiChoiceItems(categoriesArray, checkedItems) { _, which, idChecked ->
                    checkedItems[which] = idChecked
                }
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .show()

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener { view ->

                if (true !in checkedItems) {
                    Snackbar.make(
                        view,
                        getString(R.string.no_category_alert),
                        Snackbar.LENGTH_SHORT
                    )
                        .setAnchorView(view)
                        .show()
                    return@setOnClickListener
                }
                dialog.dismiss()

                Filters.filterCategories.forEachIndexed { index, category ->
                    category.selected = checkedItems[index]
                }
                val listAfterFilter = Filters.getRecipesListAfterFilter(viewModel.data.value)
                adapter.submitList(listAfterFilter)
                showEmptyListMessage(binding.emptyList, listAfterFilter)

                it.setIcon(
                    if (false in checkedItems) R.drawable.ic_filter_off_24dp
                    else R.drawable.ic_filter_24dp
                )

            }

            return@setOnMenuItemClickListener true

        }

        binding.searchText.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                Filters.searchString = newText
                val listAfterFilter = Filters.getRecipesListAfterFilter(viewModel.data.value)
                adapter.submitList(listAfterFilter)
                showEmptyListMessage(binding.emptyList, listAfterFilter)
                return false
            }

        })

    }.root

    private fun showEmptyListMessage(imageView: ImageView, list: List<Recipe>?) {
        imageView.visibility = if (list.isNullOrEmpty()) View.VISIBLE else View.GONE
    }

}
