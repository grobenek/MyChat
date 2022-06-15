package szathmary.peter.mychat.main.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import szathmary.peter.mychat.R
import szathmary.peter.mychat.databinding.ActivityMainScreenBinding
import szathmary.peter.mychat.message.Message

/**
 * Main activity of application
 */
class MainScreenActivityRegular : AppCompatActivity() {

    private lateinit var binding: ActivityMainScreenBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setting up navigation between two fragments
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_main
        ) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = binding.bottomNavigationView
        NavigationUI.setupWithNavController(bottomNavigationView, navController)


        sendOnlineNotification()
    }

    /**
     * Send message from System that user is online
     */
    private fun sendOnlineNotification() {
        val dbReferrence = Firebase.database.getReference("Messages")
        val newChild = dbReferrence.push()

        val key = newChild.key
        if (key != null) {
            dbReferrence.child(key).setValue(
                Message(
                    "System",
                    intent?.getStringExtra("username")
                        .toString() + " is online!" // getting username from LoginActivity
                )
            )
        }
    }

    /**
     * Disable back button and informs user that he needs to logout
     */
    override fun onBackPressed() {
        Toast.makeText(applicationContext, "You must logout in User page!", Toast.LENGTH_SHORT)
            .show()
        return
    }
}