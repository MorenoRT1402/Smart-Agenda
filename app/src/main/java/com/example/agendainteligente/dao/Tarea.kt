package com.example.agendainteligente.dao

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.agendainteligente.entidades.TareaLocal
import com.example.agendainteligente.manager.DateManager
import java.sql.Timestamp
import java.time.Duration
import java.time.LocalDateTime
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
class Tarea(
    var nombre: String,
    var descripcion: String,
    var fechaInicio: Date,
    private var horaInicio: Timestamp,
    var fechaFin: Date,
    private var horaFin: Timestamp,
    var dias: Long?,
    var horas: Long?,
    var minutos: Long?

) {
    private var alarma: Duration

    init {
        addTimeToDate()
        if (dias == null)
            dias = 0
        if (horas == null)
            horas = 0
        if (minutos == null)
            minutos = 0

        val duracion = Duration.ofDays(dias!!)
            .plusHours(horas!!)
            .plusMinutes(minutos!!)
        alarma = duracion

    }

    constructor() : this("", "", Date(), Timestamp(0), Date(), Timestamp(0), 0, 0, 0)


    private fun addTimeToDate() {
        fechaInicio = DateManager.addTimestampToDate(fechaInicio, horaInicio)
        fechaFin = DateManager.addTimestampToDate(fechaFin, horaFin)
    }



    fun calcularUrgencia(): Long {
        // Calcular la diferencia de tiempo entre la fecha y hora de inicio y la fecha y hora de fin de la tarea
        val inicio = LocalDateTime.of(
            DateManager.convertirDateALocalDate(fechaInicio),
            DateManager.convertirTimeALocalTime(horaInicio)
        )
        val fin = LocalDateTime.of(
            DateManager.convertirDateALocalDate(fechaFin),
            DateManager.convertirTimeALocalTime(horaFin)
        )
        val duracionTarea = Duration.between(inicio, fin)

        // Obtener la duración total en horas y redondear hacia arriba
        val duracionTotalHoras = duracionTarea.toHours()

        return Math.ceil(duracionTotalHoras.toDouble()).toLong()
    }


    fun toLocal() : TareaLocal {
        val fechaInicioLocal = DateManager.convertirDateALocalDate(fechaInicio)
        val fechaFinLocal = DateManager.convertirDateALocalDate(fechaFin)
        val horaInicioLocal = DateManager.convertirTimeALocalTime(horaInicio)
        val horaFinLocal = DateManager.convertirTimeALocalTime(horaFin)

        return TareaLocal(nombre,descripcion,fechaInicioLocal,horaInicioLocal,fechaFinLocal,horaFinLocal,dias, horas, minutos)
    }

    override fun toString(): String {
        return "TareaGlobal: '$nombre' \n'$descripcion' \nInicio: $fechaInicio a las $horaInicio \nFin: $fechaFin a las $horaFin \nAlarma:$dias dias, $horas horas" +
                "y $minutos minutos antes. Alarma=$alarma)"
    }
}

