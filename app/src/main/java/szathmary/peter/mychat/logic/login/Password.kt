package szathmary.peter.mychat.logic.login

data class Password(val password: String) {
    var hashedPassword: Int = 0
    init {
        hashedPassword =  password.hashCode()
    }
}