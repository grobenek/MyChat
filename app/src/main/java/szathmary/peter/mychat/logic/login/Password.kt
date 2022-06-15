package szathmary.peter.mychat.logic.login

/**
 * Data class for password with function to hash itself at initialization
 */
data class Password(val password: String? = null) {

    private var hashedPassword: Long = 0

    init {
        hashedPassword = password.hashCode().toLong()
    }

    /**
     * Getter of hashedPassword
     *
     * @return hashed password
     */
    fun getSecuredPassword(): Long {
        return hashedPassword
    }
}