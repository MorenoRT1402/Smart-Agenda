package com.example.agendainteligente.entidades

import java.time.LocalDate

data class Pico(
    val tipo: Tipo?,
    var intervalo: Long, // Intervalo en días desde el último pico del mismo tipo
    var ultimo: LocalDate
) {
    enum class Tipo {
        OPTIMO,
        ALTO,
        MESETA,
        BAJO,
        DESASTRE,
        DEFAULT
    }

    override fun toString(): String {
        return "Pico '$tipo' cada $intervalo dias. El último de este tipo fue el $ultimo"
    }
}