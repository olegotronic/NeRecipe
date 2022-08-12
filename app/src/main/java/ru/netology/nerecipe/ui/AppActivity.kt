package ru.netology.nerecipe.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nerecipe.R
import ru.netology.nerecipe.util.Filters

class AppActivity : AppCompatActivity(R.layout.app_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Filters.initCategoriesFromResource(applicationContext, R.menu.category_menu)
    }

}
