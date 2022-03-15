package szathmary.peter.mychat.ui.login

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import szathmary.peter.mychat.R
import szathmary.peter.mychat.database.DatabaseHandler
import szathmary.peter.mychat.databinding.ActivityLoginBinding
import szathmary.peter.mychat.logic.login.EmailChecker
import szathmary.peter.mychat.logic.login.LoginInformation
import szathmary.peter.mychat.logic.login.Password
import szathmary.peter.mychat.user.User

/**
 * Activity that is first shown at start of application
 * Activity for user login
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

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

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //textEdit of username(email)
        val email = binding.email
        //textEdit of password
        val password = binding.password
        //textEdit for username
        username = binding.username!!
        //button for login (is disabled)
        val login = binding.login
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
                if (username?.text?.isEmpty() == true || username?.text?.length!! < 3) {
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
                login.isEnabled = checkForLoginAvailability()
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
                login.isEnabled = checkForLoginAvailability()
            }

            override fun afterTextChanged(p0: Editable?) {
                return
            }

        })

        login.setOnClickListener {
            val loginInformation = LoginInformation(email.text.toString(), Password(password.text.toString()).getSecuredPassword(), username.text.toString())
            val dbreference = Firebase.database.getReference("User").child(loginInformation.username)

            dbreference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        if (user == User(loginInformation)) {
                            warningMessage?.text = "Vitaj, ${user.username}!"
                        } else {
                            warningMessage?.text = "Invalid login information!"
                        }
                    } else {
                        warningMessage?.text = "Invalid login information!"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    warningMessage?.text = error.toString()
                }

            })

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