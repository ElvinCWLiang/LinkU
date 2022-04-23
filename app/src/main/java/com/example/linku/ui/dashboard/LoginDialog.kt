package com.example.linku.ui.dashboard

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.example.linku.R
import kotlinx.android.synthetic.main.dialog_login.*

class LoginDialog(context: Context) : Dialog(context),
    View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_login_google -> {
                //firebase
                Log.i("evlog","btn_login_google")
            }

            R.id.btn_login_email -> {
                Log.i("evlog","btn_login_email")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_login)
        btn_login_email.setOnClickListener(this)
        btn_login_google.setOnClickListener(this)
    }


}