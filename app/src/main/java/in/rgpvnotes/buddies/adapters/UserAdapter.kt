package `in`.rgpvnotes.buddies.adapters

import `in`.rgpvnotes.buddies.R
import `in`.rgpvnotes.buddies.activities.ChatActivity
import `in`.rgpvnotes.buddies.glide.GlideApp
import `in`.rgpvnotes.buddies.model.AppUser
import `in`.rgpvnotes.buddies.viewHolder.UsersViewHolder
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class UserAdapter(private var items: ArrayList<AppUser>, val context: Context) :
    RecyclerView.Adapter<UsersViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        return UsersViewHolder(LayoutInflater.from(context).inflate(R.layout.single_user_row, parent, false))
    }

    fun addItem(user: AppUser) {
        items.add(user)
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {

        val user = items[position]

        val myUserId = FirebaseAuth.getInstance().uid

        if (user.userId == myUserId) {
            holder.hideLayout()
        }

        holder.singleName.text = user.userName

        holder.singleStatus.text = user.status

        GlideApp.with(context)
            .load(user.thumbImage)
            .placeholder(R.drawable.icon_user_default)
            .into(holder.singleImage)

        if (user.onlineStatus) {
            holder.onlineStatus.visibility = View.VISIBLE
        } else {
            holder.onlineStatus.visibility = View.INVISIBLE
        }

        val userId = user.userId

        holder.itemView.setOnClickListener {
            val profileIntent = Intent(context, ChatActivity::class.java)
            profileIntent.putExtra("sender_id", userId)
            context.startActivity(profileIntent)
        }


    }

    override fun getItemCount(): Int {
        return items.size
    }


}