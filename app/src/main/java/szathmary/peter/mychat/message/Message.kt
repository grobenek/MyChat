package szathmary.peter.mychat.message

import szathmary.peter.mychat.user.User
import java.security.Timestamp
import java.util.*

data class Message(val sender: User, val text: String, val timeAndDate: Date)
