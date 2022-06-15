package szathmary.peter.mychat.main.activity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import szathmary.peter.mychat.R
import szathmary.peter.mychat.logic.login.InternetConnectionChecker
import szathmary.peter.mychat.message.Message

/**
 * Fragment for reading/sending messages
 */
class MessagesFragment : Fragment() {

    private val adapter = MessagesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //reference for part of database with messages
        val dbreference = Firebase.database.getReference("Messages")
        dbreference.addChildEventListener(object : ChildEventListener {
            /**
             * If new message is added to the database, it will be displayed in recycleView
             */
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val messageToAdd = Message(
                    snapshot.child("sender").value as String?,
                    snapshot.child("text").value as String?,
                )

                // System announcement about actual users online status won't be shown
                if (messageToAdd.sender == "System" && messageToAdd.text.toString()
                        .contains(activity?.intent?.getStringExtra("username").toString())
                ) {
                    return
                }
                Log.v("New message added", messageToAdd.sender!!)
                addMessageToRecycleView(messageToAdd)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                return
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                return
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                return
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Error in reading new messages from database", error.message)
            }
        })
    }

    /**
     * Add message to the recycleView
     *
     * @param messageToAdd Message that will be added to the recycleView
     */
    private fun addMessageToRecycleView(messageToAdd: Message) {
        MessageList.addMessage(messageToAdd)
        adapter.notifyItemRangeRemoved(MessageList.size(), 1)
        // scrolling to the bottom of recycle view if display is rotated
        if (view != null) {
            val rvMessages: RecyclerView =
                view!!.findViewById(R.id.messages_recycle) as RecyclerView
            rvMessages.scrollToPosition(MessageList.size() - 1)
        }
    }

    //skusam
    override fun onStart() {
        super.onStart()
        val rvMessages: RecyclerView = view!!.findViewById(R.id.messages_recycle) as RecyclerView
        rvMessages.scrollToPosition(MessageList.size() - 1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    // will be called only on creation of the fragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initialization of recycle view and connecting adapter to it
        val rvMessages: RecyclerView = view.findViewById(R.id.messages_recycle) as RecyclerView
        rvMessages.adapter = adapter
        rvMessages.layoutManager = LinearLayoutManager(this.context)

        val sendButton: ImageButton = view.findViewById(R.id.send_button)
        val messageInput: EditText = view.findViewById(R.id.message_input)


        attachListenerOnSendButton(sendButton, messageInput)
    }

    /**
     * Attach listener to the button that sends new message to the database
     *
     * @param sendButton button to which listener will be attached to
     * @param messageInput EditText with text of the message that will be sent
     */
    private fun attachListenerOnSendButton(
        sendButton: ImageButton,
        messageInput: EditText
    ) {
        sendButton.setOnClickListener {
            // if device has no internet connection, it will make a toast and nothing will happen
            if (activity != null) {
                if (!InternetConnectionChecker().hasInternetConnection(activity?.applicationContext!!)) {
                    Toast.makeText(
                        activity?.applicationContext,
                        "You are not connected to the internet!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            }
            if (messageInput.text.isNotEmpty() && messageInput.text.toString()
                    .filterNot { it.isWhitespace() }
                    .isNotEmpty() // checking if only space is in message
            ) {
                sendMessageToTheDatabase(messageInput)
            }
            // resetting input
            messageInput.setText("")
        }
    }

    /**
     * Sends message to the database
     *
     * @param messageInput EditText with text of the new message
     */
    private fun sendMessageToTheDatabase(messageInput: EditText) {
        val dbReferrence = Firebase.database.getReference("Messages")
        val newChild = dbReferrence.push()

        val key = newChild.key
        if (key != null) {
            dbReferrence.child(key).setValue(
                Message(
                    activity?.intent?.getStringExtra("username")
                        .toString(), // getting username of user from loginActivity
                    messageInput.text.toString()
                )
            )
        }
    }

    /**
     * Adapter for recycleView
     */
    inner class MessagesAdapter : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            // initiliazers
            val user: TextView = itemView.findViewById(R.id.message_user_name)
            val message: TextView = itemView.findViewById(R.id.message_text)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val context = parent.context

            val inflater = LayoutInflater.from(context)
            // Inflate the layoult
            val contactView = inflater.inflate(R.layout.item_list_message, parent, false)

            return ViewHolder(contactView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val message = MessageList.getMessage(position)

            val userText = holder.user
            val messageText = holder.message

            userText.text = message.sender

            // setting color of sender based on their "role"
            when (userText.text) {
                activity?.intent?.getStringExtra("username")
                    .toString() -> { // username of user from LoginActivity
                    userText.setTextColor(Color.rgb(179, 137, 237))
                }
                "System" -> {
                    userText.setTextColor(Color.CYAN)
                }
                else -> {
                    userText.setTextColor(Color.rgb(255, 195, 0))
                }
            }

            messageText.text = message.text
        }

        override fun getItemCount(): Int {
            return MessageList.size()
        }
    }
}

/**
 * List containing all messages displayed in recycleView
 */
object MessageList {
    private val messageList = ArrayList<Message>()

    /**
     * Add message to the messageList
     *
     * @param message message to be added
     */
    fun addMessage(message: Message) {
        this.messageList.add(message)
    }

    /**
     * Get message from the messageList
     *
     * @param position position of the message in messageList
     *
     * @return message on given position
     */
    fun getMessage(position: Int): Message {
        return this.messageList[position]
    }

    /**
     * Returns number of messages in messageList
     *
     * @return number of messages in messageList
     */
    fun size(): Int {
        return this.messageList.size
    }
}