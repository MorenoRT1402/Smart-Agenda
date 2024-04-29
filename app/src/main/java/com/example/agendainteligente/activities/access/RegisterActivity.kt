package com.example.agendainteligente.activities.access

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.agendainteligente.R
import com.example.agendainteligente.activities.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
//        window.decorView.requestFocus()

        findViewById<Button>(R.id.btnRegister).setOnClickListener { register() }
    }

    /*
    private fun register(){
        val db = FirebaseAuth.getInstance()
        val nick = findViewById<EditText>(R.id.eTNickRegister).text.toString()
        val email = findViewById<EditText>(R.id.eTEmailRegister).text.toString()
        val pass = findViewById<EditText>(R.id.eTPasswordRegister).text.toString()

        if (userCorrect(nick, email, pass)){
            println("Correcto")
            db.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(){
                if (it.isSuccessful) {
                    userConfig(nick)
                    showHome()
                }else
                    showError()
            }
        }
    }

     */

    private fun userConfig(nick: String) {
        val db = FirebaseFirestore.getInstance()
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        if (userID != null) {
            val documentReference = db.collection("users").document(userID)
            val users = hashMapOf("nickname" to nick, "level" to 1)
            documentReference.set(users)
        }
    }


    /*
    private fun showError() {
//        Log.e("Registro erroneo", )
        var builder = AlertDialog.Builder(this)
//        builder.setTitle()
        builder.setMessage("Error al crear al usuario")
        builder.setPositiveButton("Aceptar", null)
        dialog = builder.create()
        dialog!!.show()
    }

     */

    private fun userCorrect(nick: String, email: String, pass: String): Boolean {
        if (nick != "" && email != "" && pass != "") {
            return true
        }else {
            if (nick== "") {
                Toast.makeText(this, "Introduce un nombre", Toast.LENGTH_SHORT).show()
            }
            if (email == "") {
                Toast.makeText(this, "Introduce un correo válido", Toast.LENGTH_SHORT).show()
            }
            if (pass == "") {
                Toast.makeText(this, "Introduce una contraseña válida", Toast.LENGTH_SHORT).show()
            }
        }
        return false
    }

    private fun showHome() {
//        startActivity(Intent(this, Class.forName(Constants.HOME_ACTIVITY)))
        startActivity(Intent(this, HomeActivity::class.java))
    }


    private fun register() {
        val db = FirebaseAuth.getInstance()
        val nick = findViewById<EditText>(R.id.eTNickRegister).text.toString()
        val email = findViewById<EditText>(R.id.eTEmailRegister).text.toString()
        val pass = findViewById<EditText>(R.id.eTPasswordRegister).text.toString()

        if (userCorrect(nick, email, pass)) {
            println("Correcto")
            val dialog = showError()
            db.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                if (it.isSuccessful) {
                    userConfig(nick)
                    showHome()
                } else {
                    dialog.show()
                }
            }
        }
    }

    private fun showError(): AlertDialog {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Error al crear al usuario")
        builder.setPositiveButton("Aceptar", null)
        return builder.create()
    }



}