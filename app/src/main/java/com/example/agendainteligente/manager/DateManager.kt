package com.example.agendainteligente.manager

import android.os.Build
import androidx.annotation.RequiresApi
import java.sql.Time
import java.sql.Timestamp
import java.time.*
import java.util.*

object DateManager {
    @RequiresApi(Build.VERSION_CODES.O)
    private val zoneId = ZoneId.systemDefault()

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertirDateALocalDate(date: Date): LocalDate {
        val instant = date.toInstant()
        return instant.atZone(zoneId).toLocalDate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertirLocalDateADate(localDate: LocalDate): Date {
        val instant = localDate.atStartOfDay(zoneId).toInstant()
        return Date.from(instant)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertirTimeALocalTime(time: Time): LocalTime {
        val instant = time.toInstant()
        return instant.atZone(zoneId).toLocalTime()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertirTimeALocalTime(time: Timestamp): LocalTime {
        val instant = time.toInstant()
        return instant.atZone(zoneId).toLocalTime()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertirLocalTimeATime(localTime: LocalTime): Time {
        val instant = localTime.atDate(LocalDate.now()).atZone(zoneId).toInstant()
        return Time(instant.toEpochMilli())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertirLocalTimeATimestamp(localTime: LocalTime): Timestamp {
        val instant = localTime.atDate(LocalDate.now()).atZone(zoneId).toInstant()
        return Timestamp(instant.toEpochMilli())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addTimestampToDate(fecha: Date, hora: Timestamp): Date {
        // Obtener la fecha y hora del Timestamp
        val calendarFecha = Calendar.getInstance().apply { time = fecha }
        val calendarHora = Calendar.getInstance().apply { time = hora }

        // Establecer la hora del calendario de la fecha
        calendarFecha.set(Calendar.HOUR_OF_DAY, calendarHora.get(Calendar.HOUR_OF_DAY))
        calendarFecha.set(Calendar.MINUTE, calendarHora.get(Calendar.MINUTE))
        calendarFecha.set(Calendar.SECOND, calendarHora.get(Calendar.SECOND))
        calendarFecha.set(Calendar.MILLISECOND, calendarHora.get(Calendar.MILLISECOND))

        // Obtener el Date resultante

        return calendarFecha.time
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getFecha(fecha: LocalDate): LocalDate {
        return LocalDate.of(fecha.year, fecha.month, fecha.dayOfMonth)
    }

}
