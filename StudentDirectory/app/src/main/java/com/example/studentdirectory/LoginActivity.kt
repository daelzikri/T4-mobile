package com.example.studentdirectory

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studentdirectory.utils.PrefManager

class LoginActivity : AppCompatActivity() {

    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefManager = PrefManager(this)

        // Jika sudah login dan remember me aktif, langsung ke MainActivity
        if (prefManager.isLoggedIn() && prefManager.isRememberMe()) {
            goToMain()
            return
        }

        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val cbRememberMe = findViewById<CheckBox>(R.id.cbRememberMe)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // Isi username jika remember me sebelumnya aktif
        if (prefManager.isRememberMe()) {
            etUsername.setText(prefManager.getUsername())
            cbRememberMe.isChecked = true
        }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            when {
                username.isEmpty() -> {
                    etUsername.error = "Username tidak boleh kosong"
                }
                password.isEmpty() -> {
                    etPassword.error = "Password tidak boleh kosong"
                }
                username == "admin" && password == "123456" -> {
                    prefManager.saveLoginSession(username, cbRememberMe.isChecked)
                    Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()
                    goToMain()
                }
                else -> {
                    Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
