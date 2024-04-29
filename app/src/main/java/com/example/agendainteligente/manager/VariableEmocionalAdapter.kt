package com.example.agendainteligente.manager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.agendainteligente.R
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class VariableEmocionalAdapter(private val listVariablesEmocionales: List<String>) : RecyclerView.Adapter<VariableEmocionalAdapter.VariableEmocionalViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VariableEmocionalViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.variable_emocional_detalle, parent, false)
        return VariableEmocionalAdapter.VariableEmocionalViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VariableEmocionalViewHolder, position: Int) {
        val currentVariableEmocional = listVariablesEmocionales[position]
        holder.btnVariable.text = currentVariableEmocional
        holder.btnVariable.setOnClickListener { cambiarDeLista() }
    }

    override fun getItemCount(): Int {
        return listVariablesEmocionales.size
    }

    class VariableEmocionalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Referencias a los componentes de la vista de cada elemento
        val btnVariable : Button = itemView.findViewById(R.id.btnVariableEmocional)
    }

    private fun cambiarDeLista() {

    }

}