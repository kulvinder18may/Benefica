package com.panchayat.takoli.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.panchayat.takoli.R
import com.panchayat.takoli.utils.AppUtils
import com.panchayat.takoli.utils.SessionManager
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
