package com.example.agendainteligente.entidades

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.agendainteligente.dao.TareaAutoGenerada
import com.example.agendainteligente.manager.DateManager
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
class TareaAutoGeneradaLocal(
    var nombre: String,
    var descripcion: String,
    var fecha: LocalDate,
    private var hora: LocalTime,
    var dias: Long?,
    var horas: Long?,
    var minutos: Long?
) {
    constructor(nombre: String, descripcion: String, fecha: LocalDate) : this(nombre, descripcion, fecha, LocalTime.MIN, null, null, null)

    var alarma: Duration

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

    fun toGlobal() : TareaAutoGenerada {
        val fechaGlobal = DateManager.convertirLocalDateADate(fecha)

        return TareaAutoGenerada(nombre, descripcion, fechaGlobal, hora, dias, horas, minutos)
    }

    override fun toString(): String {
        return "TareaLocal: '$nombre' \n'$descripcion' \nFecha: $fecha a las $hora \nAlarma:$dias dias, $horas horas" +
                "y $minutos minutos antes. Alarma=$alarma)"
    }
}

