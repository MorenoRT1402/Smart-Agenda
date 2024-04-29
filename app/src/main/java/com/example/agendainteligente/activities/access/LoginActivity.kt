package com.example.agendainteligente.activities.access

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.agendainteligente.R
import com.example.agendainteligente.activities.HomeActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.btnAcceder).setOnClickListener{
            login()
        }
        findViewById<TextView>(R.id.tVRegistro).setOnClickListener {
            showRegister()
        }
    }

    private fun showRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    fun login(){
        val db = FirebaseAuth.getInstance()
        val email = findViewById<EditText>(R.id.eTEmailLogin)
        val pass = findViewById<EditText>(R.id.eTPassLogin)

        if (userCorrect(email, pass)){
            db.signInWithEmailAndPassword(email.toString(), pass.toString()).addOnCompleteListener {
                if (it.isSuccessful)
                    showHome()
                else
                    showError()
            }
        }
    }

    private fun showError() {
        val builder = AlertDialog.Builder(this)
//        builder.setTitle()
        builder.setMessage("El usuario no existe")
        builder.setPositiveButton("Aceptar", null)
        val dialog : AlertDialog = builder.create()
        dialog.show()
    }

    private fun userCorrect(email: EditText?, pass: EditText?): Boolean {
        if (email.toString() != "" && pass.toString() != "") {
            return true
        }else {
                if (email.toString() == "") {
                    Toast.makeText(this, "Introduce un correo válido", Toast.LENGTH_SHORT).show()
                }
                if (pass.toString() == "") {
                    Toast.makeText(this, "Introduce una contraseña válida", Toast.LENGTH_SHORT).show()
                }
            }
        return false
    }

    private fun showHome() {
        startActivity(Intent(this, HomeActivity::class.java))
    }
}