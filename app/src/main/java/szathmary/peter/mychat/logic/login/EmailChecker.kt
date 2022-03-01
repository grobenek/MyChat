package szathmary.peter.mychat.logic.login

import android.text.TextUtils
import android.util.Patterns


class EmailChecker {
    fun isValidEmail(target: CharSequence?): Boolean {
        return if (TextUtils.isEmpty(target) && target == null) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }
}