package `in`.rgpvnotes.buddies.adapters

import `in`.rgpvnotes.buddies.R
import `in`.rgpvnotes.buddies.model.Message
import `in`.rgpvnotes.buddies.viewHolder.MessageViewHolder
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView


class MessageAdapter(var messageList : List<Message>,val context : Context,private val callBack : MessageAdapterInterface ) : RecyclerView.Adapter<MessageViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(LayoutInflater.from(context).inflate(R.layout.message_single_row, parent, false))
    }

    fun addItems(messageList: List<Message>) {
        this.messageList = messageList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {

        val message = messageList[position]

        if(message.type == Message.MessageType.IMAGE){
            holder.setMessageImage(context,message.extraUrl)
        }else {
            holder.setText(message.messageText)
            holder.setMessageImageNull()
        }

        holder.setIsSender(message.conversationId != message.senderId)
        holder.setTime(message.serverTimestamp)
        holder.setMsgStatus(message.seen)


        holder.itemView.setOnClickListener {

            val options = arrayOf<CharSequence>("Message Info", "Delete message")
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Message")

            builder.setItems(options) { _, i ->
                when (i) {

                    0 ->

                        Toast.makeText(holder.itemView.context, "Todo", Toast.LENGTH_SHORT).show()

                    1 ->

                        callBack.onDelete(message.messageId, message.type)
                }
            }

            builder.show()
        }

    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    interface MessageAdapterInterface {

        fun onDelete(msgId: String, type: String)

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }


}