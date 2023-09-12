package com.example.delservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class RegistrActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registr)

        val etUsername: EditText = findViewById(R.id.etUserName)
        val etEmail: EditText = findViewById(R.id.etEmailAddress)
        val etPhone: EditText = findViewById(R.id.etPhone)
        val etPass: EditText = findViewById(R.id.etPass)
        val etConfirmPass: EditText = findViewById(R.id.etConfirmPass)

        val registBut: Button = findViewById(R.id.reg_button)
        val accExistsButton: Button = findViewById(R.id.acc_already_exists_but)

        //Объявление класса с функциями взаимодействия с БД
        val DB = DBHelper(this)

        registBut.setOnClickListener {
            val name_of_user: String = etUsername.text.toString()
            val email_of_user: String = etEmail.text.toString()
            val phone_of_user: String = etPhone.text.toString()
            val password_of_user: String = etPass.text.toString()
            val confirm_password: String = etConfirmPass.text.toString()

            if (TextUtils.isEmpty(name_of_user) || TextUtils.isEmpty(password_of_user) ||
                TextUtils.isEmpty(confirm_password) || TextUtils.isEmpty(email_of_user) || TextUtils.isEmpty(phone_of_user))
                Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show()
            else {
                if (password_of_user.equals(confirm_password)) {
                    val checkUser: Boolean = DB.checkUsername(name_of_user)
                    if (checkUser == false) {
                        val insert = DB.insertData(name_of_user, email_of_user, phone_of_user, password_of_user)
                        if (insert == true) {
                            Toast.makeText(this, "Регистрация прошла успешно", Toast.LENGTH_LONG)
                                .show()
                            val i = Intent(applicationContext, SignInActivity::class.java)
                            startActivity(i)
                        } else {
                            Toast.makeText(this, "Регистрация не удалась", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Такой пользователь уже существует", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this,"Пароли отличаются", Toast.LENGTH_LONG).show()
                }
            }
        }

        accExistsButton.setOnClickListener {
            val i: Intent = Intent(this, SignInActivity::class.java)
            startActivity(i)
        }
    }
}