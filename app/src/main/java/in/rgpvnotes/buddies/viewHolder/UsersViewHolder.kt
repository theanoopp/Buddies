package `in`.rgpvnotes.buddies.viewHolder

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.single_user_row.view.*

class UsersViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val singleName: TextView = view.chat_single_name
    val singleStatus: TextView = view.user_single_status
    val singleImage: CircleImageView = view.chat_single_image
    val onlineStatus: CircleImageView = view.user_online_status


    fun hideLayout() {

        val layout = itemView.user_single_main
        val params =
            ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.height = 0
        layout.layoutParams = params

    }

}

