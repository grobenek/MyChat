package szathmary.peter.mychat.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import szathmary.peter.mychat.R
import szathmary.peter.mychat.databinding.ActivityLoginBinding
import szathmary.peter.mychat.logic.login.EmailChecker

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var passwordReady = false
    private var usernameReady = false
    private var errorMassegePassword: TextView? = null
    private var crossPassword: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.username
        val password = binding.password
        val login = binding.login
        errorMassegePassword = binding.errorMessagePassword
        crossPassword = binding.crossPassword
        crossPassword?.isVisible = false

        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
               passwordReady = !isPasswordInefficient(password.text.length < 6 && password.text.isNotEmpty())
                login.isEnabled = checkForLoginAvilability()
            }

            override fun afterTextChanged(p0: Editable?) {
                return
            }
        })

        username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                usernameReady = EmailChecker().isValidEmail(username.text)
                login.isEnabled = checkForLoginAvilability()
            }

            override fun afterTextChanged(p0: Editable?) {
                return
            }

        })

        login.setOnClickListener{
            //LOGIN METODA
        }
    }

    private fun isPasswordInefficient(state: Boolean): Boolean {
        if (!state) {
            crossPassword?.isVisible = false
            errorMassegePassword?.isVisible = false
        } else {
            crossPassword?.isVisible = true
            errorMassegePassword?.text = getString(R.string.password_error_message)
        }
        return state
    }

    fun checkForLoginAvilability(): Boolean {
        return usernameReady && passwordReady
    }
}