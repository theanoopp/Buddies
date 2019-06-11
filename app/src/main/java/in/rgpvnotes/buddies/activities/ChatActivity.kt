package `in`.rgpvnotes.buddies.activities

import `in`.rgpvnotes.buddies.R
import `in`.rgpvnotes.buddies.adapters.MessageAdapter
import `in`.rgpvnotes.buddies.glide.GlideApp
import `in`.rgpvnotes.buddies.model.AppUser
import `in`.rgpvnotes.buddies.model.Message
import `in`.rgpvnotes.buddies.services.SendImageService
import `in`.rgpvnotes.buddies.utils.FirestoreUtil
import `in`.rgpvnotes.buddies.utils.SessionManagement
import `in`.rgpvnotes.buddies.viewModel.MessageViewModel
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chat_custom_bar.*
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity(), MessageAdapter.MessageAdapterInterface {

    private val galleryRequest = 1

    private lateinit var mDatabase: FirebaseFirestore

    private lateinit var mCurrentUserId: String

    private lateinit var mFriendId: String

    private lateinit var messageViewModel: MessageViewModel

    private lateinit var messageAdapter: MessageAdapter

    private val mMessageList: ArrayList<Message> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowCustomEnabled(true)

        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val actionBarView = inflater.inflate(R.layout.chat_custom_bar,null)
        supportActionBar!!.customView = actionBarView

        val mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseFirestore.getInstance()


        chat_recyclerView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.reverseLayout = true
        chat_recyclerView.layoutManager = mLayoutManager

        mCurrentUserId = mAuth.currentUser!!.uid
        mFriendId = intent.getStringExtra("sender_id")

        mDatabase.collection("users").document(mFriendId)
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

                if (documentSnapshot!!.exists()) {
                    val friendModel = documentSnapshot.toObject(AppUser::class.java)!!
                    custom_bar_name.text = friendModel.userName

                    GlideApp.with(applicationContext)
                        .load(friendModel.thumbImage)
                        .into(custom_bar_image)

                }

            }




        button_chatbox_send.setOnClickListener {
            button_chatbox_send.isClickable = false
            sendMessage()
        }

        chat_image_button.setOnClickListener {
            sendImageMessage()
        }

        message_input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {

                if (charSequence.isEmpty()) {
                    chat_image_button.visibility = View.VISIBLE
                } else {
                    chat_image_button.visibility = View.GONE
                }
            }

            override fun afterTextChanged(editable: Editable) {


            }
        })


        messageAdapter = MessageAdapter(mMessageList, this@ChatActivity, this)

        chat_recyclerView.adapter = messageAdapter

        messageViewModel = ViewModelProviders.of(this).get(MessageViewModel::class.java)


        messageViewModel.getMessageList(mFriendId).observe(this@ChatActivity, androidx.lifecycle.Observer {

            messageAdapter.addItems(it)

        })

        messageViewModel.getUnreadCount(mFriendId, mCurrentUserId)
            .observe(this@ChatActivity, androidx.lifecycle.Observer {

                makeSeen(it)

            })

    }

    private fun sendImageMessage() {

        val galleryIntent = Intent()
        galleryIntent.action = Intent.ACTION_PICK
        galleryIntent.type = "image/*"
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), galleryRequest)


    }

    private fun sendMessage() {

        val messageText = message_input.text.toString()
        if (!TextUtils.isEmpty(messageText)) {

            message_input.setText("")
            button_chatbox_send.isClickable = true

            val user = SessionManagement.getUser(this)

            FirestoreUtil.sendTextMessage(mCurrentUserId,mFriendId,messageText,user.userPhone)

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == galleryRequest && resultCode == RESULT_OK) {

            val imageUri = data!!.data

            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(this)


        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)

            if (resultCode == RESULT_OK) {

                val uri = result.uri!!
                val intent = Intent(this, SendImageService::class.java)
                intent.putExtra("mCurrentUserId",mCurrentUserId)
                intent.putExtra("mFriendId",mFriendId)
                intent.putExtra("uri",uri.toString())
                intent.putExtra("name",custom_bar_name.text.toString())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent)
                }else{
                    startService(intent)
                }



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this@ChatActivity, error.message, Toast.LENGTH_SHORT).show()
            }


        }
    }


    override fun onDelete(msgId: String, type: String) {
        Toast.makeText(this, "Todo", Toast.LENGTH_SHORT).show()
    }


    private fun makeSeen(list: List<Message>) {

        list.forEach {
            makeMessageSeen(it)
        }

    }

    private fun makeMessageSeen(message: Message) {

        val docId = message.messageId
        val seen = message.seen

        val senderId = message.senderId

        val isReceived = mCurrentUserId != senderId

        if (isReceived && !seen) {

            val msgRef = mDatabase.collection("users").document(mCurrentUserId).collection("messages").document(docId)
            val msgRef2 = mDatabase.collection("users").document(senderId).collection("messages").document(docId)


            val batch = mDatabase.batch()

            val map = HashMap<String, Any>()
            map["seenAt"] = FieldValue.serverTimestamp()
            map["seen"] = true


            batch.update(msgRef, map)
            batch.update(msgRef2, map)

            batch.commit()

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            super.onBackPressed()
        }

        return true
    }

}
