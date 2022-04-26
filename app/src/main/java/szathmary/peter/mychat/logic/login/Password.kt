package szathmary.peter.mychat.logic.login

/**
 * Data class for password with function to hash itself at initialization
 */
data class Password(val password: String? = null) {
    constructor(password: Long) : this(){
        hashedPassword = password
    }

    var hashedPassword: Long = 0
    init {
        hashedPassword = password.hashCode().toLong()
    }

    fun getSecuredPassword(): Long {
        return hashedPassword
    }
}