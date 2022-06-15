package szathmary.peter.mychat.main.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import szathmary.peter.mychat.R
import szathmary.peter.mychat.ui.login.RegisterOrLoginActivity

/**
 * Fragment for displaying user's information
 */
class UserFragment : Fragment() {

    private lateinit var username: String
    private lateinit var email: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usernameTextView: TextView = view.findViewById(R.id.username_info)
        val emailTextView: TextView = view.findViewById(R.id.email_info)

        retrieveUserInformation()

        usernameTextView.text = username
        emailTextView.text = email

        val logoutButton: Button = view.findViewById(R.id.logout_button)

        attachListenerToTheLogoutButton(logoutButton)

        val forgotPassword : TextView = view.findViewById(R.id.forgot_password)

        forgotPassword.setOnClickListener{
            logoutButton.isEnabled = false
            GlobalScope.launch { // launch new coroutine in background and continue
                delay(1000) // non-blocking delay for 1 second (default time unit is ms)
              switchToResetPasswordActivity()
            }
        }
    }

    /**
     * Attach listener to the logout button that switch back to the registerOrLoginActivity
     *
     * @param logoutButton button that listener will be attached to
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun attachListenerToTheLogoutButton(logoutButton: Button) {
        logoutButton.setOnClickListener {
            logoutButton.isEnabled = false
            GlobalScope.launch { // launch new coroutine in background and continue
                delay(1000) // non-blocking delay for 1 second (default time unit is ms)

                switchToRegisterOrLoginActivity()
            }
        }
    }

    /**
     * Switch to RegisterOrLoginActivity and clear activity stack
     */
    private fun switchToRegisterOrLoginActivity() {
        activity?.finishAffinity() // clear activity stack
        val switchActivityIntent =
            Intent(activity, RegisterOrLoginActivity::class.java)
        startActivity(switchActivityIntent)
    }

    /**
     * Switch to ResetPasswordActivity and clear activity stack
     */
    private fun switchToResetPasswordActivity() {
        activity?.finishAffinity() // clear activity stack
        val switchActivityIntent =
            Intent(activity, ResetPasswordActivity::class.java)
        switchActivityIntent.putExtra("username", username)
        startActivity(switchActivityIntent)
    }

    /**
     * Get username and email from LoginActivity
     */
    private fun retrieveUserInformation() {
        username = activity?.intent?.getStringExtra("username").toString()
        email = activity?.intent?.getStringExtra("email").toString()
    }
}