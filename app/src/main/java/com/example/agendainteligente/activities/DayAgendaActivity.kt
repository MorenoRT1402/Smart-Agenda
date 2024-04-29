package com.example.agendainteligente.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agendainteligente.R
import com.example.agendainteligente.entidades.TareaAutoGeneradaLocal
import com.example.agendainteligente.entidades.TareaLocal
import com.example.agendainteligente.manager.FirebaseFirestoreManager
import com.example.agendainteligente.manager.TareaGeneradaAdapter
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DayAgendaActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_agenda)

        initComponents()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initComponents() {
        initRecyclerView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initRecyclerView() {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {

            val intent = intent
            val fechaSeleccionada = intent.getStringExtra("fecha_seleccionada")
            val mapTareas =
                intent.getSerializableExtra("tareas") as? HashMap<LocalDate, List<TareaLocal>>

            val dateRegex = "CalendarDay\\{(\\d{4})-(\\d{1,2})-(\\d{1,2})\\}".toRegex()
            val matchResult = dateRegex.find(fechaSeleccionada.toString())
            if (matchResult != null) {
                val (year, month, dayOfMonth) = matchResult.destructured
                val selectedLocalDate =
                    LocalDate.of(year.toInt(), month.toInt() + 1, dayOfMonth.toInt())
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

                val tareasFiltradas: MutableList<TareaAutoGeneradaLocal> = mutableListOf()

                if (mapTareas != null) {
                    for (entrada in mapTareas.entries) {
                        val fecha = entrada.key
                        val listaTareas = entrada.value

                        if (fecha == selectedLocalDate) {
                            for (tarea in listaTareas) {
                                tareasFiltradas.add(
                                    TareaAutoGeneradaLocal(
                                        tarea.nombre,
                                        tarea.descripcion,
                                        fecha
                                    )
                                )
                            }
                        }
                    }
                }

                val recyclerView = findViewById<RecyclerView>(R.id.rVtareaGeneradaDayAgenda)
                val tareaGeneradaAdapter = TareaGeneradaAdapter(tareasFiltradas)
                recyclerView.adapter = tareaGeneradaAdapter
                recyclerView.layoutManager = LinearLayoutManager(this)

            }
        }
    }
}