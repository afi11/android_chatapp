package codeafifudin.fatakhul.chatapp.utils.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import codeafifudin.fatakhul.chatapp.MainActivity
import codeafifudin.fatakhul.chatapp.R
import codeafifudin.fatakhul.chatapp.endpoints.EndPoints
import codeafifudin.fatakhul.chatapp.models.LatestMessage
import codeafifudin.fatakhul.chatapp.utils.Auth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class FirebaseNotification : FirebaseMessagingService() {

    val CHANNEL_ID = "FirebaseMessaging"
    val auth = Auth()

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        val latestMessage = LatestMessage(
            p0.data.getValue("iduser"),
            p0.data.getValue("name"), p0.data.getValue("photo"),
            p0.data.getValue("message"), p0.data.getValue("waktu"), "0"
        )
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("detUser", latestMessage)
        if(latestMessage.iduser == auth.getIdUser(this)) {
            sendNotifications(latestMessage.name, latestMessage.message,latestMessage.photo, intent
            )
        }
    }

    fun sendNotifications(title: String, body: String, profil: String, intent: Intent){
        val pendingIntent = PendingIntent.getActivity(
            this, 100,
            intent, PendingIntent.FLAG_ONE_SHOT
        )

        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val mChannel = NotificationChannel(
                CHANNEL_ID, "FirebaseMessaging",
                NotificationManager.IMPORTANCE_HIGH
            )
            mChannel.description = "Firebase Cloud Messaging"
            mChannel.setSound(ringtoneUri, audioAttributes)
            notificationManager.createNotificationChannel(mChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(baseContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setLargeIcon(getBitmapfromUrl(EndPoints.URL_WEB + "/profil/" + profil))
            .setContentTitle(title)
            .setContentText(body)
            .setSound(ringtoneUri)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true).build()
        notificationManager.notify(100, notificationBuilder)
    }

    fun getBitmapfromUrl(imageUrl: String?): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input: InputStream = connection.getInputStream()
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            Log.e("awesome", "Error in getting notification image: " + e.localizedMessage)
            null
        }
    }

    

}