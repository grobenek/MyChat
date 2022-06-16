package szathmary.peter.mychat.logic.login

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Class for checking if device has active internet connection
 */
class InternetConnectionChecker {
    /**
     * checks if device has active internet connection
     *@param context Context
     *
     * @return true if device has active internet connection, else false
     *
     * @see https://stackoverflow.com/questions/6493517/detect-if-android-device-has-internet-connection
     */
    fun hasInternetConnection(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager
            .getNetworkCapabilities(network)
        return (capabilities != null
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED))
    }
}