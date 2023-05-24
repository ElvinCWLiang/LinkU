package com.project.linku

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.project.linku.data.remote.FireBaseRepository

class MessageService : Service() {

    private val TAG = "ev_" + javaClass.simpleName

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
        notifyMessage()
    }

    /* sync specific account conversation on Firebase */
    private fun notifyMessage() {
//        FireBaseRepository(null).notifyMessage(object : ChildEventListener {
//            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                Log.i(TAG, "onChildAdded")
//                val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//                val notificationBuilder = NotificationCompat.Builder(applicationContext, "")
//                   // .setSmallIcon(R.drawable.ic_android)
//                    .setContentTitle("FCM Message")
//                    .setContentText("message")
//                    .setAutoCancel(true)
//                    .setSound(defaultSoundUri)
//                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                notificationManager.notify(0, notificationBuilder.build())
//            }
//            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
//            override fun onChildRemoved(snapshot: DataSnapshot) {}
//            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
//            override fun onCancelled(error: DatabaseError) {}
//        })
    }
}