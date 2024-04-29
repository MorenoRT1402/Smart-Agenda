package com.example.agendainteligente.activities

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.agendainteligente.R
import com.example.agendainteligente.entidades.Pico
import com.example.agendainteligente.entidades.RegistroEmocionalLocal
import com.example.agendainteligente.entidades.TareaLocal
import com.example.agendainteligente.manager.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.time.Duration
import java.time.LocalDate

class AgendaActivity : MoveIntoActivitiesClass() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)

        val spinner = findViewById<Spinner>(R.id.settings_spinner)
        setupSpinner(this, spinner)
        initBottomNavigationView()
        setupUser()
        initCalendar()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initCalendar() {
        val fFM = FirebaseFirestoreManager()
        fFM.obtenerTareas { listaTareas ->
            fFM.obtenerRegistrosEmocionales { registrosEmocionales ->
                val tareasOrganizadas = organizarTareasPorPatronEmocional(listaTareas, registrosEmocionales)
                Log.d("Tareas Organizadas", tareasOrganizadas.toString())
                setupCalendar(tareasOrganizadas)
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupCalendar(tareas: Map<LocalDate, List<TareaLocal>>) {
        addDecorator(tareas)
        val calendarView = findViewById<MaterialCalendarView>(R.id.calendarViewAgenda)
        // OnClick Calendar
        calendarView.setOnDateChangedListener { _, date, _ ->
            val intent = Intent(this, DayAgendaActivity::class.java)

            intent.putExtra("fecha_seleccionada", date.toString())
            intent.putExtra("tareas", HashMap(tareas)) // Pasar el mapa como Serializable
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addDecorator(tareas: Map<LocalDate, List<TareaLocal>>) {
        Log.d("Tareas auto", tareas.toString())
        val calendarView = findViewById<MaterialCalendarView>(R.id.calendarViewAgenda)
        val listaCalendarDays = mutableListOf<CalendarDay>()

        for (tarea in tareas) {
            val fecha = tarea.key
            val year = fecha.year
            val month = fecha.monthValue - 1 // Los meses en Calendar son indexados desde 0
            val day = fecha.dayOfMonth

            val calendarDay = CalendarDay.from(year, month, day)
            listaCalendarDays.add(calendarDay)
        }

        val eventDecorator = EventDecorator(Color.parseColor("#FF00FFBA"), listaCalendarDays)
        calendarView.addDecorators(eventDecorator)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun organizarTareasPorPatronEmocional(
        listaTareas: List<TareaLocal>,
        registrosEmocionales: List<RegistroEmocionalLocal>
    ): Map<LocalDate, List<TareaLocal>> {
        val patronEmocional = obtenerPatronEmocional(registrosEmocionales)
        val listaTareasPorUrgencia = ordenarPorUrgencia(listaTareas)

        val previsionEmocional =
            getLastDate(listaTareas)?.let { obtenerPrevisionEmocional(patronEmocional, it) }

        return asignarTareasACadaDia(listaTareasPorUrgencia, previsionEmocional)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun asignarTareasACadaDia(
        listaTareasPorUrgencia: List<TareaLocal>,
        previsionEmocional: Map<LocalDate, Pico>?
    ): Map<LocalDate, List<TareaLocal>> {
        val listaTareasPorDia = mutableMapOf<LocalDate, List<TareaLocal>>()

        if (previsionEmocional != null) {
 //           val fechaInicioMasBaja = listaTareasPorUrgencia.minByOrNull { it.fechaInicio }?.fechaInicio
            val fechaInicioMasBaja = LocalDate.now()
            val fechaFinMasAlta = listaTareasPorUrgencia.maxByOrNull { it.fechaFin }?.fechaFin

            if (fechaInicioMasBaja != null && fechaFinMasAlta != null) {
                val duracionTotal = Duration.between(fechaInicioMasBaja.atStartOfDay(), fechaFinMasAlta.atStartOfDay())
                val tercio = duracionTotal.dividedBy(3)

                val tercioInferior = fechaInicioMasBaja.plusDays(tercio.toDays())
                val tercioSuperior = fechaInicioMasBaja.plusDays(tercio.toDays() * 2)

                for (tarea in listaTareasPorUrgencia) {
                    val tipoPico: Pico.Tipo = when {
                        tarea.fechaFin <= tercioInferior -> Pico.Tipo.OPTIMO
                        tarea.fechaFin <= tercioSuperior -> Pico.Tipo.ALTO
                        else -> Pico.Tipo.MESETA
                    }

                    val fechaAsignacion = obtenerFechaAsignacion(previsionEmocional, tipoPico) //Busca ese tipo de pico dentro de la prevision emocional
                    val listaTareas = listaTareasPorDia.getOrDefault(fechaAsignacion, emptyList()).toMutableList()
                    listaTareas.add(tarea)
                    listaTareasPorDia[fechaAsignacion] = listaTareas
                }
                // Assigna cada tarea a su dia de fin
                for (tarea in listaTareasPorUrgencia) {
                    val fechaAsignacion = tarea.fechaFin
                    val listaTareas = listaTareasPorDia.getOrDefault(fechaAsignacion, emptyList()).toMutableList()
                    listaTareas.add(tarea)
                    listaTareasPorDia[fechaAsignacion] = listaTareas
                }
            }
        }

        return listaTareasPorDia
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun obtenerFechaAsignacion(
        previsionEmocional: Map<LocalDate, Pico>,
        tipoPico: Pico.Tipo
    ): LocalDate {
        val fechasDisponibles = previsionEmocional.filterValues { it.tipo == tipoPico }.keys
        return fechasDisponibles.minOrNull() ?: LocalDate.now()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getLastDate(listaTareas: List<TareaLocal>): LocalDate? {
        if (listaTareas.isEmpty()) {
            return null
        }

        var lastDate: LocalDate? = null

        for (tarea in listaTareas) {
            val fechaFin = tarea.fechaFin
            if (lastDate == null || fechaFin.isAfter(lastDate)) {
                lastDate = fechaFin
            }
        }

        return lastDate
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun obtenerPrevisionEmocional(patronEmocional: List<Pico>, fechaUltimaTarea: LocalDate): Map<LocalDate, Pico> {
        val previsionEmocional = mutableMapOf<LocalDate, Pico>()
        for (pico in patronEmocional) {
            val fechaReferencia = pico.ultimo.plusDays(pico.intervalo) // Fecha de próximo pico igual
            var fecha = fechaReferencia

            while (fecha.isBefore(fechaUltimaTarea)) { //Por establecer un limite agil de cálculo. Solo se calculará hasta fecha de ultima tarea
                if (pico.intervalo == 0L) {
                    break  // Salir del bucle cuando el intervalo es 0
                }
                previsionEmocional[fecha] = Pico(pico.tipo, pico.intervalo, fecha) //Se establece pico en próxima fecha previsible

                fecha = fecha.plusDays(pico.intervalo)
            }
        }
        return previsionEmocional
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun ordenarPorUrgencia(listaTareas: List<TareaLocal>): List<TareaLocal> {
        return listaTareas.sortedByDescending { it.calcularUrgencia() }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerPatronEmocional(registrosEmocionales: List<RegistroEmocionalLocal>): List<Pico> {
        // Paso 1: Almacenar los estados de ánimo en un Map por fecha
        val registroPorFecha = mutableMapOf<LocalDate, Int>()
        for (registro in registrosEmocionales) {
            registroPorFecha[registro.fecha] = registro.valorEmocional
        }
        // Paso 2: Calcular el valor medio de los estados de ánimo
        val valorMedio = registroPorFecha.values.average()

        // Paso 3: Identificar los picos de estados de ánimo
        val tiposPicoPorFecha = obtenerPicosPorFechas(registroPorFecha, valorMedio)
        val picos = obtenerListaDePicos(tiposPicoPorFecha)

        // Paso 4: Almacenar los picos por numero de veces presentes
        val contadorPicos = obtenerPicosAgrupados(picos)

        // Paso 5: Por último nos quedamos solo con los que aparezcan más veces de cada tipo
        return obtenerPatronPicos(contadorPicos)
    }
    private fun obtenerPatronPicos(contadorPicos: Map<Pair<Pico.Tipo, Long>, Pair<Int, LocalDate>>): List<Pico> {
        val picosPatron = mutableListOf<Pico>()

        val sortedEntries = contadorPicos.entries.sortedWith(compareByDescending<Map.Entry<Pair<Pico.Tipo, Long>, Pair<Int, LocalDate>>> { entry ->
            val (key, _) = entry
            val (_, intervalo) = key
            intervalo
        }.thenByDescending { entry ->
            val (_, ultimo) = entry.value
            ultimo
        })

        for (entry in sortedEntries) {
            val (key, _) = entry
            val (tipo, intervalo) = key
            val (_, ultimo) = entry.value

            picosPatron.add(Pico(tipo, intervalo, ultimo))
        }

        return picosPatron
    }



    private fun obtenerPicosAgrupados(listaPicos: List<Pico>): Map<Pair<Pico.Tipo, Long>, Pair<Int, LocalDate>> {
        val picosAgrupados = mutableMapOf<Pair<Pico.Tipo, Long>, Pair<Int, LocalDate>>()

        for (pico in listaPicos) {
            val tipoPico = pico.tipo ?: Pico.Tipo.DEFAULT
            val intervalo = pico.intervalo

            val key = Pair(tipoPico, intervalo)
            val countAndLastDate = picosAgrupados[key]

            if (countAndLastDate == null) {
                picosAgrupados[key] = Pair(1, pico.ultimo)
            } else {
                val count = countAndLastDate.first + 1
                val lastDate = pico.ultimo
                picosAgrupados[key] = Pair(count, lastDate)
            }
        }

        return picosAgrupados
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerListaDePicos(tiposPicoPorFecha: MutableMap<LocalDate, Pico.Tipo?>): List<Pico> {
        val listaPicos = mutableListOf<Pico>()

        for ((fechaActual, tipoActual) in tiposPicoPorFecha) {
            if (tipoActual != null) {
                var intervalo = 0L
                var fechaAnterior: LocalDate? = null

                for ((fecha, tipo) in tiposPicoPorFecha) {
                    if (fecha < fechaActual && tipo == tipoActual) {
                        val intervaloActual = fechaActual.toEpochDay() - fecha.toEpochDay()
                        if (fechaAnterior == null || intervaloActual < intervalo) {
                            intervalo = intervaloActual
                            fechaAnterior = fecha
                        }
                    }
                }

                val pico = Pico(tipoActual, intervalo, fechaActual)
                listaPicos.add(pico)
            }
        }

        return listaPicos
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerPicosPorFechas(
        registroPorFecha: MutableMap<LocalDate, Int>,
        valorMedio: Double
    ): MutableMap<LocalDate, Pico.Tipo?> {
        val tiposPicoPorFecha = mutableMapOf<LocalDate, Pico.Tipo?>()
        for ((fecha, valorEmocional) in registroPorFecha) {
            val diferencia = valorEmocional - valorMedio
            val tipoPico: Pico.Tipo = when { //Asignar tipo de Pico
                diferencia > valorMedio * 0.16 -> Pico.Tipo.OPTIMO
                diferencia > valorMedio * 0.32 -> Pico.Tipo.ALTO
                diferencia < -valorMedio * 0.16 -> Pico.Tipo.DESASTRE
                diferencia < -valorMedio * 0.32 -> Pico.Tipo.BAJO
                else -> Pico.Tipo.MESETA
            }
            tiposPicoPorFecha[fecha] = tipoPico
        }
        return tiposPicoPorFecha
    }

    private fun setupUser() {
        val db = Firebase.firestore
        val user = FirebaseAuth.getInstance().currentUser
        val levelButton = findViewById<Button>(R.id.btnNivelHome2)
        val nicknameText = findViewById<TextView>(R.id.tVUsuarioHome2)

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    if (document.id == user?.uid) {
                        levelButton.text = document.get("level").toString()
                        nicknameText.text = document.get("nickname").toString()
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }

    }

        private fun initBottomNavigationView() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.agendaItem

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.statsItem -> goView("HomeTwoActivity")
                R.id.diaryItem -> goView(Intent(this, DiaryTwoActivity::class.java))
                R.id.homeItem -> goView(Intent(this, HomeActivity::class.java))
                R.id.agendaItem -> goView(Intent(this, AgendaActivity::class.java))
                R.id.quickViewItem -> goView("HomeTwoActivity")
                else -> {
                    true
                }
            }
        }
    }

    override fun setupSpinner(context: Context, spinner: Spinner) {
        val items = arrayOf("Ajustes", "Cerrar sesión")
        val adapter =
            SettingsSpinnerAdapter(context, android.R.layout.simple_spinner_dropdown_item, items)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (items[position]) {
                    "Ajustes" -> {
                        // Si el elemento seleccionado ya es "Ajustes", no hacemos nada
                        if (parent?.selectedItem.toString() == "Ajustes") {
                            return
                        }
                        val intent = Intent(context, SettingsActivity::class.java)
                        context.startActivity(intent)
                    }
                    "Cerrar sesión" -> {
                        logout(context)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun obtainCalendarImages(): Map<CalendarDay, Int> {
        val mapImages = mapOf<CalendarDay, Int>()
        return mapImages
    }

}