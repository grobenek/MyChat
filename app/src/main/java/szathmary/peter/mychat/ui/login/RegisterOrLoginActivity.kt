package szathmary.peter.mychat.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import szathmary.peter.mychat.databinding.ActivityRegisterOrLoginBinding


class RegisterOrLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterOrLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterOrLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener{
            val switchActivityIntent = Intent(this, RegisterActivity::class.java)
            startActivity(switchActivityIntent)
        }

        binding.loginButton.setOnClickListener{

        }
    }
}