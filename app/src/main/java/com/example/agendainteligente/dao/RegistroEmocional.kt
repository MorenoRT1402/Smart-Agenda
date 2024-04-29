package com.example.agendainteligente.dao

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.agendainteligente.entidades.RegistroEmocionalLocal
import com.example.agendainteligente.manager.DateManager
import java.util.*

class RegistroEmocional(
    val valorEmocional: Int,
    val estadosAdicionales: List<String>?,
    val variablesEmocionales: List<String>?,
    val fecha: Date,
    val anotaciones: String
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toLocal() : RegistroEmocionalLocal {
        val fechaLocal = DateManager.convertirDateALocalDate(fecha)
        return RegistroEmocionalLocal(valorEmocional,estadosAdicionales,variablesEmocionales, fechaLocal, anotaciones)
    }

    // Constructor vacío requerido por Firebase
    constructor() : this(50, null, null, Date(), "")

}


