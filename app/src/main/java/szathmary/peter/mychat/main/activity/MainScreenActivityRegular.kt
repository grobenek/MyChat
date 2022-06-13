package szathmary.peter.mychat.main.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import szathmary.peter.mychat.R
import szathmary.peter.mychat.databinding.ActivityMainScreenBinding
import szathmary.peter.mychat.message.Message


class MainScreenActivityRegular : AppCompatActivity() {

    private lateinit var binding: ActivityMainScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //zo stranky
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_main
        ) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = binding.bottomNavigationView
        NavigationUI.setupWithNavController(bottomNavigationView, navController)


        // system notification that user is online - najdi sposob aby to userovi neukazalochat
        val dbReferrence = Firebase.database.getReference("Messages")
        val newChild = dbReferrence.push()

        val key = newChild.key
        if (key != null) {
            dbReferrence.child(key).setValue(
                Message(
                    "System",
                    intent?.getStringExtra("username").toString() + " is online!"
                )
            )
        }

    }

    //disabled bavk button
    override fun onBackPressed() {
        Toast.makeText(applicationContext,"You must logout in User page!", Toast.LENGTH_SHORT).show()
        return
    }
}