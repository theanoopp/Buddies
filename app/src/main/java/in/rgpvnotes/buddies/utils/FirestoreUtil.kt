package `in`.rgpvnotes.buddies.utils


import `in`.rgpvnotes.buddies.model.AppUser
import `in`.rgpvnotes.buddies.model.Conversation
import `in`.rgpvnotes.buddies.model.Message
import `in`.rgpvnotes.buddies.viewModel.ConversationViewModel
import `in`.rgpvnotes.buddies.viewModel.MessageViewModel
import android.app.Application
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

object FirestoreUtil {

    private var isCalled = false

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val mUser = mAuth.currentUser

    private val mDatabase: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = mDatabase.document(
            "users/${mUser?.uid ?: throw NullPointerException("UID is null.")}"
        )

    fun getCurrentUser(onComplete: (AppUser) -> Unit) {

        currentUserDocRef.get().addOnSuccessListener {
            onComplete(it.toObject(AppUser::class.java)!!)
        }
    }

    fun getNewMessages(application: Application) {

        if (!isCalled) {

            isCalled = true

            val messageViewModel = MessageViewModel(application)
            val conversationViewModel = ConversationViewModel(application)

            val lastMessage = SessionManagement.getLastMessage(application)

            val db = FirebaseFirestore.getInstance()

            val user = FirebaseAuth.getInstance().currentUser


            val query = if (lastMessage != null && lastMessage.serverTimestamp != null) {
                db.collection("users").document(user!!.uid).collection("messages")
                    .orderBy("serverTimestamp").startAfter(lastMessage.serverTimestamp)
            } else {
                db.collection("users").document(user!!.uid).collection("messages")
                    .orderBy("serverTimestamp")
            }

            var beanId = ""

            query.addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                for (dc in querySnapshot!!.documentChanges) {

                    val docId = dc.document.id

                    val bean: Message

                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {

                            bean = dc.document.toObject(Message::class.java)

                            conversationViewModel.addConversation(
                                Conversation(
                                    bean.conversationId,
                                    bean.serverTimestamp
                                )
                            )
                            messageViewModel.addMessage(bean)

                            beanId = bean.messageId

                            if (bean.serverTimestamp != null) {
                                SessionManagement.saveLastMessage(application, bean)
                            }

                        }


                        DocumentChange.Type.MODIFIED -> {
                            bean = dc.document.toObject(Message::class.java)

                            conversationViewModel.addConversation(
                                Conversation(
                                    bean.conversationId,
                                    bean.serverTimestamp
                                )
                            )
                            messageViewModel.addMessage(bean)


                            if (bean.messageId == beanId) {
                                SessionManagement.saveLastMessage(application, bean)
                            }

                        }

                        DocumentChange.Type.REMOVED -> {
                            Log.d("MYT", "DELETED $docId")
                            messageViewModel.deleteMessageById(docId)
                        }
                    }
                }


            }

        }
    }


    fun sendTextMessage(
        mCurrentUserId: String,
        mFriendId: String,
        messageText: String,
        userPhone: String
    ) {

        val currentUserRef =
            mDatabase.collection("users").document(mCurrentUserId).collection("messages").document()

        val messageId = currentUserRef.id

        val friendUserRef =
            mDatabase.collection("users").document(mFriendId).collection("messages").document(messageId)

        val senderMessage = Message(
            messageId,
            messageText,
            false,
            Date(0),
            Message.MessageType.TEXT,
            mCurrentUserId,userPhone,
            mFriendId,
            mFriendId,
            ""
        )

        val receiverMessage = Message(
            messageId,
            messageText,
            false,
            Date(0),
            Message.MessageType.TEXT,
            mCurrentUserId,userPhone,
            mFriendId,
            mCurrentUserId,
            ""
        )

        val batch = mDatabase.batch()

        batch.set(currentUserRef, senderMessage)
        batch.set(friendUserRef, receiverMessage)

        batch.commit()
    }


    //region FCM
    fun getFCMRegistrationTokens(onComplete: (tokens: MutableList<String>) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener {
            val user = it.toObject(AppUser::class.java)!!
            onComplete(user.registrationTokens)
        }
    }

    fun setFCMRegistrationTokens(registrationTokens: MutableList<String>) {
        currentUserDocRef.update(mapOf("registrationTokens" to registrationTokens))
    }

}