package com.example.mvp_autorskiy_start

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.mvp_autorskiy_start.data.repository.FavoritesRepository
import com.example.mvp_autorskiy_start.data.repository.QuizRepository
import com.example.mvp_autorskiy_start.ui.test.TestMenuFragment
import com.example.mvp_autorskiy_start.ui.arguments.ArgumentsLibraryFragment
import com.example.mvp_autorskiy_start.ui.authors.AuthorsFragment
import com.example.mvp_autorskiy_start.ui.favorites.FavoritesFragment
import com.example.mvp_autorskiy_start.ui.home.HomeFragment
import com.example.mvp_autorskiy_start.ui.practice.PracticeFragment
import com.example.mvp_autorskiy_start.ui.profile.ProfileFragment
import com.example.mvp_autorskiy_start.ui.theory.TheoryFragment
import com.example.mvp_autorskiy_start.utils.MusicPlayerManager
import com.example.mvp_autorskiy_start.data.repository.ResourceMapper
import com.example.mvp_autorskiy_start.utils.SoundPlayer
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        QuizRepository.init(this)
        FavoritesRepository.init(this)
        MusicPlayerManager.init(this)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        toolbar = findViewById(R.id.topAppBar)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

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
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        // Здесь можно запустить музыку, если нужно
        // MusicPlayerManager.start(this, R.raw.some_track)
    }

    override fun onPause() {
        super.onPause()
        MusicPlayerManager.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicPlayerManager.stop()
        SoundPlayer.release()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    supportFragmentManager.popBackStack()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}