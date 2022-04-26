package szathmary.peter.mychat.logic.login

import szathmary.peter.mychat.user.User
import szathmary.peter.mychat.user.UserRole

class LoginInformation(
    val email: String,
    val password: Long,
    val username: String,
    val role: UserRole?
) {

constructor(user: User) : this(user.email!!, user.password!!, user.username!!, user.role)

}


