package com.mysqldatabase.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.mysqldatabase.app.R
import com.mysqldatabase.app.ui.base.BaseActivity
import com.mysqldatabase.app.ui.fragments.LoginFragment
import com.mysqldatabase.app.ui.fragments.SignupFragment
import com.mysqldatabase.app.utils.addFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.toast

class LoginActivity : BaseActivity() {
    private var doubleBackToExit = false
    private lateinit var signUpFragment: SignupFragment
    private lateinit var loginFragment: LoginFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        signUpFragment = SignupFragment()
        loginFragment = LoginFragment()

        addFragment(loginFragment, loginHolder.id)
    }

    override fun onBackPressed() {
        if (!signUpFragment.backPressOkay() || !loginFragment.backPressOkay()) {
            toast("Please wait...")

        } else if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()

        } else {
            if (doubleBackToExit) {
                super.onBackPressed()
            } else {
                toast("Tap back again to exit")

                doubleBackToExit = true
                Handler().postDelayed({doubleBackToExit = false}, 1500)
            }
        }
    }

}
