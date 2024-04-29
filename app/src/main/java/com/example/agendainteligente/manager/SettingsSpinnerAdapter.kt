package com.example.agendainteligente.manager

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

class SettingsSpinnerAdapter(context: Context, resource: Int, objects: Array<String>): ArrayAdapter<String>(context, resource, objects){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return View(context)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getDropDownView(position, convertView, parent)
    }
}