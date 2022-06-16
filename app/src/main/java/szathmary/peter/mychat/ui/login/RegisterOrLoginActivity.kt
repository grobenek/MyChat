package szathmary.peter.mychat.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import szathmary.peter.mychat.databinding.ActivityRegisterOrLoginBinding

/**
 * Activity for user to decide between login and register
 */
class RegisterOrLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterOrLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterOrLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            switchToRegisterActivity()
        }

        binding.loginButton.setOnClickListener {
            switchToLoginActivity()
        }
    }

    /**
     * Switch to LoginActivity
     */
    private fun switchToLoginActivity() {
        val switchActivityIntent = Intent(this, LoginActivity::class.java)
        startActivity(switchActivityIntent)
    }

    /**
     * Switch to RegisterActivity
     */
    private fun switchToRegisterActivity() {
        val switchActivityIntent = Intent(this, RegisterActivity::class.java)
        startActivity(switchActivityIntent)
    }
}