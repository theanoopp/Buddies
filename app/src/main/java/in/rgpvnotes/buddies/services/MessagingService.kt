package `in`.rgpvnotes.buddies.services

import `in`.rgpvnotes.buddies.model.AppUser
import `in`.rgpvnotes.buddies.notifications.Notifications
import `in`.rgpvnotes.buddies.utils.ContactUtil
import `in`.rgpvnotes.buddies.utils.FirestoreUtil
import `in`.rgpvnotes.buddies.utils.TokenUtils
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        if (FirebaseAuth.getInstance().currentUser != null)
            TokenUtils.addTokenToFirestore(token)

    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        FirestoreUtil.getNewMessages(application)

        val map = remoteMessage.data

        if (remoteMessage.data != null) {

            val type = map["notificationType"]

            val senderId = map["senderId"]!!


            FirebaseFirestore.getInstance().collection("users").document(senderId).get()
                .addOnSuccessListener {
                    if (it.exists()) {

                        val user = it.toObject(AppUser::class.java)!!

                        if (type.equals("MESSAGE")) {

                            val userNum = map["senderNum"]

                            val messageText = map["messageText"]

                            val userName = ContactUtil.getContactName(this, userNum!!)

                            Notifications.notifyMessage(
                                this@MessagingService,
                                userName,
                                messageText!!,
                                senderId
                            )


                        }

                    }
                }.addOnFailureListener { e ->
                    Log.d("MYT", "notification error = " + e.localizedMessage)
                }

        }

    }
}