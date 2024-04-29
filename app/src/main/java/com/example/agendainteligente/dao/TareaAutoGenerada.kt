package com.example.agendainteligente.dao

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.agendainteligente.entidades.TareaAutoGeneradaLocal
import com.example.agendainteligente.manager.DateManager
import java.time.Duration
import java.time.LocalTime
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
class TareaAutoGenerada(
    var nombre: String,
    var descripcion: String,
    var fecha: Date,
    private var hora: LocalTime,
    var dias: Long?,
    var horas: Long?,
    var minutos: Long?
) {
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

    fun toLocal() : TareaAutoGeneradaLocal {
        val fechaLocal = DateManager.convertirDateALocalDate(fecha)

        return TareaAutoGeneradaLocal(nombre, descripcion, fechaLocal, hora, dias, horas, minutos)
    }

    override fun toString(): String {
        return "Tarea: '$nombre' \n'$descripcion' \nFecha: $fecha a las $hora \nAlarma:$dias dias, $horas horas" +
                "y $minutos minutos antes. Alarma=$alarma)"
    }
}

