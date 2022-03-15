package szathmary.peter.mychat.database

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import szathmary.peter.mychat.logic.login.LoginInformation
import szathmary.peter.mychat.logic.login.Password
import szathmary.peter.mychat.user.User

 class DatabaseHandler { // DAJ ABSTRACT

     fun testConnectionToDatabase() {
         TODO(
             "Sprav napojenie na databazu, nejaky test connection" +
                     "Nezabudni ze toto je abstraktna trieda z ktorej bude potom delit admin aj bezny user" +
                     "Vseobecne sa bude kontrolovat login, vlozenie spravy" +
                     "Odstranenie spravy" +
                     "nacitanie sprav" +
                     "kontrola ci su vsetky aktualizovane" +
                     "zmena mena a hesla"
         )

     }
 }