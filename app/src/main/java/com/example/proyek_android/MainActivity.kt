package com.example.proyek_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        println("Test")
        println("Test repo")
        println("Test repo2")
        println("Test repo3")


        val et_email = findViewById<EditText>(R.id.et_lgemail)
        val et_password = findViewById<EditText>(R.id.et_lgpassword)
        val tv_register = findViewById<TextView>(R.id.tv_register)

        tv_register.setOnClickListener{
            startActivity(Intent(this, register::class.java))
        }
    }
}