package szathmary.peter.mychat.logic.login

import android.text.TextUtils
import android.util.Patterns

/**
 * Class for checking format of username(email)
 */
class EmailChecker {
    /**
     * Checks for right format of username(email)
     *
     * @param target email to check
     *@return true if email is in right format, else false
     */
    fun isValidEmail(target: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}