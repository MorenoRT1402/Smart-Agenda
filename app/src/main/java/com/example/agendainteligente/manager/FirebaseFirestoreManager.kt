package com.example.agendainteligente.manager

import com.example.agendainteligente.dao.RegistroEmocional
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.agendainteligente.dao.Tarea
import com.example.agendainteligente.entidades.*
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class FirebaseFirestoreManager {

    private val firestore = FirebaseFirestore.getInstance()
    private val tasksCollection = firestore.collection("tareas")
    private val generatedTasksCollection = firestore.collection("tareas_generadas")
    private val emotionalRecordsCollection = firestore.collection("registros_emocionales")
    private val emotionalVariablesCollection = firestore.collection("variables_emocionales")
    private val usersCollection = firestore.collection("users")

    fun currentUserID(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun agregarTarea(usuario: String, tarea: TareaLocal) {
        val tareaGlobal = tarea.toGlobal()
        tasksCollection.document(usuario).collection("tareas").add(tareaGlobal)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerTareas(usuario: String, callback: (List<TareaLocal>) -> Unit) {
        tasksCollection.document(usuario).collection("tareas").get().addOnSuccessListener { snapshot ->
            val tareas = snapshot.documents.mapNotNull { it.toObject(Tarea::class.java)?.toLocal() }
            callback(tareas)
        }.addOnFailureListener { exception ->
            // Manejar el error
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerTareas(): List<TareaLocal> {
        val tareas: MutableList<TareaLocal> = mutableListOf()
        val userID = FirebaseAuth.getInstance().currentUser?.uid

        val tasks = tasksCollection.document(userID.toString()).collection("tareas").get()

        try {
            Tasks.await(tasks) // Esperar a que se completen todas las tareas
            tareas.addAll(tasks.result?.documents?.mapNotNull { it.toObject(Tarea::class.java)?.toLocal() } ?: emptyList())
        } catch (e: Exception) {
            Log.e("Obtener Tareas", "Error al obtener tareas (metodo antiguo)")
        }


        return tareas
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerTareas(callback: (List<TareaLocal>) -> Unit) {
        val tareas: MutableList<TareaLocal> = mutableListOf()
        val userID = FirebaseAuth.getInstance().currentUser?.uid

        val tasks = tasksCollection.document(userID.toString()).collection("tareas").get()

        tasks.addOnSuccessListener { snapshot ->
            tareas.addAll(snapshot.documents.mapNotNull { it.toObject(Tarea::class.java)?.toLocal() })
            callback(tareas)
        }.addOnFailureListener { exception ->
            // Manejar el error
            callback(emptyList())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun agregarRegistroEmocional(usuario: String, registroEmocional: RegistroEmocionalLocal) {
        val registroEmocionalGlobal = registroEmocionalToGlobal(registroEmocional)
        emotionalRecordsCollection.document(usuario).collection("registros").add(registroEmocionalGlobal).addOnSuccessListener {
            Log.e("REGISTRO EMOCIONAL REGISTRADO", "Registrado en $emotionalRecordsCollection por $usuario")
        }.addOnFailureListener { exception ->
            // Manejar el error
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun modificarRegistroEmocional(usuario: String, registroEmocional: RegistroEmocionalLocal) {
        val fechaGlobal = convertirLocalDateADate(registroEmocional.fecha)
        val registroEmocionalGlobal = registroEmocionalToGlobal(registroEmocional)

        emotionalRecordsCollection.document(usuario).collection("registros")
            .whereEqualTo("fecha", fechaGlobal)
            .get()
            .addOnSuccessListener { snapshot ->
                val documentos = snapshot.documents
                if (documentos.isNotEmpty()) {
                    // Actualizar el registro emocional existente
                    val documento = documentos.first()
                    documento.reference.set(registroEmocionalGlobal)
                        .addOnSuccessListener {
                            Log.e("MODIFICAR REGISTRO", "Registro emocional modificado")
                        }
                        .addOnFailureListener { exception ->
                            // Manejar el error
                            Log.e("MODIFICAR REGISTRO", "Error al modificar el registro emocional", exception)
                        }
                } else {
                    Log.e("MODIFICAR REGISTRO", "No se encontró ningún registro emocional con la fecha especificada")
                }
            }
            .addOnFailureListener { exception ->
                // Manejar el error
                Log.e("MODIFICAR REGISTRO", "Error al obtener registros emocionales", exception)
            }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun registroEmocionalToGlobal(registroEmocional: RegistroEmocionalLocal): RegistroEmocional {
        val fechaGlobal = convertirLocalDateADate(registroEmocional.fecha)
        return RegistroEmocional(registroEmocional.valorEmocional, registroEmocional.estadosAdicionales,
            registroEmocional.variablesEmocionales, fechaGlobal, registroEmocional.anotaciones)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerRegistrosEmocionales(usuario: String, callback: (List<RegistroEmocional>) -> Unit) {
        emotionalRecordsCollection.document(usuario).collection("registros").get().addOnSuccessListener { snapshot ->
            val registrosEmocionales = snapshot.documents.mapNotNull { it.toObject(RegistroEmocional::class.java) }
            callback(registrosEmocionales)
        }.addOnFailureListener { exception ->
            Log.e("ERROR OBTENER REGISTROS", "Error al obtener registros emocionales")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerRegistrosEmocionales(callback: (List<RegistroEmocionalLocal>) -> Unit) {
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        emotionalRecordsCollection.document(userID.toString()).collection("registros").get().addOnSuccessListener { snapshot ->
            val registrosEmocionales = snapshot.documents.mapNotNull { it.toObject(RegistroEmocional::class.java)?.toLocal() }
            callback(registrosEmocionales)
        }.addOnFailureListener { exception ->
            Log.e("ERROR OBTENER REGISTROS", "Error al obtener registros emocionales")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerRegistrosEmocionales(): List<RegistroEmocionalLocal> {
        val registros: MutableList<RegistroEmocional> = mutableListOf()
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        val registrosLocales = mutableListOf<RegistroEmocionalLocal>()

        emotionalRecordsCollection.document(userID.toString()).collection("registros").get().addOnSuccessListener { snapshot ->
            registros.addAll(snapshot.documents.mapNotNull { it.toObject(RegistroEmocional::class.java) })
            for (registro in registros) {
                registrosLocales.add(registroEmocionalToLocal(registro))
            }
        }.addOnFailureListener { exception ->
            // Manejar el error
        }

        return registrosLocales
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerRegistroEmocional(fechaSeleccionada: LocalDate?): RegistroEmocionalLocal? {
        val registrosEmocionales = obtenerRegistrosEmocionales()
        for (registro in registrosEmocionales) {
            if (registro.fecha == fechaSeleccionada) {
                return registro
            }
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerRegistroEmocional(fechaSeleccionada: LocalDate?, callback: (RegistroEmocionalLocal?) -> Unit) {
        obtenerRegistrosEmocionales { registrosEmocionales ->
            val registroEmocional = registrosEmocionales.find { it.fecha == fechaSeleccionada }
            callback(registroEmocional)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun registroEmocionalToLocal(registro: RegistroEmocional): RegistroEmocionalLocal {
        val fechaLocal = convertirDateALocalDate(registro.fecha)
        return RegistroEmocionalLocal(registro.valorEmocional, registro.estadosAdicionales,
            registro.variablesEmocionales, fechaLocal, registro.anotaciones)
    }

    fun agregarTareaAutoGenerada(usuario: String, tarea: TareaAutoGeneradaLocal) {
        generatedTasksCollection.document(usuario).collection("tareas").add(tarea)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerTareasAutoGeneradas(usuario: String, callback: (List<TareaAutoGeneradaLocal>) -> Unit) {
        generatedTasksCollection.document(usuario).collection("tareas").get().addOnSuccessListener { snapshot ->
            val tareas = snapshot.documents.mapNotNull { it.toObject(TareaAutoGeneradaLocal::class.java) }
            callback(tareas)
        }.addOnFailureListener { exception ->
            // Manejar el error
        }
    }

    fun modificarUsuario(usuario: Usuario) {
        usersCollection.document(usuario.id).set(usuario)
    }

    fun obtenerUsuario(usuarioID: String, callback: (List<Usuario>) -> Unit) {
        usersCollection.document(usuarioID).get().addOnSuccessListener { document ->
            val usuario = document.toObject(Usuario::class.java)
            if (usuario != null) {
                callback(listOf(usuario))
            } else {
                callback(emptyList())
            }
        }.addOnFailureListener { exception ->
            // Manejar el error
        }
    }

    fun agregarVariableEmocional(usuario: String, nombre: String) {
        val data = hashMapOf(
            "nombre" to nombre
        )
        emotionalVariablesCollection.document(usuario).collection("variables").add(data)
    }


    fun obtenerVariablesEmocionales(usuario: String, callback: (List<String>) -> Unit) {
        emotionalVariablesCollection.document(usuario).collection("variables").get().addOnSuccessListener { snapshot ->
            val variables = snapshot.documents.mapNotNull { it.getString("nombre") }
            callback(variables)
        }.addOnFailureListener { exception ->
            // Manejar el error
        }
    }


    fun obtenerVariablesEmocionales(usuario: String, fecha: LocalDate, callback: (List<String>) -> Unit) {
        emotionalVariablesCollection.document(usuario).collection("variables").get().addOnSuccessListener { snapshot ->
            val variables = snapshot.documents.mapNotNull { it.toObject(String::class.java) }
            callback(variables)
        }.addOnFailureListener { exception ->
            // Manejar el error
        }
    }

    fun obtenerVariablesEmocionales(usuario: String, fecha: Date, callback: (List<String>) -> Unit) {
        emotionalVariablesCollection.document(usuario).collection("variables").get().addOnSuccessListener { snapshot ->
            val variables = snapshot.documents.mapNotNull { it.toObject(String::class.java) }
            callback(variables)
        }.addOnFailureListener { exception ->
            // Manejar el error
        }
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
