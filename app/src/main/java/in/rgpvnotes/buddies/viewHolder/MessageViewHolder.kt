package `in`.rgpvnotes.buddies.viewHolder

import `in`.rgpvnotes.buddies.R
import `in`.rgpvnotes.buddies.glide.GlideApp
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.message_single_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val mTextField: TextView = itemView.text_message_body
    private val timeText: TextView = itemView.text_message_receive_time
    private val msgStatus: ImageView = itemView.text_message_status
    private val msgImage: ImageView = itemView.image_message
    private val mMessageContainer: RelativeLayout = itemView.message_container
    private val messageLayout: ConstraintLayout = itemView.messageLayout

    fun setText(text: String) {
        mTextField.visibility = View.VISIBLE
        msgImage.visibility = View.GONE
        mTextField.text = text
    }

    fun setTime(date: Date?) {
        if (date != null) {
            val simpleDateFormat = SimpleDateFormat("hh:mm a")
            val time = simpleDateFormat.format(date)
            timeText.text = time
        } else {
            timeText.text = "sending"
        }
    }

    fun setMessageImage(context: Context, url: String) {

        msgImage.visibility = View.VISIBLE
        mTextField.visibility = View.GONE

        GlideApp.with(context)
            .load(url)
            .into(msgImage)

    }

    fun setMessageImageNull() {
        msgImage.setImageDrawable(null)
    }


    fun setMsgStatus(seen: Boolean) {

        if (seen) {
            msgStatus.setImageResource(R.drawable.ic_check_blue_12dp)
        } else {
            msgStatus.setImageResource(R.drawable.ic_check_black_12dp)
        }


    }

    fun setIsSender(isSender: Boolean) {

        if (isSender) {
            msgStatus.visibility = View.VISIBLE
            messageLayout.setBackgroundResource(R.drawable.in_message_bg)
            mMessageContainer.gravity = Gravity.END
        } else {

            msgStatus.visibility = View.GONE
            messageLayout.setBackgroundResource(R.drawable.out_message_bg)
            mMessageContainer.gravity = Gravity.START
        }

    }

}