package com.example.agendainteligente.activities.access

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.agendainteligente.R
import com.example.agendainteligente.activities.HomeActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
    }


    override fun onStart() {
        super.onStart()
        FirebaseApp.initializeApp(this)
        checkSession()
    }

    private fun checkSession() {
        val db = FirebaseAuth.getInstance()
        val user = db.currentUser
        if (user != null){
            showHome()
        } else {
            goLogin()
        }
    }

    private fun goLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun showHome() {
        startActivity(Intent(this, HomeActivity::class.java))
    }
}