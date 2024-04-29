package com.example.agendainteligente.entidades

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.agendainteligente.dao.Tarea
import com.example.agendainteligente.manager.DateManager
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
class TareaLocal(
    var nombre: String,
    var descripcion: String,
    var fechaInicio: LocalDate,
    private var horaInicio: LocalTime,
    var fechaFin: LocalDate,
    private var horaFin: LocalTime,
    var dias: Long?,
    var horas: Long?,
    var minutos: Long?
) : java.io.Serializable {
    private var alarma: Duration

    init {

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


    fun calcularUrgencia(): Int {
        // Calcular la diferencia de tiempo entre la fecha y hora de inicio y la fecha y hora de fin de la tarea
        val duracionTarea = Duration.between(LocalDateTime.of(fechaInicio, horaInicio), LocalDateTime.of(fechaFin, horaFin))

        // Calcular el valor de urgencia basado en la inversa de la diferencia de tiempo
        val urgencia = -duracionTarea.toHours().toInt()

        return urgencia
    }


    fun toGlobal() : Tarea {
        val fechaInicioGlobal = DateManager.convertirLocalDateADate(fechaInicio)
        val fechaFinGlobal = DateManager.convertirLocalDateADate(fechaFin)
        val horaInicioGlobal = DateManager.convertirLocalTimeATimestamp(horaInicio)
        val horaFinGlobal = DateManager.convertirLocalTimeATimestamp(horaFin)

        return Tarea(nombre, descripcion, fechaInicioGlobal, horaInicioGlobal, fechaFinGlobal, horaFinGlobal, dias, horas, minutos)
    }

    override fun toString(): String {
        return "TareaLocal: '$nombre' \n'$descripcion' \nInicio: $fechaInicio a las $horaInicio \nFin: $fechaFin a las $horaFin \nAlarma:$dias dias, $horas horas" +
                "y $minutos minutos antes. Alarma=$alarma)"
    }


}

