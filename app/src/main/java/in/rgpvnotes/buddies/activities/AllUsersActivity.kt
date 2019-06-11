package `in`.rgpvnotes.buddies.activities

import `in`.rgpvnotes.buddies.R
import `in`.rgpvnotes.buddies.adapters.UserAdapter
import `in`.rgpvnotes.buddies.model.AppUser
import `in`.rgpvnotes.buddies.utils.ContactUtil
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_all_users.*


class AllUsersActivity : AppCompatActivity() {

    private var list: ArrayList<AppUser> = ArrayList()

    private val userAdapter = UserAdapter(list, this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_users)

        Toast.makeText(this@AllUsersActivity, "Refreshing", Toast.LENGTH_SHORT).show()

        users_recyclerView.setHasFixedSize(true)
        users_recyclerView.layoutManager = LinearLayoutManager(this)
        users_recyclerView.adapter = userAdapter

        val database = FirebaseFirestore.getInstance()

        val allContacts = ContactUtil.fetchContacts(this)

        allContacts.forEach { contact ->

            database.collection("users").whereEqualTo("userPhone", contact.number).get().addOnSuccessListener {

                if (it.size() == 1) {
                    val user = it.documents[0].toObject(AppUser::class.java)
                    user?.userName = contact.name
                    userAdapter.addItem(user!!)
                }

            }.addOnFailureListener {
                Log.d("MYT", "Not found ${it.localizedMessage}")
            }


        }


    }
}
