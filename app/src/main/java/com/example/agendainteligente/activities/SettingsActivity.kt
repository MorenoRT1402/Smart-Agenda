package com.example.agendainteligente.activities

import android.os.Bundle
import android.widget.Spinner
import com.example.agendainteligente.R
import com.example.agendainteligente.manager.MoveIntoActivitiesClass

class SettingsActivity : MoveIntoActivitiesClass() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_two)

        val spinner = findViewById<Spinner>(R.id.settings_spinner)
        setupSpinner(this, spinner)

    }
}