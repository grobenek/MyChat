package szathmary.peter.mychat.logic.login

/**
 * Data class for password with function to hash itself at initialization
 */
data class Password(val password: String) {
    var hashedPassword: Int = 0
    init {
        hashedPassword =  password.hashCode()
    }
}