package szathmary.peter.mychat.main.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import szathmary.peter.mychat.R
import szathmary.peter.mychat.databinding.ActivityMainScreenBinding


class MainScreenActivityRegular : AppCompatActivity() {

    private lateinit var binding: ActivityMainScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val messagesFragment = MessagesFragment()
        val userFragment = UserFragment()

        //zo stranky
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_main
        ) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = binding.bottomNavigationView
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }

    //disabled bavk button
    override fun onBackPressed() {
        Toast.makeText(applicationContext,"You must logout in User page!", Toast.LENGTH_SHORT).show()
        return
    }
}