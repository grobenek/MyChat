package szathmary.peter.mychat.main.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import szathmary.peter.mychat.R
import szathmary.peter.mychat.message.Message


class MessagesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvMessages: RecyclerView = view.findViewById(R.id.messages_recycle) as RecyclerView

        val adapter = MessagesAdapter()

        rvMessages.adapter = adapter
        rvMessages.layoutManager = LinearLayoutManager(this.context)


        val dbreference = Firebase.database.getReference("Messages") // prve naplnenie arrayu sprav

        dbreference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val messageToAdd = Message(
                        snapshot.child("sender").value as String?,
                        snapshot.child("text").value as String?
                    )
                    Log.v("NEW CHILD ADDED", messageToAdd.sender!!)
                    MessageList.addMessage(messageToAdd)
                    adapter.notifyItemRangeRemoved(MessageList.size(), 1)
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


        //TODO spravit odosielanie sprav cez textEdit
        val messageInput : EditText = view.findViewById(R.id.message_input)




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