package com.example.agendainteligente.manager

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.agendainteligente.*
import com.example.agendainteligente.activities.DiaryTwoActivity
import com.example.agendainteligente.activities.HomeActivity
import com.example.agendainteligente.activities.access.LoginActivity
import com.example.agendainteligente.activities.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

open class MoveIntoActivitiesClass : AppCompatActivity() {



    private fun initComponents() {
//        initBottomNavigationView()
//        val spinner = findViewById<Spinner>(R.id.settings_spinner)
//        setupSpinner(this, spinner)
//        setupCalendar()
        initBottomNavigationView()
//        setupUser()
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
                    if (document.id == user?.uid){
                        levelButton.text = document.get("level").toString()
                        nicknameText.text = document.get("nickname").toString()
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }


//        levelButton.text = "1"
//        nicknameText.text = "User"
    }

    open fun setupSpinner(context: Context, spinner: Spinner) {
        val items = arrayOf("Ajustes", "Cerrar sesión")
        val adapter = SettingsSpinnerAdapter(context, android.R.layout.simple_spinner_dropdown_item, items)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = items[position]
                when (selectedItem) {
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


    private fun initBottomNavigationView() {
        var bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.homeItem

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.statsItem -> goView("HomeTwoActivity")
                R.id.diaryItem -> goView(Intent(this, DiaryTwoActivity::class.java))
                R.id.homeItem -> goView(Intent(this, HomeActivity::class.java))
                R.id.agendaItem -> goView("AgendaActivity")
                R.id.quickViewItem -> goView("HomeTwoActivity")
                else -> {true}
            }

        }
    }

    fun goView(activity: Intent): Boolean {
        startActivity(activity)
        return true
    }

    protected fun goView(activity: String): Boolean {
//        startActivity(Intent(this, Class.forName(activity)))
        startActivity(Intent(this, activity::class.java))
        return true
    }

    protected fun logout(context: Context) {
        Firebase.auth.signOut()
        startActivity(Intent(context, LoginActivity::class.java))
    }

    open fun setupCalendar() {
        TODO("Not yet implemented")
    }
}
