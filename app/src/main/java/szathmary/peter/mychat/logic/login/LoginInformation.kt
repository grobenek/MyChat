package szathmary.peter.mychat.logic.login

import szathmary.peter.mychat.user.UserRole

data class LoginInformation(val email: String, val password: Long, val username: String, val role: UserRole)