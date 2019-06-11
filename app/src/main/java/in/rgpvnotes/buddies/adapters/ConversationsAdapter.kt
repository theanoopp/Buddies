package `in`.rgpvnotes.buddies.adapters

import `in`.rgpvnotes.buddies.R
import `in`.rgpvnotes.buddies.activities.ChatActivity
import `in`.rgpvnotes.buddies.model.AppUser
import `in`.rgpvnotes.buddies.model.Conversation
import `in`.rgpvnotes.buddies.utils.ContactUtil
import `in`.rgpvnotes.buddies.viewHolder.ConversationsViewHolder
import `in`.rgpvnotes.buddies.viewModel.MessageViewModel
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ConversationsAdapter(

    private var items: List<Conversation>,
    val context: Context,
    private val callBack : ConversationInterface

) : RecyclerView.Adapter<ConversationsViewHolder>() {

    private val messageViewModel = ViewModelProviders.of(context as FragmentActivity).get(MessageViewModel::class.java)

    private val userId by lazy { FirebaseAuth.getInstance().uid }
    private val mDatabase: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationsViewHolder {
        return ConversationsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.single_conversatio_row,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: ConversationsViewHolder, position: Int) {

        val friendObject = items[position]
        val friendId = friendObject.conversationId

        mDatabase.collection("users").document(friendId)
            .addSnapshotListener(context as FragmentActivity) { documentSnapshot, _ ->

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val friend = documentSnapshot.toObject(AppUser::class.java)!!
                    val name = ContactUtil.getContactName(context, friend.userPhone)
                    holder.nameView.text = name
                    holder.setThumbImage(friend.thumbImage,context)
                }


            }

        messageViewModel.getUnreadCount(friendId, userId).observe(context, androidx.lifecycle.Observer {
            holder.setMessageCount(it.size)
        })

        messageViewModel.getLastMessage(friendId).observe(context, androidx.lifecycle.Observer { list ->

            if (list.size > 0) {
                val message = list[0]
                holder.setLastMessage(message)
            } else {
                holder.setStart()
            }

        })


        holder.itemView.setOnClickListener {
            callBack.onClick(friendId)
        }


    }

    fun addItem(userList: List<Conversation>) {

        this.items = userList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    interface ConversationInterface {
        fun onClick(senderId: String)
    }

}