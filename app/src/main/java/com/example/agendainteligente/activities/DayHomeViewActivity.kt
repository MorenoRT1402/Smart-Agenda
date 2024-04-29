package com.example.agendainteligente.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agendainteligente.R
import com.example.agendainteligente.entidades.TareaLocal
import com.example.agendainteligente.manager.FirebaseFirestoreManager
import com.example.agendainteligente.manager.TareaAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DayHomeViewActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_home_view)

        initComponents()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initComponents() {
        findViewById<FloatingActionButton>(R.id.fABAgregarTarea).setOnClickListener { nuevaTarea() }
        initRecyclerView()
    }

    private fun nuevaTarea() {
        val fechaSeleccionada = intent.getStringExtra("fecha_seleccionada")
        val intent = Intent(this, DayHomeActivity::class.java)
        intent.putExtra("fecha_seleccionada", fechaSeleccionada)
        startActivity(intent)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initRecyclerView() {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val fechaSeleccionadaStr = intent.getStringExtra("fecha_seleccionada") // Obtener la fecha seleccionada del intent como String
            val dateRegex = "CalendarDay\\{(\\d{4})-(\\d{1,2})-(\\d{1,2})\\}".toRegex()
            val matchResult = dateRegex.find(fechaSeleccionadaStr.toString())
            if (matchResult != null) {
                val (year, month, dayOfMonth) = matchResult.destructured
                val selectedLocalDate =
                    LocalDate.of(year.toInt(), month.toInt() +1, dayOfMonth.toInt())
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

                FirebaseFirestoreManager().obtenerTareas(user.uid) { tareas ->
                    val tareasFiltradas = tareas.filter { tarea ->
                        val fechaTarea = tarea.fechaFin // Obtener la fecha de la tarea
                        Log.d("Fechas", "$fechaTarea == $selectedLocalDate ${fechaTarea == selectedLocalDate}")
                        fechaTarea == selectedLocalDate // Filtrar las tareas por fecha
                    }

                    val recyclerView = findViewById<RecyclerView>(R.id.rVTareasDayDiaryView)
                    val tareaAdapter = TareaAdapter(tareasFiltradas as MutableList<TareaLocal>)
                    recyclerView.adapter = tareaAdapter

                    tareaAdapter.onItemClick = {
                        val intent = Intent(this, DayHomeActivity::class.java)


                        intent.putExtra("tarea", it)
                        startActivity(intent)
                    }

                    recyclerView.layoutManager = LinearLayoutManager(this)
                }
            }
        }
    }
}
