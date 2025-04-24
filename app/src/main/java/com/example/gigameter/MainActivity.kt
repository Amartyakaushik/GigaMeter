package com.example.gigameter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gigameter.fragments.DashboardFragment
import com.example.gigameter.fragments.ElectricityFragment
import com.example.gigameter.fragments.GasFragment
import com.example.gigameter.fragments.WaterFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    selectedFragment = DashboardFragment()
                }
                R.id.navigation_electricity -> {
                    selectedFragment = ElectricityFragment()
                }
                R.id.navigation_water -> {
                    selectedFragment = WaterFragment()
                }
                R.id.navigation_gas -> {
                    selectedFragment = GasFragment()
                }
            }
            if (selectedFragment != null) {
                loadFragment(selectedFragment)
            }
            true // Return true to display the item as the selected item
        }

        // Load default fragment
        if (savedInstanceState == null) {
            bottomNavigation.selectedItemId = R.id.navigation_dashboard // Set default selection
            loadFragment(DashboardFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            // Optional: Add transition
            // .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }
}