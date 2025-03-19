package com.chandana.quotecrate.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chandana.quotecrate.R
import com.chandana.quotecrate.databinding.ActivityMainBinding
import com.chandana.quotecrate.ui.login.LoginActivity
import com.chandana.quotecrate.utils.extensions.displayMessage
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        val sharedPref = getSharedPreferences(getString(R.string.userinfo_text), MODE_PRIVATE)
        val user = sharedPref.getString(getString(R.string.email), "")
        binding.materialToolbar.title = getString(R.string.user_info, user)
        binding.materialToolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.logout -> {
                    val editor = sharedPref.edit()
                    editor.clear()
                    editor.apply()
                    signOut()
                    displayMessage(getString(R.string.user_signed_out_successfully))
                    true
                }

                else -> false
            }
        }
    }

    private fun signOut() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

}