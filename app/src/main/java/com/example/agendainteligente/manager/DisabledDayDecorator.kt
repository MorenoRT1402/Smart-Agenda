package com.example.agendainteligente.manager

import android.graphics.Color
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class DisabledDayDecorator(private val today: CalendarDay) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean {
        // Devuelve verdadero si el día es anterior a la fecha actual
        return day.isBefore(today)
    }

    override fun decorate(view: DayViewFacade) {
        // Cambia el color del texto del día deshabilitado a gris
        view.addSpan(Color.GRAY)
    }
}
