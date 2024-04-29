package com.example.agendainteligente.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.agendainteligente.R
import com.example.agendainteligente.entidades.RegistroEmocionalLocal
import com.example.agendainteligente.manager.FirebaseFirestoreManager
import com.example.agendainteligente.manager.MoveIntoActivitiesClass
import com.example.agendainteligente.manager.VariableEmocionalAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class DayDiaryActivity : MoveIntoActivitiesClass() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_diary)

        initComponents()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initComponents() {
        var modificar = false
        Log.e("DAY DIARY INICIANDO COMPONENTES", "OK")
        val user = FirebaseAuth.getInstance().currentUser?.uid
        findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener { introducirNombreVariable() }
        val fechaSeleccionada = obtenerFecha()
        if (user != null) {
            FirebaseFirestoreManager().obtenerVariablesEmocionales(user) { variablesEmocionalesTotales ->
                // Aquí puedes utilizar la lista de variablesEmocionalesTotales obtenida
                val adapterTotales = VariableEmocionalAdapter(variablesEmocionalesTotales)
                refresh(findViewById(R.id.rVVariablesTotales),adapterTotales)
            }
        }
            val variablesEmocionalesGuardadas = obtenerVariablesEmocionales(fechaSeleccionada)
                // Aquí puedes utilizar la lista de variablesEmocionalesGuardadas obtenida
                val adapterGuardadas = variablesEmocionalesGuardadas?.let { //El adaptador toma la lista de variables guardadas para ese dia
                    VariableEmocionalAdapter(it)
                }
        if (adapterGuardadas != null) {
            refresh(findViewById(R.id.rVVariablesGuardadas),adapterGuardadas)
        }
        val registroActual = FirebaseFirestoreManager().obtenerRegistroEmocional(fechaSeleccionada)
        FirebaseFirestoreManager().obtenerRegistroEmocional(fechaSeleccionada) { registroActual ->
            if (registroActual != null) {
                findViewById<SeekBar>(R.id.seekBar).progress = registroActual.valorEmocional
                findViewById<EditText>(R.id.eTAnotacionesExtra).setText(registroActual.anotaciones)
                modificar = true
            }
        }
        findViewById<Button>(R.id.btnGuardarDayDiary).setOnClickListener { subirRegistroEmocional(modificar) }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun obtenerVariablesEmocionales(fechaSeleccionada: LocalDate?) : List<String>? {
        val registroEmocionalFecha = FirebaseFirestoreManager().obtenerRegistroEmocional(fechaSeleccionada)
        if (registroEmocionalFecha != null) {
            return registroEmocionalFecha.variablesEmocionales
        }
        return null
    }

    /*
    @RequiresApi(Build.VERSION_CODES.O)
    private fun obtenerFecha(): LocalDate {
        val fechaSeleccionadaStr = intent.getStringExtra("fecha_seleccionada") // Obtener la fecha seleccionada del intent como cadena de texto

        if (fechaSeleccionadaStr != null) {
            // Extraer la parte de la cadena que contiene la fecha sin el texto adicional "CalendarDay{}"
            val fechaStr = fechaSeleccionadaStr.substringAfter("{").substringBefore("}")

            val formatter = DateTimeFormatter.ofPattern("yyyy-M-d") // Formato "yyyy-M-d"
            val localDate = LocalDate.parse(fechaStr, formatter) // Convertir la cadena de texto de fecha seleccionada al formato deseado
            val instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant() // Obtener el instante de la fecha local
            return localDate // Convertir el instante a un objeto Date
        } else {
            // Manejar el caso de fechaSeleccionadaStr nula, por ejemplo, lanzando una excepción o devolviendo un valor predeterminado
            throw IllegalStateException("Fecha seleccionada es nula")
        }
    }

     */

    @RequiresApi(Build.VERSION_CODES.O)
    private fun obtenerFecha(): LocalDate {
        val fechaSeleccionadaStr = intent.getStringExtra("fecha_seleccionada") // Obtener la fecha seleccionada del intent como cadena de texto

        if (fechaSeleccionadaStr != null) {
            val dateFormat = SimpleDateFormat("yyyy-M-d", Locale.getDefault())
            val date = dateFormat.parse(fechaSeleccionadaStr) // Analizar la cadena de texto de fecha en un objeto Date

            // Convertir el objeto Date a LocalDate
            val instant = date.toInstant()
            val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()

            return localDate // Devolver el objeto LocalDate
        } else {
            // Manejar el caso de fechaSeleccionadaStr nula, por ejemplo, lanzando una excepción o devolviendo un valor predeterminado
            throw IllegalStateException("Fecha seleccionada es nula")
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun subirRegistroEmocional(modificar: Boolean) {
        val valorEmocional = findViewById<SeekBar>(R.id.seekBar).progress
        val anotaciones = findViewById<EditText>(R.id.eTAnotacionesExtra).text.toString()
        Log.e("VALOR EMOCIONAL", valorEmocional.toString())
        val registroEmocional = RegistroEmocionalLocal(valorEmocional,null,null,obtenerFecha(),anotaciones)
        if (!modificar) {
            FirebaseFirestoreManager().currentUserID()
                ?.let { FirebaseFirestoreManager().agregarRegistroEmocional(it, registroEmocional) }
        }else{
            FirebaseFirestoreManager().currentUserID()?.let { FirebaseFirestoreManager().modificarRegistroEmocional(it, registroEmocional) }
        }
        Toast.makeText(this, "Registrado", Toast.LENGTH_SHORT).show()
        goView(Intent(this, DiaryTwoActivity::class.java))
    }


    private fun introducirNombreVariable() {
        val builder = AlertDialog.Builder(this@DayDiaryActivity)
        val view = layoutInflater.inflate(R.layout.introduccion_nombre_variable_emocional, null)

        builder.setView(view)

        val dialog = builder.create()
        dialog.show()

        val eTNombreVariable = view.findViewById<EditText>(R.id.eTNombreVariableEmocional)
        val btnGuardarNombreVariable = view.findViewById<Button>(R.id.btnGuardarNombreVariableEmocional)
        btnGuardarNombreVariable.setOnClickListener { registrarVariableEmocional(eTNombreVariable.text.toString()) }
        //goView(Intent(this, DayDiaryActivity::class.java))
    }


    private fun registrarVariableEmocional(nombre: String) {
        val user = FirebaseAuth.getInstance().currentUser?.uid
        if (user != null) {
            FirebaseFirestoreManager().agregarVariableEmocional(user, nombre)
        }
        if (user != null) {
            FirebaseFirestoreManager().obtenerVariablesEmocionales(user) { listVariables ->
                val adapterTotales = VariableEmocionalAdapter(listVariables)
                refresh(findViewById(R.id.rVVariablesTotales), adapterTotales)
            }
        }
    }


    private fun refresh(recyclerView: RecyclerView, adapter: VariableEmocionalAdapter) {
        recyclerView.adapter = adapter
    }
}