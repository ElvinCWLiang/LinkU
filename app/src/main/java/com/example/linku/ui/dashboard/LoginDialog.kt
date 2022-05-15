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

class LoginDialog(context: Context, _dashboardViewModel: DashboardViewModel) : Dialog(context),
    View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_sign_in -> {
                //firebase
                Log.i("evlog","btn_login_google")
            }

            R.id.btn_sign_up -> {
                Log.i("evlog","btn_login_email")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_login)
        btn_sign_in.setOnClickListener(this)
        btn_sign_up.setOnClickListener(this)
    }


}