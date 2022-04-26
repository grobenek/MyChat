package szathmary.peter.mychat.user

import szathmary.peter.mychat.logic.login.LoginInformation

/**
 * Object representation of User
 *
 * @property username username of user
 * @property email email of user
 * @property password hashed password of user
 */
data class User(var password: Long? = null, var email: String? = null, var username: String? = null, var role: UserRole? = UserRole.REGULAR) {
    constructor(loginInformation: LoginInformation) : this(
        loginInformation.password,
        loginInformation.email,
        loginInformation.username,
        loginInformation.role
    )
}

