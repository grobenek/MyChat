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
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import szathmary.peter.mychat.R
import szathmary.peter.mychat.logic.login.InternetConnectionChecker
import szathmary.peter.mychat.message.Message


class MessagesFragment : Fragment() {

    val adapter = MessagesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //referencia na cast databazy so spravami
        val dbreference = Firebase.database.getReference("Messages")
        dbreference.addChildEventListener(object : ChildEventListener {
            // ak sa prida sprava, zobrazi sa v recycleView
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val messageToAdd = Message(
                    snapshot.child("sender").value as String?,
                    snapshot.child("text").value as String?,
                )
                if (messageToAdd.sender == "System" && messageToAdd.text.toString().contains(activity?.intent?.getStringExtra("username").toString())) {
                    return
                }
                Log.v("NEW CHILD ADDED", messageToAdd.sender!!)
                MessageList.addMessage(messageToAdd)
                adapter.notifyItemRangeRemoved(MessageList.size(), 1)
                //aby sa scrollovalo automaticky na spodok ak je view vytvoreny (nefunguje pri otoceni obrazovky)
                if (view != null) {
                    val rvMessages: RecyclerView = view!!.findViewById(R.id.messages_recycle) as RecyclerView
                    rvMessages.scrollToPosition(MessageList.size()-1)
                }
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

    //skusam
    override fun onStart() {
        super.onStart()
        val rvMessages: RecyclerView = view!!.findViewById(R.id.messages_recycle) as RecyclerView
        rvMessages.scrollToPosition(MessageList.size()-1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    // spusti sa len raz pri prvom vytvoreni fragmentu
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //vytvori sa recycleView, pripoji sa adapter
        val rvMessages: RecyclerView = view.findViewById(R.id.messages_recycle) as RecyclerView
        rvMessages.adapter = adapter
        rvMessages.layoutManager = LinearLayoutManager(this.context)
        // sending messages
        val sendButton : ImageButton = view.findViewById(R.id.send_button)
        val messageInput : EditText = view.findViewById(R.id.message_input)

        //ak stlacim tlacidlo a nemam prazdny text, text sa odosle ako sprava do databazy
        sendButton.setOnClickListener{
            if (activity != null) {
                if (!InternetConnectionChecker().hasInternetConnection(activity?.applicationContext!!)) {
                    Toast.makeText(activity?.applicationContext,"You are not connected to the internet!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
            if (messageInput.text.isNotEmpty() && messageInput.text.toString().filterNot { it.isWhitespace() }.isNotEmpty()) {
                val dbReferrence = Firebase.database.getReference("Messages")
                val newChild = dbReferrence.push()

                val key = newChild.key
                if (key != null) {
                    dbReferrence.child(key).setValue(Message(activity?.intent?.getStringExtra("username").toString(),
                        messageInput.text.toString()
                    ))
                }
            }
            messageInput.setText("")
        }
    }

    inner class MessagesAdapter : RecyclerView.Adapter<MessagesAdapter.ViewHolder>()
    {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            // Your holder should contain and initialize a member variable
            // for any view that will be set as you render a row
            val user: TextView = itemView.findViewById(R.id.message_user_name)
            val message: TextView = itemView.findViewById(R.id.message_text)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val context = parent.context
            val inflater = LayoutInflater.from(context)
            // Inflate the custom layout
            val contactView = inflater.inflate(R.layout.item_list_message, parent, false)
            // Return a new holder instance
            return ViewHolder(contactView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val message = MessageList.getMessage(position)

            val userText = holder.user
            val messageText = holder.message

            userText.text = message.sender
            //TODO vypisat spravu na kontrolu mailu
            //TODO disable button pri registracii

            // ak to je moja sprava, nastavim farbu na cervenu
            if (userText.text == activity?.intent?.getStringExtra("username").toString()) {
                userText.setTextColor(Color.rgb(179, 137, 237))
            } else if (userText.text == "System") {
                userText.setTextColor(Color.CYAN)
            } else {
                userText.setTextColor(Color.rgb(255, 195, 0))
            }

            messageText.text = message.text
        }

        override fun getItemCount(): Int {
            return MessageList.size()
        }
    }
}

object MessageList{
    private val messageList = ArrayList<Message>()

    fun addMessage(message: Message) {
        this.messageList.add(message)
    }

    fun removeMessage(message: Message) {
        this.messageList.remove(message)
    }

    fun removeMessage(position: Int) {
        this.removeMessage(position)
    }

    fun getMessage(position: Int): Message {
        return this.messageList[position]
    }

    fun size(): Int {
        return this.messageList.size
    }
}