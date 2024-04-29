package com.example.agendainteligente.manager

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.agendainteligente.entidades.RegistroEmocionalLocal
import java.time.LocalDate

data class EmocionalPattern(val tipo: String, var duracionPromedio: Int, var repeticiones: Int = 0)

@RequiresApi(Build.VERSION_CODES.O)
private fun detectarPatronesEstadosAnimo(registrosEmocionales: List<RegistroEmocionalLocal>) {
    val estadoAnimoMap = mutableMapOf<LocalDate, Int>()
    val emocionalPatternMap = mutableMapOf<String, EmocionalPattern>()

    // Paso 1: Almacenar los estados de ánimo en un Map por fecha
    for (registro in registrosEmocionales) {
        estadoAnimoMap[registro.fecha] = registro.valorEmocional
    }

    // Paso 2: Calcular el valor medio de los estados de ánimo
    val valorMedio = estadoAnimoMap.values.average()

    // Paso 3: Identificar los picos de estados de ánimo
    for ((fecha, estadoAnimo) in estadoAnimoMap) {
        val diferencia = estadoAnimo - valorMedio

        if (diferencia > 0.16 * valorMedio) {
            emocionalPatternMap.getOrPut("Pico óptimo") { EmocionalPattern("Pico óptimo", 0) }.repeticiones++
        } else if (diferencia > 0.16 * valorMedio) {
            emocionalPatternMap.getOrPut("Pico alto") { EmocionalPattern("Pico alto", 0) }.repeticiones++
        } else if (diferencia < -0.16 * valorMedio) {
            emocionalPatternMap.getOrPut("Pico bajo") { EmocionalPattern("Pico bajo", 0) }.repeticiones++
        } else if (diferencia < -0.16 * valorMedio) {
            emocionalPatternMap.getOrPut("Pico desastre") { EmocionalPattern("Pico desastre", 0) }.repeticiones++
        }
    }

    // Paso 4: Calcular la duración promedio de cada tipo de pico
    for ((tipo, pattern) in emocionalPatternMap) {
        pattern.duracionPromedio = calcularDuracionPromedioPico(estadoAnimoMap, tipo)
    }

    // Paso 5: Seleccionar el patrón más frecuente de cada tipo
    val patronesFinales = emocionalPatternMap.values
        .groupBy { it.tipo }
        .map { (_, patterns) -> patterns.maxByOrNull { it.repeticiones }!! }

    // Utilizar los patrones finales para asignar tareas en el futuro
    asignarTareasSegunPatrones(patronesFinales)
}

private fun calcularDuracionPromedioPico(estadoAnimoMap: Map<LocalDate, Int>, tipo: String): Int {
    // Implementar lógica para calcular la duración promedio de los picos según el tipo
    // Recorrer el estadoAnimoMap y calcular la diferencia entre fechas consecutivas para un tipo específico

    return 0 // Valor de ejemplo, reemplazar con el cálculo real
}

private fun asignarTareasSegunPatrones(patrones: List<EmocionalPattern>) {
    // Implementar lógica para asignar tareas según los patrones detectados
    // Utilizar los patrones y sus duraciones promedio para definir las tareas a asignar en cada periodo

    // Ejemplo de cómo se podría acceder a los patrones y sus propiedades:
    for (patron in patrones) {
        val tipo = patron.tipo
        val duracionPromedio = patron.duracionPromedio
        val repeticiones = patron.repeticiones

        // Implementar la asignación de tareas según el tipo y duración promedio
    }
}
