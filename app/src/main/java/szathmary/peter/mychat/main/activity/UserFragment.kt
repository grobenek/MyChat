package szathmary.peter.mychat.main.activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat.finishAffinity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import szathmary.peter.mychat.R
import szathmary.peter.mychat.ui.login.RegisterOrLoginActivity

class UserFragment : Fragment() {

    lateinit var username: String
    lateinit var email: String

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

        logoutButton.setOnClickListener {
            GlobalScope.launch { // launch new coroutine in background and continue
                delay(1000) // non-blocking delay for 1 second (default time unit is ms)

                activity?.finishAffinity() // clear activity stack
                val switchActivityIntent =
                    Intent(activity, RegisterOrLoginActivity::class.java)
                startActivity(switchActivityIntent)
            }
        }
    }

    private fun retrieveUserInformation() {
        username = activity?.intent?.getStringExtra("username").toString()
        email = activity?.intent?.getStringExtra("email").toString()
    }


}