package szathmary.peter.mychat.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import szathmary.peter.mychat.R
import szathmary.peter.mychat.databinding.ActivityRegisterBinding
import szathmary.peter.mychat.logic.login.EmailChecker
import szathmary.peter.mychat.logic.login.InternetConnectionChecker
import szathmary.peter.mychat.logic.login.LoginInformation
import szathmary.peter.mychat.logic.login.Password
import szathmary.peter.mychat.main.activity.MainScreenActivityRegular
import szathmary.peter.mychat.user.User

/**
 * Activity that is first shown at start of application
 * Activity for user login
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    //indicating if password is in good format
    private var passwordReady = false

    //indicating if email is in right email format
    private var emailReady = false

    //indicating if username is valid
    private var usernameReady = false
    private var warningMessage: TextView? = null
    private var crossPassword: ImageView? = null
    private var crossUsername: ImageView? = null
    private lateinit var username: EditText


    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //textEdit of username(email)
        val email = binding.email
        //textEdit of password
        val password = binding.password
        //textEdit for username
        username = binding.username!!
        //button for login (is disabled)
        val register = binding.register
        warningMessage = binding.errorWarningMessage
        crossPassword = binding.crossPassword
        crossPassword?.isVisible = false
        crossUsername = binding.crossUsername
        crossUsername?.isVisible = false

        binding.username?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (username.text?.isEmpty() == true || username.text?.length!! < 3) {
                    crossUsername?.isVisible = true
                    usernameReady = false
                    warningMessage?.text = getString(R.string.username_error_message)
                } else {
                    crossUsername?.isVisible = false
                    usernameReady = true
                    warningMessage?.text = getString(R.string.empty_string)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                return
            }

        })

        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            /**
             * Checks if password is in right format, than checks if password and username are both in right state,
             * if yes, login button is enabled
             */
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                passwordReady =
                    !isPasswordInefficient(password.text.length < 6 && password.text.isNotEmpty())
                register.isEnabled = checkForLoginAvailability()
            }

            override fun afterTextChanged(p0: Editable?) {
                return
            }
        })

        email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            /**
             * Checks if email is in right format, than checks if password and username are both in right state,
             * if yes, login button is enabled
             */
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                emailReady = EmailChecker().isValidEmail(email.text)
                register.isEnabled = checkForLoginAvailability()
            }

            override fun afterTextChanged(p0: Editable?) {
                return
            }

        })

        /**
         * Register user and switch to LoginActivity
         */
        register.setOnClickListener {
            binding.errorWarningMessage?.text = getString(R.string.empty_string)
            if (!InternetConnectionChecker().hasInternetConnection(this)) {
                binding.errorWarningMessage?.text = "You are not connected to the internet!" // dat ako text
                return@setOnClickListener
            }
            val loginInformationFromUser = LoginInformation(
                email.text.toString(),
                Password(password.text.toString()).getSecuredPassword(),
                username.text.toString()
            )//TODO sprav kontrolu role
            val dbreference =
                Firebase.database.getReference("User").child(loginInformationFromUser.username)

            dbreference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        Log.v("USER JE  NULL", "USER JE NULL")
                        // checks if user with this name already exist
                        if (user.username == loginInformationFromUser.username) {
                            warningMessage?.text = getString(R.string.error_username_exists)
                            return
                        }
                    }
                    // create user in database and switch activity to LoginActivity
                    Firebase.database.getReference("User")
                        .child(loginInformationFromUser.username)
                        .setValue(User(loginInformationFromUser))
                    warningMessage?.text = getString(R.string.registration_succesfull)
                    GlobalScope.launch { // launch new coroutine in background and continue
                        delay(1500) // non-blocking delay for 1 second (default time unit is ms)
                        val switchActivityIntent =
                            Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(switchActivityIntent)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    warningMessage?.text = error.toString()
                }
            })
        }
    }

    /**
     * Waits 1500ms and change activity to MainScreenActivityRegular
     *
     * @param loginInformation login information of usert
     */

    @OptIn(DelicateCoroutinesApi::class)
    fun changeToMainScreenActivity(loginInformation: LoginInformation) {
        val registerActivity: RegisterActivity = this
        GlobalScope.launch { // launch new coroutine in background and continue
            delay(1500) // non-blocking delay for 1 second (default time unit is ms)
            val switchActivityIntent =
                Intent(registerActivity, MainScreenActivityRegular::class.java)
            switchActivityIntent.putExtra("username", loginInformation.username)
            switchActivityIntent.putExtra("password", loginInformation.password)
            switchActivityIntent.putExtra("email", loginInformation.email)
            startActivity(switchActivityIntent)
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
            warningMessage?.text = getString(R.string.empty_string)
        } else {
            crossPassword?.isVisible = true
            warningMessage?.text = getString(R.string.password_error_message)
        }
        return conditionOfPassword
    }


    /**
     * Checks if both email, password and username are in right formats
     *
     * @return true if both username, email and password are in right formats, else false
     */
    fun checkForLoginAvailability(): Boolean {
        return emailReady && passwordReady && usernameReady
    }
}