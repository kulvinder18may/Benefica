package com.benfica.app.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.benfica.app.R
import com.benfica.app.utils.AppUtils
import com.benfica.app.utils.SessionManager
import org.koin.android.ext.android.inject

class SplashActivity : AppCompatActivity() {
    private val sessionManager: SessionManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (sessionManager.isFirstLaunch()) {
            launch(LoginActivity::class.java)
            return
        }

        when (sessionManager.isLoggedIn()) {
            true -> launch(MainActivity::class.java)
            else -> launch(LoginActivity::class.java)
        }
    }

    private fun launch(activity: Class<*>)  {
        startActivity(Intent(this, activity))
        AppUtils.slideRight(this)
        finish()
    }
}
