package com.example.agendainteligente.manager

import android.content.Context
import com.example.agendainteligente.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class CustomDayViewDecorator(context: Context, private val imagesMap: Map<CalendarDay,Int>) : DayViewDecorator {



    private val context = context.applicationContext

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return imagesMap.containsKey(day)
    }

    override fun decorate(view: DayViewFacade?) {
//        val drawable = context.getDrawable(imagesMap[view?.date] ?: R.drawable.default_image)
        val drawable = context.getDrawable(R.drawable.ic_home)
        if (drawable != null) {
            view?.setBackgroundDrawable(drawable)
        }


    }

    


}
