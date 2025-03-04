package com.example.heatandhumidity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerTextView: TextView
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Retrofit kurulumu
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.109.162:8085/") //192.168.109.162
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // View'ları tanımlama
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerTextView = findViewById(R.id.registerTextView)

        // Login butonuna tıklama işlemi
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            
            if (username.isNotEmpty() && password.isNotEmpty()) {
                val loginRequest = LoginRequest(username, password)
                
                apiService.login(loginRequest).enqueue(object : Callback<Boolean> {
                    override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                        if (response.isSuccessful) {
                            val loginSuccess = response.body() ?: false // Null kontrolü
                            if (loginSuccess) {
                                // Başarılı giriş
                                val intent = Intent(this@LoginActivity, WelcomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            } else {
                                // Başarısız giriş
                                Toast.makeText(this@LoginActivity, "Giriş başarısız!", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // Hata durumunda
                            Log.e("LoginError", "Response Code: ${response.code()}")
                            Toast.makeText(this@LoginActivity, "Giriş başarısız! Hata kodu: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                        // Ağ hatası
                        Log.e("LoginError", "Network Error: ${t.message}")
                        Toast.makeText(this@LoginActivity, "Bağlantı hatası: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "Kullanıcı adı ve şifre boş olamaz!", Toast.LENGTH_SHORT).show()
            }
        }

        // Register text'ine tıklama işlemi
        registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
} 