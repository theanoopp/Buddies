package `in`.rgpvnotes.buddies.viewHolder

import `in`.rgpvnotes.buddies.R
import `in`.rgpvnotes.buddies.database.DateTypeConverter
import `in`.rgpvnotes.buddies.glide.GlideApp
import `in`.rgpvnotes.buddies.model.Message
import `in`.rgpvnotes.buddies.utils.ContactUtil
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.single_conversatio_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class ConversationsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val SECOND_MILLIS = 1000
    private val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private val HOUR_MILLIS = 60 * MINUTE_MILLIS


    val nameView = itemView.chat_single_name
    private val lastMessage = itemView.chat_single_msg
    private val messageCount = itemView.user_unread_msg
    private val thumbImageView = itemView.chat_single_image
    private val lastMessageStatus = itemView.chat_msg_status
    private val lastMessageTime = itemView.chat_single_last_time

    fun setThumbImage(thumbImage: String,context:Context) {

        val requestOptions = RequestOptions()
            .placeholder(R.drawable.icon_user_default)
            .error(R.drawable.icon_user_default)

        GlideApp.with(context)
            .setDefaultRequestOptions(requestOptions)
            .load(thumbImage)
            .into(thumbImageView)

    }


    fun setLastMessage(message: Message) {

        if (message.type == Message.MessageType.TEXT) {
            lastMessage.text = message.messageText
        } else if (message.type == Message.MessageType.IMAGE) {
            lastMessage.text = "Photo"
        }
        setLastMessageTime(message.serverTimestamp)
        setLastMessageStatus(message.seen)
        setIsSender(message.senderId != message.conversationId)
    }

    fun setStart() {

        lastMessage.text = "Start Chatting"
        lastMessageTime.text = ""
        lastMessageStatus.visibility = View.GONE
        messageCount.visibility = View.GONE

    }

    private fun setLastMessageTime(date: Date?) {

        if (date == null) {
            lastMessageTime.text = ""
        } else {

            val time = DateTypeConverter.toLong(date)!!

            val now = System.currentTimeMillis()

            val diff = now - time
            when {
                diff < 24 * HOUR_MILLIS -> {
                    val dateString = SimpleDateFormat("hh:mm a").format(Date(time))
                    lastMessageTime.text = dateString
                }
                diff < 48 * HOUR_MILLIS -> lastMessageTime.text = "yesterday"
                else -> {
                    val dateString = SimpleDateFormat("dd/MM/yy").format(Date(time))
                    lastMessageTime.text = dateString
                }
            }

        }
    }


    fun setMessageCount(count: Int) {
        if (count == 0) {
            messageCount.visibility = View.GONE
        }
        messageCount.text = "$count"
    }


    private fun setIsSender(isSender: Boolean) {

        if (isSender) {
            messageCount.visibility = View.GONE
            lastMessageStatus.visibility = View.VISIBLE
        } else {
            messageCount.visibility = View.VISIBLE
            lastMessageStatus.visibility = View.GONE
        }

    }

    private fun setLastMessageStatus(seen: Boolean) {

        if (seen) {
            lastMessageStatus.setImageResource(R.drawable.ic_check_blue_12dp)
        } else {
            lastMessageStatus.setImageResource(R.drawable.ic_check_black_12dp)
        }

    }

}