package com.example.agendainteligente.activities

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.agendainteligente.R
import com.example.agendainteligente.entidades.TareaLocal
import com.example.agendainteligente.manager.*
import com.example.agendainteligente.manager.NotificationReceiver.Companion.CHANNEL_ID
import com.example.agendainteligente.manager.NotificationReceiver.Companion.HORA_NOTIFICATION
import com.example.agendainteligente.manager.NotificationReceiver.Companion.MINUTE_NOTIFICATION
import com.example.agendainteligente.manager.NotificationReceiver.Companion.NOTIFICATION_ID
import com.example.agendainteligente.manager.NotificationReceiver.Companion.SECOND_NOTIFICATION
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.time.DateTimeException
import java.time.LocalDate
import java.util.*


class HomeActivity : MoveIntoActivitiesClass() {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_two)

        initComponentsLocal()
        createChannel()
        scheduleNotification()
    }

    private fun scheduleNotification() {
        val intent = Intent(applicationContext, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, NOTIFICATION_ID, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,
            NotificationReceiver.calcularTiempoAlarma(HORA_NOTIFICATION, MINUTE_NOTIFICATION,
                SECOND_NOTIFICATION), pendingIntent)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "MySuperChannel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Probando"
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initComponentsLocal() {
        val tareas = obtenerListaTareas()
        val spinner = findViewById<Spinner>(R.id.settings_spinner)
        setupSpinner(this, spinner)
        setupCalendar()
        initBottomNavigationView()
        setupUser()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun obtenerListaTareas(): List<TareaLocal> {
        val firebaseDatabaseManager = FirebaseFirestoreManager()

        // Obtener las tareas correctamente

        return firebaseDatabaseManager.obtenerTareas()
    }



    override fun setupSpinner(context: Context, spinner: Spinner) {
        val items = arrayOf("Ajustes", "Cerrar sesión")
        val adapter =
            SettingsSpinnerAdapter(context, android.R.layout.simple_spinner_dropdown_item, items)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = items[position]
                when (selectedItem) {
                    "Ajustes" -> {
                        // Si el elemento seleccionado ya es "Ajustes", no hacemos nada
                        if (parent?.selectedItem.toString() == "Ajustes") {
                            return
                        }
                        val intent = Intent(context, SettingsActivity::class.java)
                        context.startActivity(intent)
                    }
                    "Cerrar sesión" -> {
                        logout(context)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupUser() {
        val db = Firebase.firestore
        val user = FirebaseAuth.getInstance().currentUser
        val levelButton = findViewById<Button>(R.id.btnNivelHome2)
        val nicknameText = findViewById<TextView>(R.id.tVUsuarioHome2)

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    if (document.id == user?.uid) {
                        levelButton.text = document.get("level").toString()
                        nicknameText.text = document.get("nickname").toString()
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }


//        levelButton.text = "1"
//        nicknameText.text = "User"
    }


    fun setupCalendar(tareas: MutableList<TareaLocal>?) {
        val firebaseDatabaseManager = FirebaseFirestoreManager()
        val db = Firebase.firestore
        val user = FirebaseAuth.getInstance().currentUser
        val userID = user?.uid


        val calendarView = findViewById<MaterialCalendarView>(R.id.calendarViewHome)
//    val decorator = getDrawable(R.drawable.ic_home)?.let { ImageDecorator(it,tareas) }
        val decorator = getDrawable(R.drawable.ic_home)?.let { ImageDecorator(it, null) }
        calendarView.addDecorator(decorator)

        val customDecorator = CustomDayViewDecorator(this, obtainCalendarImages())
        val disabledDayDecorator = DisabledDayDecorator(CalendarDay.today())


        val today = CalendarDay.today()
        calendarView.state().edit()
            .setMinimumDate(today)
            .commit()


        //OnClick Calendar
        calendarView.setOnDateChangedListener { widget, date, selected ->
            val intent = Intent(this, DayHomeActivity::class.java)
            intent.putExtra("fecha_seleccionada", date.toString())
            startActivity(intent)


            calendarView.addDecorator(customDecorator)
            calendarView.addDecorator(disabledDayDecorator)
        }
    }

/*
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupCalendar(tareas: List<TareaLocal>) {
        val calendarView = findViewById<MaterialCalendarView>(R.id.calendarViewHome)
        val listaCalendarDays = mutableListOf<CalendarDay>()

        for (tarea in tareas) {
            val localDate = tarea.fechaFin

            val year = localDate.year
            val month = localDate.monthValue - 1 // Los meses en Calendar son indexados desde 0
            val day = localDate.dayOfMonth

            val calendarDay = CalendarDay.from(year, month, day)
            listaCalendarDays.add(calendarDay)
        }

        calendarView.addDecorator(EventDecorator(Color.parseColor("#FF00FFBA"), listaCalendarDays))
    }

 */


    @RequiresApi(Build.VERSION_CODES.O)
    override fun setupCalendar() {
        val fFM = FirebaseFirestoreManager()
        val db = Firebase.firestore
        val user = FirebaseAuth.getInstance().currentUser
        val userID = user?.uid

        val calendarView = findViewById<MaterialCalendarView>(R.id.calendarViewHome)


        val today = CalendarDay.today()
        calendarView.state().edit()
            .setMinimumDate(today)
            .commit()

        // Agregar los decoradores antes de configurar el listener
        fFM.obtenerTareas { tareas ->
            Log.e("Lista Tareas", tareas.toString())
            // Verificar si hay tareas asignadas
            if (tareas.isNotEmpty()) {
 //               val decorator = getDrawable(R.drawable.ic_home)?.let { ImageDecorator(it, tareas) }
                addDecorator(tareas)
                }
            }

        // OnClick Calendar
        calendarView.setOnDateChangedListener { widget, date, selected ->
            val intent = Intent(this, DayHomeViewActivity::class.java)

            intent.putExtra("fecha_seleccionada", date.toString())
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addDecorator(tareas: List<TareaLocal>) {
        val calendarView = findViewById<MaterialCalendarView>(R.id.calendarViewHome)
        val listaCalendarDays = mutableListOf<CalendarDay>()

        val customDecorator = CustomDayViewDecorator(this, obtainCalendarImages())
        val disabledDayDecorator = DisabledDayDecorator(CalendarDay.today())

        for (tarea in tareas) {
            val localDate = tarea.fechaFin

            val year = localDate.year
            val month = localDate.monthValue - 1 // Los meses en Calendar son indexados desde 0
            val day = localDate.dayOfMonth

            val calendarDay = CalendarDay.from(year, month, day)
            listaCalendarDays.add(calendarDay)
        }

        calendarView.addDecorator(EventDecorator(Color.parseColor("#FF00FFBA"), listaCalendarDays))
//      calendarView.addDecorator(customDecorator)
        calendarView.addDecorator(disabledDayDecorator)
    }





    private fun obtainCalendarImages(): Map<CalendarDay, Int> {
        val mapImages = mapOf<CalendarDay, Int>()
        return mapImages
    }

    class ImageDecorator(private val drawable: Drawable, private val tareas: List<TareaLocal>?) : DayViewDecorator {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            if (day != null && tareas != null) {
                val year = day.year
                val month = day.month
                val dayOfMonth = day.day

                try {
                    // Verificar si la fecha es válida
                    if (isValidDate(year, month, dayOfMonth)) {
                        val fechaDia = LocalDate.of(year, month, dayOfMonth)
                        for (tarea in tareas) {
                            if (tarea.fechaFin == fechaDia) {
                                Log.d("ImageDecorator", "Fecha encontrada: ${tarea.fechaFin}")
                                return true
                            }
                        }
                    } else {
                        Log.e("ImageDecorator", "Fecha inválida: $day")
                    }
                } catch (e: DateTimeException) {
                    Log.e("ImageDecorator", "Error al crear la fecha: $day", e)
                }
            }

            Log.d("Tareas", "tareas o day es null")
            return false
        }

        override fun decorate(view: DayViewFacade?) {
            view?.setBackgroundDrawable(drawable)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun isValidDate(year: Int, month: Int, day: Int): Boolean {
            return try {
                LocalDate.of(year, month, day)
                true
            } catch (e: DateTimeException) {
                false
            }
        }
    }

    private fun initBottomNavigationView() {
        var bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.homeItem

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.statsItem -> goView("HomeTwoActivity")
                R.id.diaryItem -> goView(Intent(this, DiaryTwoActivity::class.java))
                R.id.homeItem -> true
                R.id.agendaItem -> goView(Intent(this, AgendaActivity::class.java))
                R.id.quickViewItem -> goView("HomeTwoActivity")
                else -> {
                    true
                }
            }

        }
    }

    private fun setupDailyNotification() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notificationIntent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, 0)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, HORA_NOTIFICATION)
            set(Calendar.MINUTE, MINUTE_NOTIFICATION)
            set(Calendar.SECOND, SECOND_NOTIFICATION)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}