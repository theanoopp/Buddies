package `in`.rgpvnotes.buddies

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.firebase.FirebaseApp

class Buddies : Application() {

    companion object {
        const val friendRequestChannelId = "friend_request_channel_id"
        const val imageSendChannelId = "image_send_channel_id"
    }


    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        createNotificationChannels()

    }

    private fun createNotificationChannels() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val manager = getSystemService(NotificationManager::class.java)

            manager.deleteNotificationChannel(friendRequestChannelId)

            val friendRequestsChannel = NotificationChannel(
                friendRequestChannelId,
                "Friend Requests",
                NotificationManager.IMPORTANCE_HIGH
            )
            friendRequestsChannel.description = "Friend Requests"
            friendRequestsChannel.enableVibration(true)

            val imageSendChannel = NotificationChannel(
                imageSendChannelId,
                "Image send",
                NotificationManager.IMPORTANCE_LOW
            )
            imageSendChannel.description = "Image send"
            imageSendChannel.enableVibration(false)

            manager.createNotificationChannel(friendRequestsChannel)
            manager.createNotificationChannel(imageSendChannel)


        }


    }

}
