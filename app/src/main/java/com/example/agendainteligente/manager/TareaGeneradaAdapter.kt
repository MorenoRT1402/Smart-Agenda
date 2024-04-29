package com.example.agendainteligente.manager

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.agendainteligente.R
import com.example.agendainteligente.entidades.TareaAutoGeneradaLocal
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class TareaGeneradaAdapter(private val tareasGeneradas: List<TareaAutoGeneradaLocal>) : RecyclerView.Adapter<TareaGeneradaAdapter.TareaGeneradaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaGeneradaViewHolder {
        // Inflar el diseño de vista de elemento y crear un objeto TareaGeneradaViewHolder
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.tarea_generada_detalle, parent, false)
        return TareaGeneradaViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return tareasGeneradas.size
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TareaGeneradaViewHolder, position: Int) {
        val currentTareaGenerada = tareasGeneradas[position]
        holder.tVNombre.text = currentTareaGenerada.nombre
        holder.eTDescripcion.setText(currentTareaGenerada.descripcion)

        val today = LocalDate.now()
        val fechaTarea = currentTareaGenerada.fecha

        if (fechaTarea == today) {
            holder.tVFecha.text = "Hoy"
        } else {
            val diasRestantes = ChronoUnit.DAYS.between(today, fechaTarea)
            holder.tVFecha.text = "En $diasRestantes días"
        }
    }


    class TareaGeneradaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Referencias a los componentes de la vista de cada elemento
        val tVNombre: TextView = itemView.findViewById(R.id.tVNombreTareaGeneradaDetalle)
        val eTDescripcion: EditText = itemView.findViewById(R.id.eTDescripcionTareaGeneradaDetalle)
        val tVFecha: TextView = itemView.findViewById(R.id.tVDiasRestantesTareaGenerada)
    }
}
