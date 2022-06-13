package szathmary.peter.mychat.message

import szathmary.peter.mychat.user.User
import java.util.*

/**
 * Object representation of one message
 * @property sender sender of message (User)
 * @property text text of message
 * @property timeAndDate date and time of creation of message
 */
data class Message(var sender: String? = null, var text: String? = null) {
    //time and date (hopefully) of message
    private val timeAndDate = Calendar.getInstance()
}
