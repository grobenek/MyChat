package szathmary.peter.mychat.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import szathmary.peter.mychat.logic.login.LoginInformation
import szathmary.peter.mychat.logic.login.Password
import szathmary.peter.mychat.main.activity.MainScreenActivityRegular
import szathmary.peter.mychat.user.User

class LoginActivity : AppCompatActivity() {
    private var usernameReady = false
    private var passwordReady = false

    private lateinit var binding: ActivityLoginBinding

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.crossUsernameLogin.isVisible = false
        binding.crossPasswordLogin.isVisible = false

        /**
         * Checks if user with set loginInformation exists and switch to MainScreenActivityRegular
         */
        binding.loginButton.setOnClickListener {
            val loginInformationFromUser = LoginInformation("", Password(binding.passwordTextEditLogin.text.toString()).getSecuredPassword(), binding.usernameTextEditLogin.text.toString(), null)//TODO sprav kontrolu role
            val dbreference = Firebase.database.getReference("User").child(binding.usernameTextEditLogin.text.toString())
            lateinit var loginInformationFromDatabase: LoginInformation
            dbreference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        loginInformationFromDatabase = LoginInformation(user)
                        if (loginInformationFromDatabase.username == loginInformationFromUser.username && loginInformationFromDatabase.password == loginInformationFromUser.password) {
                            binding.errorWarningMessageLogin.text = getString(R.string.welcome_message, binding.usernameTextEditLogin.text.toString())
                            GlobalScope.launch { // launch new coroutine in background and continue
                                delay(1000) // non-blocking delay for 1 second (default time unit is ms)
                                val switchActivityIntent =
                                    Intent(this@LoginActivity, MainScreenActivityRegular::class.java)
                                switchActivityIntent.putExtra("username", loginInformationFromUser.username)
                                switchActivityIntent.putExtra("role", loginInformationFromUser.role.toString())
                                switchActivityIntent.putExtra("email", loginInformationFromDatabase.email)

                                startActivity(switchActivityIntent)
                            }
                        } else {
                            binding.errorWarningMessageLogin.text = getString(R.string.invalid_login)
                        }
                    } else {
                        binding.errorWarningMessageLogin.text = getString(R.string.invalid_login)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.errorWarningMessageLogin.text = error.toString()
                }
            })
        }

        binding.usernameTextEditLogin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

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
    fun checkForLoginAvailability(): Boolean {
        return passwordReady && usernameReady
    }
}