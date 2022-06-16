package szathmary.peter.mychat.main.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import szathmary.peter.mychat.R
import szathmary.peter.mychat.databinding.ActivityResetPasswordBinding
import szathmary.peter.mychat.logic.login.InternetConnectionChecker
import szathmary.peter.mychat.logic.login.Password
import szathmary.peter.mychat.ui.login.LoginActivity

class ResetPasswordActivity : AppCompatActivity() {

    private var passwordRepeatReady = false // indicator if username is in correct state
    private var passwordReady = false // indicator if password is in correct state

    private lateinit var binding: ActivityResetPasswordBinding

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.crossPasswordResetPassword.isVisible = false
        binding.crossPasswordResetPasswordRepeat.isVisible = false

        binding.passwordPasswordReset.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            /**
             * If password is changed, checks it's condition and set crossPassword and warningMessage
             */
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                passwordReady =
                    (binding.passwordPasswordReset.text.length < 6 && binding.passwordPasswordReset.text.isNotEmpty())
                if (!passwordReady) {
                    binding.crossPasswordResetPassword.isVisible = false
                    binding.errorWarningMessageReset.text = getString(R.string.empty_string)
                } else {
                    binding.crossPasswordResetPassword.isVisible = true
                    binding.errorWarningMessageReset.text =
                        getString(R.string.password_error_message)
                }
                binding.resetPasswordButton.isEnabled = checkForLoginAvailability()
            }

            override fun afterTextChanged(p0: Editable?) {
                return
            }
        })

        binding.passwordRepeatReset.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            /**
             * If passwordRepeat is changed, checks it's condition and set crossPassword and warningMessage
             */
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                passwordReady =
                    (binding.passwordPasswordReset.text.toString() == binding.passwordRepeatReset.text.toString())

                if (!passwordReady) {
                    binding.crossPasswordResetPasswordRepeat.isVisible = true
                    binding.errorWarningMessageReset.text =
                        getString(R.string.passwords_not_matching)
                } else {
                    binding.crossPasswordResetPasswordRepeat.isVisible = false
                    binding.errorWarningMessageReset.text = getString(R.string.empty_string)
                    binding.resetPasswordButton.isEnabled = true
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                return
            }
        })

        // change password in database and change to LoginActivity
        binding.resetPasswordButton.setOnClickListener {

            if (!InternetConnectionChecker().hasInternetConnection(applicationContext)) {
                binding.errorWarningMessageReset.text = getString(R.string.no_internet)
                return@setOnClickListener
            }

            val dbReference = Firebase.database.getReference("User")
                .child(intent?.getStringExtra("username").toString()).child("password")
            dbReference.setValue(Password(binding.passwordPasswordReset.text.toString()).getSecuredPassword())

            binding.errorWarningMessageReset.text = getString(R.string.password_change_successfully)
            binding.resetPasswordButton.isEnabled = false

            GlobalScope.launch { // launch new coroutine in background and continue
                delay(1000) // non-blocking delay for 1 second (default time unit is ms)
                switchToLoginActivity()
            }
        }
    }

    /**
     * Switch to ResetPasswordActivity and clear activity stack
     */
    private fun switchToLoginActivity() {
        this.finishAffinity() // clear activity stack
        intent?.removeExtra("username")
        intent?.removeExtra("email")
        val switchActivityIntent =
            Intent(this, LoginActivity::class.java)
        startActivity(switchActivityIntent)
    }

    /**
     * Checks if both email, password and username are in right formats
     *
     * @return true if both username, email and password are in right formats, else false
     */
    private fun checkForLoginAvailability(): Boolean {
        return passwordReady && passwordRepeatReady
    }
}