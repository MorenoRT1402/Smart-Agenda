package com.example.agendainteligente.entidades

import com.google.firebase.database.Exclude
import java.time.LocalDate
import java.util.*

class RegistroEmocionalLocal(
    val valorEmocional: Int, val estadosAdicionales: List<String>?,
    val variablesEmocionales: List<String>?, val fecha: LocalDate, val anotaciones: String
)

