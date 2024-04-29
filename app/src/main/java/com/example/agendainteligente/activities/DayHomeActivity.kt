package com.example.agendainteligente.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.agendainteligente.R
import com.example.agendainteligente.entidades.TareaLocal
import com.example.agendainteligente.manager.*
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.*


class DayHomeActivity : MoveIntoActivitiesClass() {
    private val firebaseDatabaseManager = FirebaseFirestoreManager()

    //Inicializacion de variables

    //Inicializacion de variables
    private lateinit var eTNombre: EditText
    private lateinit var eTDescripcion: EditText
    private lateinit var datePickerInicio: EditText
    private lateinit var horaInicio: EditText
    private lateinit var datePicker: EditText
    private lateinit var horaFin: EditText
    private lateinit var alarmaDias: EditText
    private lateinit var alarmaHoras: EditText
    private lateinit var alarmaMinutos: EditText
    private lateinit var btnEnviar: Button


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_home)

/*        val selectedDate = intent.getStringExtra("fecha_seleccionada")
        val dateRegex = "CalendarDay\\{(\\d{4})-(\\d{1,2})-(\\d{1,2})\\}".toRegex()
        val matchResult = dateRegex.find(selectedDate.toString())
        if (matchResult != null) {
            val (year, month, dayOfMonth) = matchResult.destructured
            val selectedLocalDate = LocalDate.of(year.toInt(), month.toInt(), dayOfMonth.toInt())
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            findViewById<EditText>(R.id.dPDiaFin).setText(selectedLocalDate.format(formatter))
        }*/


// ...

        //Inicializar vistas
        eTNombre = findViewById(R.id.eTNombreTarea)
        eTDescripcion = findViewById(R.id.eTDescripcionTarea)
        datePickerInicio = findViewById(R.id.dPDiaInicio)
        horaInicio = findViewById(R.id.eTHoraInicioDayHome)
        datePicker = findViewById(R.id.dPDiaFin)
        horaFin = findViewById(R.id.eTHoraFinDayHome)
        alarmaDias = findViewById(R.id.eTDiasDayHome)
        alarmaHoras = findViewById(R.id.eTHorasDayHome)
        alarmaMinutos = findViewById(R.id.eTMinutosDayHome)
        btnEnviar = findViewById(R.id.btnEnviarTarea)



        //Gestion DatePickers
        val selectedDate = intent.getStringExtra("fecha_seleccionada")
        val dateRegex = "CalendarDay\\{(\\d{4})-(\\d{1,2})-(\\d{1,2})\\}".toRegex()
        val matchResult = dateRegex.find(selectedDate.toString())
        if (matchResult != null) {
            val (year, month, dayOfMonth) = matchResult.destructured
            val selectedLocalDate = LocalDate.of(year.toInt(), month.toInt() +1, dayOfMonth.toInt())
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")


            val today = LocalDate.now()

            datePicker.setText(selectedLocalDate.format(formatter))
            datePickerInicio.setText(today.format(formatter))
        }


//        findViewById<EditText>(R.id.eTHoraFinDayHome).setText("23:59")

        //SetOnClickListener
        datePickerInicio.setOnClickListener { showDatePickerDialog(findViewById(
            R.id.dPDiaInicio
        ),0) }
        datePicker.setOnClickListener { showDatePickerDialog(findViewById(
            R.id.dPDiaFin
        ), 1) }
        horaInicio.setOnClickListener { showTimePickerDialog(findViewById(
            R.id.eTHoraInicioDayHome
        )) }
        horaFin.setOnClickListener { showTimePickerDialog(findViewById(
            R.id.eTHoraFinDayHome
        )) }
        btnEnviar.setOnClickListener { registrarTarea() }

        val tarea = intent.getSerializableExtra("tarea") as? TareaLocal
        if (tarea != null) {
            rellenarCampos(tarea)
            // Acceder a los atributos de la tarea: tarea.nombre, tarea.descripcion, tarea.fechaInicio, etc.
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun rellenarCampos(tarea: TareaLocal) {
        eTNombre.setText(tarea.nombre)
        eTDescripcion.setText(tarea.descripcion)

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        datePickerInicio.setText(tarea.fechaInicio.format(formatter))
        datePicker.setText(tarea.fechaFin.format(formatter))

        alarmaDias.setText(tarea.dias.toString())
        alarmaHoras.setText(tarea.horas.toString())
        alarmaMinutos.setText(tarea.minutos.toString())
    }


    private fun showTimePickerDialog(editText: EditText) {
        val timePicker = TimePickerFragment{onTimeSelected(editText, it)}
        timePicker.show(supportFragmentManager, "Time")
    }

    private fun onTimeSelected(editText: EditText, time: String) {
        editText.setText(time)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDatePickerDialog(editText: EditText, option: Int) {
        var c = Calendar.getInstance()
        var minDateMillis = c.timeInMillis
        var maxDateMillis = Long.MAX_VALUE

        if (option == 0) { // Limites para fecha de inicio
            val maxDateText = findViewById<EditText>(R.id.dPDiaFin).text.toString()
                val formatter = DateTimeFormatterBuilder()
                    .appendPattern("d/M/yyyy")
                    .toFormatter()
            if (maxDateText.isNotEmpty()) {
                val maxLocalDate = LocalDate.parse(maxDateText, formatter)
                maxDateMillis = maxLocalDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
            }
        }

        if (option == 1) { // Limites para fecha de fin
            val minDateText = findViewById<EditText>(R.id.dPDiaInicio).text.toString()
            val formatter = DateTimeFormatterBuilder()
                .appendPattern("d/M/yyyy")
                .toFormatter()
            if (minDateText.isNotEmpty()) {
                val minLocalDate = LocalDate.parse(minDateText, formatter)
                minDateMillis = minLocalDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
            }
        }


        val datePicker = DatePickerFragment({ day, month, year -> onDateSelected(editText, day, month, year) }, minDateMillis, maxDateMillis)
        datePicker.show(supportFragmentManager, "datePicker")
    }


    @SuppressLint("SetTextI18n")
    private fun onDateSelected(editText: EditText, year:Int, month:Int, day:Int){
        editText.setText("$day/$month/$year")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun registrarTarea() {
        if (cumplirCondiciones()) {
            val user = FirebaseAuth.getInstance().currentUser
            val userID = user?.uid

            val eTNombre = findViewById<EditText>(R.id.eTNombreTarea)
            val eTDescripcion = findViewById<EditText>(R.id.eTDescripcionTarea)
            val eTDiaInicio = findViewById<EditText>(R.id.dPDiaInicio)

            val fechaInicioTexto = eTDiaInicio.text.toString()

            val formatter = DateTimeFormatterBuilder()
                .appendPattern("dd/MM/")
                .appendValueReduced(ChronoField.YEAR, 2, 4, LocalDate.now().minusYears(100))
                .toFormatter()

            val fechaInicio = LocalDate.parse(fechaInicioTexto, formatter)

            val eTHoraInicio = findViewById<EditText>(R.id.eTHoraInicioDayHome)
            val horaInicioTexto = eTHoraInicio.text.toString()
            val horaInicio = if (horaInicioTexto.isNotEmpty()) {
                LocalTime.parse(horaInicioTexto, DateTimeFormatter.ofPattern("H:m"))
            } else {
                LocalTime.MIN
            }

            val eTDiaFin = findViewById<EditText>(R.id.dPDiaFin)
            val fechaFinTexto = eTDiaFin.text.toString()
            val fechaFin = LocalDate.parse(fechaFinTexto, DateTimeFormatter.ofPattern("dd/MM/yyyy"))

            val eTHoraFin = findViewById<EditText>(R.id.eTHoraFinDayHome)
            val horaFinTexto = eTHoraFin.text.toString()
            val horaFin = if (horaFinTexto.isNotEmpty()) {
                LocalTime.parse(horaFinTexto, DateTimeFormatter.ofPattern("H:m"))
            } else {
                LocalTime.MAX
            }

            val eTDias = findViewById<EditText>(R.id.eTDiasDayHome)
            val diasTexto = eTDias.text.toString()
            val dias = diasTexto.toLongOrNull()

            val eTHoras = findViewById<EditText>(R.id.eTHorasDayHome)
            val horasTexto = eTHoras.text.toString()
            val horas = horasTexto.toLongOrNull()

            val eTMinutos = findViewById<EditText>(R.id.eTMinutosDayHome)
            val minutosTexto = eTMinutos.text.toString()
            val minutos = minutosTexto.toLongOrNull()

            val tarea = TareaLocal(
                eTNombre.text.toString(),
                eTDescripcion.text.toString(),
                fechaInicio,
                horaInicio,
                fechaFin,
                horaFin,
                dias,
                horas,
                minutos
            )
            userID?.let {
                firebaseDatabaseManager.agregarTarea(it, tarea)
            }
            Log.e("SUBIR TAREA", tarea.toString())
        }

        goView(Intent(this, HomeActivity::class.java))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun cumplirCondiciones(): Boolean {
        val eTNombre = findViewById<EditText>(R.id.eTNombreTarea)
        val dpFechaInicio = findViewById<EditText>(R.id.dPDiaInicio)
        val dpFechaFin = findViewById<EditText>(R.id.dPDiaFin)

        if (eTNombre.text.isNullOrBlank()) {
            eTNombre.error = "Este campo es obligatorio"
            return false
        }

        val fechaInicioTexto = dpFechaInicio.text.toString()
        val fechaFinTexto = dpFechaFin.text.toString()

        if (fechaInicioTexto.isBlank() || fechaFinTexto.isBlank()) {
            // Verificar si los campos de fecha están vacíos
            Toast.makeText(this, "Debe seleccionar una fecha de inicio y una fecha de fin", Toast.LENGTH_SHORT).show()
            return false
        }
        val formatBuilder = DateTimeFormatterBuilder()
            .appendPattern("dd/MM/yyyy")
            .toFormatter()

        val fechaInicio = LocalDate.parse(fechaInicioTexto, formatBuilder)
        val fechaFin = LocalDate.parse(fechaFinTexto, formatBuilder)


        if (fechaInicio.isAfter(fechaFin)) {
            // Verificar si la fecha de inicio es mayor que la fecha de fin
            Toast.makeText(this, "La fecha de inicio no puede ser mayor que la fecha de fin", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

}