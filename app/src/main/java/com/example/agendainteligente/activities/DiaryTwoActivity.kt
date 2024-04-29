package com.example.agendainteligente.activities

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
import com.example.agendainteligente.entidades.RegistroEmocionalLocal
import com.example.agendainteligente.manager.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class DiaryTwoActivity : MoveIntoActivitiesClass() {

    private val ROJO = "#FFFF0000"
    val NARANJA = "#FFFF8800"
    val AMARILLO = "#FFFFCC00"
    val VERDE = "#FF00FF00"
    val VERDE_OSCURO = "#FF008000"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_two)

        initComponentsLocal()
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun initComponentsLocal() {
        setupUser()
        val spinner = findViewById<Spinner>(R.id.settings_spinner)
        setupSpinner(this, spinner)
        setupCalendar()
        initBottomNavigationView()
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setupCalendar() {
        FirebaseFirestoreManager().obtenerRegistrosEmocionales { registrosEmocionales ->
            Log.d("Registros Emocionales", registrosEmocionales.toString())
            val calendarView = findViewById<MaterialCalendarView>(R.id.calendarViewDiary)

            addDecorator(registrosEmocionales)

            // OnClick Calendar
            calendarView.setOnDateChangedListener { widget, date, selected ->
                val localDate = obtenerFecha(date.date)

                val intent = Intent(this, DayDiaryActivity::class.java)
                intent.putExtra("fecha_seleccionada", localDate.toString())
                startActivity(intent)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun obtenerFecha(date: Date): LocalDate {
        val instant = date.toInstant()
        val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()

        return localDate
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun addDecorator(registros: List<RegistroEmocionalLocal>) {

        createDecorator(ROJO, -1, 20, registros)
        createDecorator(NARANJA, 20, 40, registros)
        createDecorator(AMARILLO, 40, 60, registros)
        createDecorator(VERDE, 60, 80, registros)
        createDecorator(VERDE_OSCURO, 80, 100, registros)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createDecorator(
        color: String,
        min: Int,
        max: Int,
        registros: List<RegistroEmocionalLocal>
    ) {
        val calendarView = findViewById<MaterialCalendarView>(R.id.calendarViewDiary)
        val listaCalendarDays = mutableListOf<CalendarDay>()
        for (registro in registros){
            if (registro.valorEmocional in (min + 1)..max){
                val localDate = registro.fecha

                val year = localDate.year
                val month = localDate.monthValue - 1 // Los meses en Calendar son indexados desde 0
                val day = localDate.dayOfMonth

                val calendarDay = CalendarDay.from(year, month, day)
                listaCalendarDays.add(calendarDay)
            }
        }
        val eventDecorator = EventDecorator(Color.parseColor(color), listaCalendarDays)
        calendarView.addDecorator(eventDecorator)

    }


    private fun obtainCalendarImages(): Map<CalendarDay, Int> { //Obtener imagenes de cada dia
        val mapImages = mapOf<CalendarDay, Int>()
        return mapImages
    }

    class ImageDecorator(private val drawable: Drawable) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            // Aquí puedes definir si deseas decorar un día en particular
            return true
        }

        override fun decorate(view: DayViewFacade?) {
            // Aquí puedes configurar la vista del día
            view?.setBackgroundDrawable(drawable)
        }

    }

    private fun initBottomNavigationView() {
        var bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.diaryItem

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.statsItem -> goView("HomeTwoActivity")
                R.id.diaryItem -> goView(Intent(this, DiaryTwoActivity::class.java))
                R.id.homeItem -> goView(Intent(this, HomeActivity::class.java))
                R.id.agendaItem -> goView(Intent(this, AgendaActivity::class.java))
                R.id.quickViewItem -> goView("HomeTwoActivity")
                else -> {
                    true
                }
            }

        }
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

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }
}