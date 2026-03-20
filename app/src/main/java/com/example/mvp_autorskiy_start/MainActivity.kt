package com.example.mvp_autorskiy_start

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.appbar.MaterialToolbar
import com.example.mvp_autorskiy_start.ui.theory.TheoryFragment
import com.example.mvp_autorskiy_start.ui.test.TestMenuFragment
import com.example.mvp_autorskiy_start.ui.practice.PracticeFragment
import com.example.mvp_autorskiy_start.ui.profile.ProfileFragment
import com.example.mvp_autorskiy_start.ui.authors.AuthorsFragment
import com.example.mvp_autorskiy_start.ui.arguments.CategoriesFragment
import com.example.mvp_autorskiy_start.ui.favorites.FavoritesFragment
import com.example.mvp_autorskiy_start.data.FavoritesRepository
import com.example.mvp_autorskiy_start.ui.home.HomeFragment
import androidx.fragment.app.FragmentManager
import com.example.mvp_autorskiy_start.ui.arguments.ArgumentsLibraryFragment
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Находим View
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        toolbar = findViewById(R.id.topAppBar)

        // Настраиваем Toolbar как ActionBar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Показывает стрелку "Назад" на тулбаре
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START) // Открываем шторку по клику на бургер
        }

        // Устанавливаем обработчик кликов по меню в шторке
        navigationView.setNavigationItemSelectedListener(this)
        FavoritesRepository.init(this)

        // Загружаем первый фрагмент (например, Теорию) при запуске
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    // Функция загрузки фрагмента в контейнер
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    // Обработка нажатий на пункты меню в шторке
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        when (item.itemId) {
            R.id.nav_home -> loadFragment(HomeFragment())
            R.id.nav_theory -> loadFragment(TheoryFragment())
            R.id.nav_practice -> loadFragment(PracticeFragment())
            R.id.nav_test -> loadFragment(TestMenuFragment())
            R.id.nav_library -> loadFragment(ArgumentsLibraryFragment())
            R.id.nav_authors -> loadFragment(AuthorsFragment())
            R.id.nav_favorites -> loadFragment(FavoritesFragment())
            R.id.nav_profile -> loadFragment(ProfileFragment())
        }
        drawerLayout.closeDrawer(GravityCompat.START) // Закрываем шторку после выбора
        return true
    }

    // Обработка системной кнопки "Назад": если шторка открыта, закрываем её, иначе выходим
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}