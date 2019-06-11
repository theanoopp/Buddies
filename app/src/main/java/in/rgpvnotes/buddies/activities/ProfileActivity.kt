package `in`.rgpvnotes.buddies.activities

import `in`.rgpvnotes.buddies.R
import `in`.rgpvnotes.buddies.dialogs.MyProgressDialog
import `in`.rgpvnotes.buddies.glide.GlideApp
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileActivity : AppCompatActivity() {

    private lateinit var userId: String

    private val mDatabase: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val dialog = MyProgressDialog(this@ProfileActivity)

        dialog.setTitle("Loading")
        dialog.setMessage("Please wait while we load user's information")

        dialog.show()

        userId = intent.getStringExtra("sender_id")


        val userReference = mDatabase.collection("users").document(userId)

        userReference.get().addOnSuccessListener { friendDocument ->

            val name = friendDocument.get("userName")!!.toString()
            val status = friendDocument.get("status")!!.toString()
            val profileUri = friendDocument.get("profileUri")!!.toString()
//            val thumb_image = friendDocument.get("thumbImage")!!.toString()

            profile_name_textview.text = name
            profile_status_textview.text = status

            if (profileUri != "default") {

                GlideApp.with(this@ProfileActivity)
                    .load(profileUri)
                    .placeholder(R.drawable.icon_user_default)
                    .into(profile_imageView)

            }

        }


    }


}
