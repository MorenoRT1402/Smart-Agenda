package com.example.agendainteligente.manager

import com.example.agendainteligente.dao.RegistroEmocional
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.agendainteligente.entidades.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.LocalDate
import java.time.ZoneId
import java.util.*


class FirebaseDatabaseManager {

    // Referencia a la base de datos de Firebase
    private val database = FirebaseDatabase.getInstance()

    // Referencia a la tabla de tareas
    private val tareasRef = database.getReference("tareas")
    private val tareasGeneradasRef = database.getReference("tareas_generadas")
    private val registroEmocionalRef = database.getReference("registros_emocionales")
    private val variablesEmocionalesRef = database.getReference("variables_emocionales")
    private val usuariosRef = database.getReference("users")

    fun currentUserID(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }
    // Método para agregar una tarea a la base de datos
    fun agregarTarea(usuario: String, tarea: TareaLocal) {
        tareasRef.child(usuario).push().setValue(tarea)
    }

    // Método para obtener todas las tareas de un usuario
    fun obtenerTareas(usuario: String, callback: (List<TareaLocal>) -> Unit) {
        tareasRef.child(usuario).addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                val tareas = snapshot.children.mapNotNull { it.getValue(TareaLocal::class.java) }
                callback(tareas)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error
            }
        })
    }

    fun obtenerTareas(): List<TareaLocal> {
        val tareas: MutableList<TareaLocal> = mutableListOf()
        val userID = FirebaseAuth.getInstance().currentUser?.uid

        tareasRef.child(userID.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                tareas.addAll(snapshot.children.mapNotNull { it.getValue(TareaLocal::class.java) })
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error
            }
        })

        return tareas
    }


    /*
    // Método para agregar un registro emocional a la base de datos
    fun agregarRegistroEmocional(usuario: String, registroEmocional: com.example.agendainteligente.dao.RegistroEmocional) {
        registroEmocionalRef.child(usuario).push().setValue(registroEmocional)

        Log.e("REGISTRO EMOCIONAL REGISTRADO", "Registrado")
    }
     */

    // Método para agregar un registro emocional a la base de datos
    @RequiresApi(Build.VERSION_CODES.O)
    fun agregarRegistroEmocional(usuario: String, registroEmocional: RegistroEmocionalLocal) {
        val registroEmocionalGlobal = registroEmocionalToGlobal(registroEmocional)
        registroEmocionalRef.child(usuario).push().setValue(registroEmocionalGlobal)


        Log.e("REGISTRO EMOCIONAL REGISTRADO", "Registrado en $registroEmocionalRef por $usuario")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun registroEmocionalToGlobal(registroEmocional: RegistroEmocionalLocal): RegistroEmocional? {
        val fechaGlobal = convertirLocalDateADate(registroEmocional.fecha)
        return RegistroEmocional(registroEmocional.valorEmocional,registroEmocional.estadosAdicionales,registroEmocional.variablesEmocionales, fechaGlobal, registroEmocional.anotaciones)
    }

    // Método para obtener todas los registros emocionales de un usuario
    fun obtenerRegistrosEmocionales(usuario: String, callback: (List<RegistroEmocional>) -> Unit) {
        registroEmocionalRef.child(usuario).addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                val registrosEmocionales = snapshot.children.mapNotNull { it.getValue(
                    RegistroEmocional::class.java) }
                callback(registrosEmocionales)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error
            }
        })
    }

    fun obtenerRegistrosEmocionales() :List<RegistroEmocionalLocal>{
        val registros: MutableList<RegistroEmocional> = mutableListOf()
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        val registrosLocales = mutableListOf<RegistroEmocionalLocal>()

        registroEmocionalRef.child(userID.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                registros.addAll(snapshot.children.mapNotNull { it.getValue(RegistroEmocional::class.java) })
                for (registro in registros){
                    registrosLocales.add(registroEmocionalToLocal(registro))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error
            }
        })

        return registrosLocales
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerRegistroEmocional(fechaSeleccionada: LocalDate): RegistroEmocionalLocal? {
        val registrosEmocionales = obtenerRegistrosEmocionales()
        for (registro in registrosEmocionales) {
            if (registro.fecha == fechaSeleccionada) {
                return registro
            }
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun registroEmocionalToLocal(registro: RegistroEmocional): RegistroEmocionalLocal {
        val fechaLocal = convertirDateALocalDate(registro.fecha)
        return RegistroEmocionalLocal(registro.valorEmocional,registro.estadosAdicionales,registro.variablesEmocionales, fechaLocal, registro.anotaciones)
    }

    fun agregarTareaAutoGenerada(usuario: String, tarea: TareaAutoGeneradaLocal) {
        tareasGeneradasRef.child(usuario).push().setValue(tarea)
    }

    // Método para obtener todas las tareas de un usuario
    fun obtenerTareasAutoGeneradas(usuario: String, callback: (List<TareaAutoGeneradaLocal>) -> Unit) {
        tareasGeneradasRef.child(usuario).addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                val tareas = snapshot.children.mapNotNull { it.getValue(TareaAutoGeneradaLocal::class.java) }
                callback(tareas)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error
            }
        })
    }


    // Método para modificar un usuario de la base de datos
    fun modificarUsuario(usuario: Usuario) {
        usuariosRef.child(usuario.id).push().setValue(usuario)
    }

    // Método para obtener todas los registros emocionales de un usuario
    fun obtenerUsuario(usuarioID: String, callback: (List<Usuario>) -> Unit) {
        usuariosRef.child(usuarioID).addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                val usuario = snapshot.children.mapNotNull { it.getValue(Usuario::class.java) }
                callback(usuario)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error
            }
        })
    }

    fun agregarVariableEmocional(usuario: String, nombre: String) {
        variablesEmocionalesRef.child(usuario).push().setValue(nombre)
    }

    fun obtenerVariablesEmocionales(usuario: String, callback: (List<String>) -> Unit) {
        variablesEmocionalesRef.child(usuario).addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                val variables = snapshot.children.mapNotNull { it.getValue(String::class.java) }
                callback(variables)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error
            }
        })
    }

    fun obtenerVariablesEmocionales(usuario: String, fecha: LocalDate, callback: (List<String>) -> Unit) {
        variablesEmocionalesRef.child(usuario).addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                val variables = snapshot.children.mapNotNull { it.getValue(String::class.java) }
                callback(variables)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error
            }
        })
    }

    fun obtenerVariablesEmocionales(usuario: String, fecha: Date, callback: (List<String>) -> Unit) {
        variablesEmocionalesRef.child(usuario).addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                val variables = snapshot.children.mapNotNull { it.getValue(String::class.java) }
                callback(variables)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun convertirDateALocalDate(date: Date): LocalDate {
        val instant = date.toInstant()
        val zoneId = ZoneId.systemDefault()
        return instant.atZone(zoneId).toLocalDate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertirLocalDateADate(localDate: LocalDate): Date {
        val zonaPredeterminada = ZoneId.systemDefault()
        val instant = localDate.atStartOfDay(zonaPredeterminada).toInstant()
        return Date.from(instant)
    }



}
