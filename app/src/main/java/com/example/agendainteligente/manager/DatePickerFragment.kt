package com.example.agendainteligente.manager

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.example.agendainteligente.R
import java.time.Month
import java.util.Calendar

class DatePickerFragment(val listener: (day:Int, month:Int, year:Int) -> Unit, var minDate:Long, var maxDate: Long): DialogFragment(),
    DatePickerDialog.OnDateSetListener {

    override fun onDateSet(view: DatePicker?, dayOfMonth:Int, month: Int, year: Int) {
        listener(dayOfMonth,month+1,year)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c:Calendar = Calendar.getInstance()
        val day:Int = c.get(Calendar.DAY_OF_MONTH)
        val month:Int = c.get(Calendar.MONTH)
        val year:Int = c.get(Calendar.YEAR)

        val picker = DatePickerDialog(activity as Context, R.style.datePickerTheme, this, year, month, day)
        picker.datePicker.minDate = minDate
        picker.datePicker.maxDate = maxDate
        return picker
    }
}