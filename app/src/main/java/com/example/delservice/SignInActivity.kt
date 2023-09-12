package com.example.delservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val username: EditText = findViewById(R.id.editTextUserName)
        val password: EditText = findViewById(R.id.editTextPass)
        val signInButton: Button = findViewById(R.id.Login_but)
        val signUpButton: Button = findViewById(R.id.registr_but)
        val DB: DBHelper = DBHelper(this)

        DB.insertDefaultData()

        signUpButton.setOnClickListener {
            val intent: Intent = Intent(this, RegistrActivity::class.java)
            startActivity(intent)
            finish()
        }

        signInButton.setOnClickListener {
            val nameOfUser: String = username.text.toString()
            val passOfUser: String = password.text.toString()

            if (TextUtils.isEmpty(nameOfUser) || TextUtils.isEmpty(passOfUser))
                Toast.makeText(this, "Все поля должны быть заполены", Toast.LENGTH_SHORT).show()
            else {
                val checkUserPass: Boolean = DB.checkPassword(nameOfUser, passOfUser)
                if (checkUserPass == true) {
                    Toast.makeText(this, "Вход выполнен успешно", Toast.LENGTH_SHORT).show()
                    val i: Intent = Intent(this, MainActivity::class.java)
                    startActivity(i)
                    finish()
                } else {
                    Toast.makeText(this, "Не удалось войти, проверьте правильность введенных данных", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}