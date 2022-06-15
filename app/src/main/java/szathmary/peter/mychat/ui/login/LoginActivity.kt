package szathmary.peter.mychat.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import szathmary.peter.mychat.R
import szathmary.peter.mychat.databinding.ActivityLoginBinding
import szathmary.peter.mychat.logic.login.InternetConnectionChecker
import szathmary.peter.mychat.logic.login.LoginInformation
import szathmary.peter.mychat.logic.login.Password
import szathmary.peter.mychat.main.activity.MainScreenActivityRegular
import szathmary.peter.mychat.user.User

/**
 * Activity for user login
 */
class LoginActivity : AppCompatActivity() {
    private var usernameReady = false // indicator if username is in correct state
    private var passwordReady = false // indicator if password is in correct state

    private lateinit var binding: ActivityLoginBinding

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.crossUsernameLogin.isVisible = false
        binding.crossPasswordLogin.isVisible = false

        binding.loginButton.isEnabled = true


        attachListenerToLoginButton()

        binding.usernameTextEditLogin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            /**
             * If username is changed, checks it's condition
             */
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.usernameTextEditLogin.text?.isEmpty() == true || binding.usernameTextEditLogin.text?.length!! < 3) {
                    binding.crossUsernameLogin.isVisible = true
                    usernameReady = false
                    binding.errorWarningMessageLogin.text =
                        getString(R.string.username_error_message)
                } else {
                    binding.crossUsernameLogin.isVisible = false
                    usernameReady = true
                    binding.errorWarningMessageLogin.text = getString(R.string.empty_string)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                return
            }
        })

        binding.passwordTextEditLogin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            /**
             * If password is changed, checks it's condition
             */
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                passwordReady =
                    !isPasswordInefficient(binding.passwordTextEditLogin.text.length < 6 && binding.passwordTextEditLogin.text.isNotEmpty())
                binding.loginButton.isEnabled = checkForLoginAvailability()
            }

            override fun afterTextChanged(p0: Editable?) {
                return
            }

        })
    }

    /**
     * Attach listener to login button that will try to login user
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun attachListenerToLoginButton() {
        binding.loginButton.setOnClickListener {

            binding.errorWarningMessageLogin.text = getString(R.string.empty_string)
            if (!InternetConnectionChecker().hasInternetConnection(this)) {
                binding.errorWarningMessageLogin.text =
                    "You are not connected to the internet!" // dat ako text
                return@setOnClickListener
            }
            if (binding.usernameTextEditLogin.text.isEmpty() || binding.passwordTextEditLogin.text.isEmpty()) {
                binding.errorWarningMessageLogin.text = "You must fill out all forms!"
                return@setOnClickListener
            }

            binding.loginButton.isEnabled = false

            val loginInformationFromUser = LoginInformation(
                "",
                Password(binding.passwordTextEditLogin.text.toString()).getSecuredPassword(),
                binding.usernameTextEditLogin.text.toString()
            )
            tryToLoginUser(loginInformationFromUser)
        }
    }

    /**
     * Try to login user and switch to RegisterOrLoginActivity
     *
     * @param loginInformationFromUser login information of user that needs to be checked
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun tryToLoginUser(loginInformationFromUser: LoginInformation) {
        val dbreference = Firebase.database.getReference("User")
            .child(binding.usernameTextEditLogin.text.toString())

        lateinit var loginInformationFromDatabase: LoginInformation
        dbreference.addListenerForSingleValueEvent(object : ValueEventListener {
            /**
             * Checks if user is in database, if yes, switch to MainScreenActivity
             */
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    loginInformationFromDatabase = LoginInformation(user)
                    if (loginInformationFromDatabase.username == loginInformationFromUser.username && loginInformationFromDatabase.password == loginInformationFromUser.password) {
                        binding.errorWarningMessageLogin.text = getString(
                            R.string.welcome_message,
                            binding.usernameTextEditLogin.text.toString()
                        )
                        GlobalScope.launch { // launch new coroutine in background and continue
                            delay(1000) // non-blocking delay for 1 second (default time unit is ms)
                            switchToMainScreenActivity(
                                loginInformationFromUser,
                                loginInformationFromDatabase
                            )
                        }
                    } else {
                        binding.errorWarningMessageLogin.text =
                            getString(R.string.invalid_login)
                        binding.loginButton.isEnabled = true
                    }
                } else {
                    binding.errorWarningMessageLogin.text = getString(R.string.invalid_login)
                    binding.loginButton.isEnabled = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.errorWarningMessageLogin.text = error.toString()
                binding.loginButton.isEnabled = true
            }
        })
    }

    /**
     * Switch to MainScreenActivity and send username and email along
     *
     * @param loginInformationFromDatabase login information from database
     * @param loginInformationFromUser login information from user
     */
    private fun switchToMainScreenActivity(
        loginInformationFromUser: LoginInformation,
        loginInformationFromDatabase: LoginInformation
    ) {
        val switchActivityIntent =
            Intent(
                this@LoginActivity,
                MainScreenActivityRegular::class.java
            )
        switchActivityIntent.putExtra(
            "username",
            loginInformationFromUser.username
        )
        switchActivityIntent.putExtra(
            "email",
            loginInformationFromDatabase.email
        )

        startActivity(switchActivityIntent)
    }

    /**
     * Set password cross and warning message based on the state of password
     *
     * @param conditionOfPassword condition of password
     */
    private fun isPasswordInefficient(conditionOfPassword: Boolean): Boolean {
        if (!conditionOfPassword) {
            binding.crossPasswordLogin.isVisible = false
            binding.errorWarningMessageLogin.text = getString(R.string.empty_string)
        } else {
            binding.crossPasswordLogin.isVisible = true
            binding.errorWarningMessageLogin.text = getString(R.string.password_error_message)
        }
        return conditionOfPassword
    }


    /**
     * Checks if both email, password and username are in right formats
     *
     * @return true if both username, email and password are in right formats, else false
     */
    private fun checkForLoginAvailability(): Boolean {
        return passwordReady && usernameReady
    }
}