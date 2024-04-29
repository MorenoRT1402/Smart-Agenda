package com.example.agendainteligente.manager

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.example.agendainteligente.R
import com.google.type.TimeOfDay
import java.util.Calendar

class TimePickerFragment(val listener: (String) -> Unit):DialogFragment(),
    TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar:Calendar = Calendar.getInstance()
        val hour:Int = calendar.get(Calendar.HOUR_OF_DAY)
        val minute:Int = calendar.get(Calendar.MINUTE)
        val dialog = TimePickerDialog(activity as Context, R.style.timePickerTheme, this, hour, minute, true)
        return dialog
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        listener("$hourOfDay:$minute")
    }
}