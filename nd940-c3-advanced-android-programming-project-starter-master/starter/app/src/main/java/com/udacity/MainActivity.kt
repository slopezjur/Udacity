package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.Constants.FILE_NAME
import com.udacity.Constants.STATUS
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private var currentUrl: String = ""
    private var currentDonwload: String = ""

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        notificationManager = ContextCompat.getSystemService(
            baseContext,
            NotificationManager::class.java
        ) as NotificationManager

        createChannel()
    }

    fun onCustomBottomClicked(view: View) {
        if (currentUrl.isNotEmpty()) {
            download()
        } else {
            Toast.makeText(
                view.context,
                getString(R.string.download_unselected_message),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            when (view.getId()) {
                R.id.glideDownloadRb ->
                    if (view.isChecked) {
                        currentUrl = GLIDE_URL
                        currentDonwload = getString(R.string.glide_download_text)
                    }
                R.id.udacityDownloadRb ->
                    if (view.isChecked) {
                        currentUrl = UDACITY_URL
                        currentDonwload = getString(R.string.udacity_download_text)
                    }
                R.id.retrofitDownloadRb ->
                    if (view.isChecked) {
                        currentUrl = RETROFIT_URL
                        currentDonwload = getString(R.string.retrofit_download_text)
                    }
            }
        }
    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) ?: -1

            context?.let { sendNotification(id.toInt()) }
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(currentUrl))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel),
                NotificationManager.IMPORTANCE_LOW
            ).apply { setShowBadge(false) }

            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.description = CHANNEL_ID
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun sendNotification(notificationId: Int) {
        val contentIntent = Intent(applicationContext, DetailActivity::class.java)
        contentIntent.putExtra(FILE_NAME, currentDonwload)

        if (notificationId == -1) {
            contentIntent.putExtra(STATUS, "Fail")
        } else {
            contentIntent.putExtra(STATUS, "Success")
        }


        pendingIntent = PendingIntent.getActivity(
            applicationContext,
            notificationId,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        action = NotificationCompat.Action(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.notification_button),
            pendingIntent
        )

        val builder = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        ).setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(applicationContext.getString(R.string.notification_title))
            .setContentText((applicationContext.getString(R.string.notification_description)))
            .setContentIntent(pendingIntent)
            .addAction(action)

        notificationManager.notify(notificationId, builder.build())
    }

    companion object {
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/master.zip"
        private const val UDACITY_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }
}
