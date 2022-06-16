package szathmary.peter.mychat.logic.login

import szathmary.peter.mychat.user.User

/**
 * Class for storing login information about user
 */
class LoginInformation(
    val email: String,
    val password: Long,
    val username: String,
) {

    constructor(user: User) : this(user.email!!, user.password!!, user.username!!)

}


