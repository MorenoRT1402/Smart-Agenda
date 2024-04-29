package com.example.agendainteligente.manager

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.agendainteligente.R
import com.example.agendainteligente.entidades.TareaLocal
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class TareaAdapter(private val tareas: MutableList<TareaLocal>) : RecyclerView.Adapter<TareaAdapter.TareaViewHolder>() {

    var onItemClick: ((TareaLocal) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        // Inflar el diseño de vista de elemento y crear un objeto TareaGeneradaViewHolder
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.detalle_tarea, parent, false)
        return TareaViewHolder(itemView, tareas) // Pasar la lista de tareas al TareaViewHolder
    }

    override fun getItemCount(): Int {
        return tareas.size
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val currentTarea = tareas[position]
        holder.tVNombre.text = currentTarea.nombre
        holder.eTDescripcion.setText(currentTarea.descripcion)
        holder.btnEliminar.setOnClickListener {
            tareas.removeAt(position)
            notifyDataSetChanged()
        }

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(currentTarea)
        }
    }

    class TareaViewHolder(itemView: View, tareas: MutableList<TareaLocal>) : RecyclerView.ViewHolder(itemView) {
        val tVNombre: TextView = itemView.findViewById(R.id.tVTituloDetalleTarea)
        val eTDescripcion: EditText = itemView.findViewById(R.id.eTDescripcionDetalleTarea)
        val btnEliminar: ImageView = itemView.findViewById(R.id.btnEliminarTarea) // Referencia al ImageView del botón de eliminar

        }
    }
