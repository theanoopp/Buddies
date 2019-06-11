package `in`.rgpvnotes.buddies.notifications

import `in`.rgpvnotes.buddies.Buddies
import `in`.rgpvnotes.buddies.R
import `in`.rgpvnotes.buddies.activities.ChatActivity
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


object Notifications {

    private fun getCircleBitmap(bitmap: Bitmap): Bitmap {

        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)

        val color = Color.RED
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawOval(rectF, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        bitmap.recycle()

        return output
    }

    fun notifyMessage(
        context: Context,
        notificationTitle: String,
        notificationSubtitle: String,
        senderId: String
    ) {

        val notificationId = System.currentTimeMillis().toInt()

        val color = ContextCompat.getColor(context, R.color.colorPrimary)

        val chatIntent = Intent(context, ChatActivity::class.java)
        chatIntent.putExtra("sender_id", senderId)

        val activityIntent = PendingIntent.getActivity(context, 0, chatIntent, PendingIntent.FLAG_ONE_SHOT)

        val notificationBuilder = NotificationCompat.Builder(
            context,
            Buddies.friendRequestChannelId
        )
            .setSmallIcon(R.drawable.ic_chat)
            .setContentTitle(notificationTitle)
            .setContentText(notificationSubtitle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(activityIntent)
            .setAutoCancel(true)
            .setGroup(senderId)
            .setColor(color)


        val thumbRef = FirebaseStorage.getInstance().reference.child("images/profileImages/thumbs/$senderId.jpg")

        val localFile = File.createTempFile("images", "jpg")

        thumbRef.getFile(localFile).addOnCompleteListener {

            if (it.isSuccessful) {
                val filePath = localFile.path
                val bitmap = BitmapFactory.decodeFile(filePath)
                val roundedBitmap = getCircleBitmap(bitmap)
                notificationBuilder.setLargeIcon(roundedBitmap)
            }
            val notification = notificationBuilder.build()
            val manager = NotificationManagerCompat.from(context)
            manager.notify(notificationId, notification)

        }

    }

}

