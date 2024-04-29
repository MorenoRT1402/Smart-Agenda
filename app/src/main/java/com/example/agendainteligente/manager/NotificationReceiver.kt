package com.example.agendainteligente.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.agendainteligente.R
import com.example.agendainteligente.activities.DayDiaryActivity
import java.util.*

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        fun calcularTiempoAlarma(horaNotification: Int, minuteNotification: Int, secondNotification: Int): Long {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.set(Calendar.HOUR_OF_DAY, horaNotification)
            calendar.set(Calendar.MINUTE, minuteNotification)
            calendar.set(Calendar.SECOND, secondNotification)
            calendar.set(Calendar.MILLISECOND, 0) // Establecer los milisegundos en 0 si no se requieren

            val currentTime = System.currentTimeMillis()
            val notificationTime = calendar.timeInMillis

            // Calcular la diferencia de tiempo en milisegundos
            var tiempoAlarma = notificationTime - currentTime
            if (tiempoAlarma < 0) {
                // Si el tiempo de la alarma es anterior al tiempo actual, sumar 24 horas (un día) en milisegundos
                tiempoAlarma += 24 * 60 * 60 * 1000
            }

            return tiempoAlarma
        }

        const val HORA_NOTIFICATION = 8
        const val MINUTE_NOTIFICATION = 37
        const val SECOND_NOTIFICATION = 30
        const val CHANNEL_ID = "channelID"
        const val CHANNEL_NAME = "channelName"
        const val TITULO_NOTIFICATION = "Agenda Inteligente"
        const val TEXT_NOTIFICATION = "¿Cómo te encuentras hoy?"
        const val PERMISSION_REQUEST_CODE = 100
        const val NOTIFICATION_ID = 0
        const val NOTIFICATION_SMALL_ICON = R.drawable.ic_diary
    }

    override fun onReceive(context: Context, intent: Intent) {
        createSimpleNotification(context)

        val today = Calendar.getInstance()
        val year = today.get(Calendar.YEAR)
        val month = today.get(Calendar.MONTH) + 1 // Los meses en Calendar son indexados desde 0
        val day = today.get(Calendar.DAY_OF_MONTH)

        val activityIntent = Intent(context, DayDiaryActivity::class.java)
        activityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        activityIntent.putExtra("fecha_seleccionada", "$year-$month-$day")
    }


    private fun createSimpleNotification(context: Context) {
        val intent = Intent(context, DayDiaryActivity::class.java)
        intent.putExtra("fecha_seleccionada", obtenerFechaSeleccionada())

        val pendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(NOTIFICATION_SMALL_ICON)
            .setContentTitle(TITULO_NOTIFICATION)
            .setContentText(TEXT_NOTIFICATION)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun obtenerFechaSeleccionada(): String {
        val today = Calendar.getInstance()
        val year = today.get(Calendar.YEAR)
        val month = today.get(Calendar.MONTH) + 1 // Los meses en Calendar son indexados desde 0
        val day = today.get(Calendar.DAY_OF_MONTH)

        return "$year-$month-$day"
    }


    /*
    fun createSimpleNotification(context: Context) {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, MoveIntoActivitiesClass.CHANNEL_ID).also {
            it.setContentTitle(MoveIntoActivitiesClass.TITULO_NOTIFICATION)
            it.setContentText(MoveIntoActivitiesClass.TEXT_NOTIFICATION)
            it.setSmallIcon(R.drawable.ic_diary)
            it.priority = NotificationCompat.PRIORITY_HIGH
        }

        with(NotificationManagerCompat.from(context)){
            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(MoveIntoActivitiesClass.NOTIFICATION_ID, notification.build())
        }


    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(MoveIntoActivitiesClass.CHANNEL_ID, MoveIntoActivitiesClass.CHANNEL_NAME, importance).apply {
                lightColor = Color.MAGENTA
                enableLights(true)
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
    }
    }

     */
}
