package com.example.baro

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.baro.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // nav_host_fragment_activity_main 안에 있는 NavHostFragment 찾아서 NavController 가져오기
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setupWithNavController(navController)

        // ✅ 목적지에 따라 gnb 표시/숨김
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                // gnb를 보여줄 Fragment들의 id만 나열
                R.id.navigation_home,          // 홈 탭
                R.id.navigation_message,   // 메시지 탭
                R.id.navigation_bot    // 챗봇 탭
                    -> {
                    bottomNav.visibility = View.VISIBLE
                }

                else -> {
                    bottomNav.visibility = View.GONE
                }
            }
        }

    }
}
