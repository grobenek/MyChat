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

/**
 * Activity that is first shown at start of application
 * Activity for user login
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    //indicating if password is in good format
    private var passwordReady = false
    //indicating if username(email) is in right email format
    private var usernameReady = false
    private var errorMessagePassword: TextView? = null
    private var crossPassword: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    
        //textEdit of username(email)
        val username = binding.username
        //textEdit of password
        val password = binding.password
        //button for login (is disabled)
        val login = binding.login
        errorMessagePassword = binding.errorMessagePassword
        crossPassword = binding.crossPassword
        crossPassword?.isVisible = false

        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            /**
             * Checks if password is in right format, than checks if password and username are both in right state,
             * if yes, login button is enabled
             */
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
               passwordReady = !isPasswordInefficient(password.text.length < 6 && password.text.isNotEmpty())
                login.isEnabled = checkForLoginAvailability()
            }

            override fun afterTextChanged(p0: Editable?) {
                return
            }
        })

        username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            /**
             * Checks if username(email) is in right format, than checks if password and username are both in right state,
             * if yes, login button is enabled
             */
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                usernameReady = EmailChecker().isValidEmail(username.text)
                login.isEnabled = checkForLoginAvailability()
            }

            override fun afterTextChanged(p0: Editable?) {
                return
            }

        })

        login.setOnClickListener{
            //LOGIN METHOD
        }
    }

    /**
     * If password is inefficient, set crossPassword to visible and displays informative text
     *
     * @param conditionOfPassword condition of password based on which are help signs displayed
     * @return condition of password
     */
    private fun isPasswordInefficient(conditionOfPassword: Boolean): Boolean {
        if (!conditionOfPassword) {
            crossPassword?.isVisible = false
            errorMessagePassword?.text = getString(R.string.empty_string)
        } else {
            crossPassword?.isVisible = true
            errorMessagePassword?.text = getString(R.string.password_error_message)
        }
        return conditionOfPassword
    }


    /**
     * Checks if both username(email) and password are in right formats
     * 
     * @return true if both username and password are in right formats, else false
     */
    fun checkForLoginAvailability(): Boolean {
        return usernameReady && passwordReady
    }
}