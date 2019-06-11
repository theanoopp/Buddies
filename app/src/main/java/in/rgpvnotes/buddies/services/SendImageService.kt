package `in`.rgpvnotes.buddies.services

import `in`.rgpvnotes.buddies.Buddies
import `in`.rgpvnotes.buddies.R
import `in`.rgpvnotes.buddies.model.Message
import `in`.rgpvnotes.buddies.utils.SessionManagement
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class SendImageService  : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val mCurrentUserId = intent!!.getStringExtra("mCurrentUserId")
        val mFriendId = intent.getStringExtra("mFriendId")
        val uriString = intent.getStringExtra("uri")
        val uri = Uri.parse(uriString)
        val name = intent.getStringExtra("name")

        val user = SessionManagement.getUser(this)

        sendImageMessage(mCurrentUserId,mFriendId,uri,name,user.userPhone)

        return START_NOT_STICKY
    }

    private fun buildNotification(name: String, notificationId: Int,progress: Int) {

        val channelId = Buddies.imageSendChannelId

        val notification = NotificationCompat.Builder(this,channelId )
            .setContentTitle(getString(R.string.app_name))
            .setContentText("Sending image to $name")
            .setProgress(100, progress, false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_chat)
            .build()

        startForeground(notificationId, notification)

    }

    private fun sendImageMessage(mCurrentUserId: String, mFriendId: String, uri: Uri,name:String,userPhone: String) {

        val notificationId = System.currentTimeMillis().toInt()

        buildNotification(name,notificationId,0)

        val mDatabase = FirebaseFirestore.getInstance()

        val currentUserRef =
            mDatabase.collection("users").document(mCurrentUserId).collection("messages").document()

        val messageId = currentUserRef.id

        val friendUserRef =
            mDatabase.collection("users").document(mFriendId).collection("messages").document(messageId)

        val mStorageRef = FirebaseStorage.getInstance().reference

        val picRef = mStorageRef.child("images/image_message/$messageId.jpg")

        picRef.putFile(uri).addOnSuccessListener {

            picRef.downloadUrl.addOnCompleteListener {
                if(it.isSuccessful){
                    val picUrl = it.result.toString()
                    val senderMessage = Message(
                        messageId,
                        "",
                        false,
                        Date(0),
                        Message.MessageType.IMAGE,
                        mCurrentUserId,userPhone,
                        mFriendId,
                        mFriendId,
                        picUrl
                    )

                    val receiverMessage = Message(
                        messageId,
                        "",
                        false,
                        Date(0),
                        Message.MessageType.IMAGE,
                        mCurrentUserId,userPhone,
                        mFriendId,
                        mCurrentUserId,
                        picUrl
                    )

                    val batch = mDatabase.batch()

                    batch.set(currentUserRef, senderMessage)
                    batch.set(friendUserRef, receiverMessage)


                    batch.commit().addOnCompleteListener {
                        stopForeground(true)
                    }
                }
            }

        }.addOnFailureListener {
            stopForeground(true)
        }.addOnProgressListener { taskSnapshot ->
            val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
            buildNotification(name,notificationId,progress.toInt())
        }


    }
}
