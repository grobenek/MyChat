package szathmary.peter.mychat.user

import szathmary.peter.mychat.logic.login.Password

/**
 * Object representation of User
 *
 * @property username username of user
 * @property password hashed password of user
 */
data class User(val username: String, val password: Password)
