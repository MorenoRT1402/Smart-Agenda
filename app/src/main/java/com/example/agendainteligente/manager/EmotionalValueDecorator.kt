package com.example.agendainteligente.manager

import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.agendainteligente.entidades.RegistroEmocionalLocal
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.time.LocalDate

class EmotionalValueDecorator(private val registros: List<RegistroEmocionalLocal>) :
    DayViewDecorator {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return registros.any { registro -> isSameDay(registro.fecha, day) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun decorate(view: DayViewFacade) {
        for (registro in registros) {
            val day = CalendarDay.from(registro.fecha.year, registro.fecha.monthValue - 1, registro.fecha.dayOfMonth)

            if (isSameDay(registro.fecha, day)) {
                val color = getEmotionalColor(registro.valorEmocional)
                Log.d("GET VALOR Y EMOTIONAL COLOR", "${registro.valorEmocional}: $color")
                view.addSpan(DotSpan(10f, color))
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun isSameDay(localDate: LocalDate, calendarDay: CalendarDay): Boolean {
        return localDate.year == calendarDay.year && localDate.monthValue - 1 == calendarDay.month && localDate.dayOfMonth == calendarDay.day
    }

    private fun getEmotionalColor(valorEmocional: Int): Int {
        // Asignar colores según los valores emocionales
        return when {
            valorEmocional < 20 -> Color.parseColor("#FFFF0000") // Rojo
            valorEmocional < 40 -> Color.parseColor("#FFFF8800") // Naranja
            valorEmocional < 60 -> Color.parseColor("#FFFFCC00") // Amarillo
            valorEmocional < 80 -> Color.parseColor("#FF00FF00") // Verde
            else -> Color.parseColor("#FF008000") // Verde oscuro
        }
    }
}
