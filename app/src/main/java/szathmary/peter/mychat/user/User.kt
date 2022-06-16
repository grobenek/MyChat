package szathmary.peter.mychat.user

import szathmary.peter.mychat.logic.login.LoginInformation

/**
 * Object representation of User
 */
data class User(
    var password: Long? = null,
    var email: String? = null,
    var username: String? = null
) {
    constructor(loginInformation: LoginInformation) : this(
        loginInformation.password,
        loginInformation.email,
        loginInformation.username,
    )
}

