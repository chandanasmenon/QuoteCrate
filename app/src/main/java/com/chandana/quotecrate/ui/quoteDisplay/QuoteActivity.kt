package com.chandana.quotecrate.ui.quoteDisplay

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.chandana.quotecrate.QuoteCrateApplication
import com.chandana.quotecrate.R
import com.chandana.quotecrate.databinding.ActivityQuoteBinding
import com.chandana.quotecrate.databinding.LogoutDialogBinding
import com.chandana.quotecrate.di.component.DaggerActivityComponent
import com.chandana.quotecrate.di.module.ActivityModule
import com.chandana.quotecrate.ui.base.UiState
import com.chandana.quotecrate.ui.login.LoginActivity
import com.chandana.quotecrate.utils.extensions.displayMessage
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import javax.inject.Inject

class QuoteActivity : AppCompatActivity() {
    @Inject
    lateinit var quoteViewModel: QuoteViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityQuoteBinding
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        injectDependencies()
        auth = FirebaseAuth.getInstance()
        sharedPref = getSharedPreferences(getString(R.string.userinfo_text), MODE_PRIVATE)
        val user = sharedPref.getString(getString(R.string.email), "")
        binding.materialToolbar.title = getString(R.string.user_info, user)
        binding.materialToolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.logout -> {
                    showLogoutDialog()
                    true
                }

                else -> false
            }
        }
        binding.quoteTV.visibility = View.GONE
        binding.quoteAuthorTV.visibility = View.GONE
        binding.shareQuoteButton.visibility = View.GONE
        binding.newQuoteButton.setOnClickListener {
            lifecycleScope.launch {
                setupObserver()
            }
        }
        binding.shareQuoteButton.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                val quote = binding.quoteTV.text.toString()
                val author = binding.quoteAuthorTV.text.toString()
                val shareText = "\"$quote\" - $author"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(
                Intent.createChooser(
                    intent,
                    getString(R.string.please_select_the_app_text)
                )
            )
        }
    }

    private suspend fun setupObserver() {
        quoteViewModel.getRandomQuote()
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            quoteViewModel.uiState.collect {
                when (it) {
                    is UiState.Success -> {
                        binding.quoteTV.visibility = View.VISIBLE
                        binding.quoteAuthorTV.visibility = View.VISIBLE
                        binding.shareQuoteButton.visibility = View.VISIBLE
                        binding.quoteTV.text = it.data.quote
                        binding.quoteAuthorTV.text = it.data.author
                    }

                    is UiState.Loading -> {
                        binding.quoteTV.visibility = View.GONE
                        binding.quoteAuthorTV.visibility = View.GONE
                        binding.shareQuoteButton.visibility = View.GONE
                    }

                    is UiState.Error -> {
                        binding.quoteTV.visibility = View.GONE
                        binding.quoteAuthorTV.visibility = View.GONE
                        binding.shareQuoteButton.visibility = View.GONE
                        displayMessage(it.message)
                    }
                }
            }
        }
    }

    private fun showLogoutDialog() {
        val logoutDialogBinding = LogoutDialogBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(logoutDialogBinding.root)
            .setCancelable(true)
            .create()
        logoutDialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        logoutDialogBinding.logoutButton.setOnClickListener {
            auth.signOut()
            val editor = sharedPref.edit()
            editor.clear()
            editor.apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            dialog.dismiss()
            displayMessage(getString(R.string.user_signed_out_successfully))
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun injectDependencies() {
        DaggerActivityComponent.builder()
            .applicationComponent((application as QuoteCrateApplication).applicationComponent)
            .activityModule(ActivityModule(this)).build().inject(this)

    }
}