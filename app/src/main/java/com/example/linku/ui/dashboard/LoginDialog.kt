package com.example.linku.ui.dashboard

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.linku.R
import kotlinx.android.synthetic.main.dialog_login.*

class LoginDialog(context: Context, _dashboardViewModel: DashboardViewModel) : Dialog(context),
    View.OnClickListener {

    val dashboardViewModel = _dashboardViewModel

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_sign_in -> {
                if (edt_account.text.isNotBlank() && edt_introduction.text.isNotBlank()) {
                    dashboardViewModel.signIn(edt_account.text.toString(), edt_introduction.text.toString())
                    dismiss()
                }

            }
            R.id.btn_sign_up -> {
                if (edt_account.text.isNotBlank() && edt_introduction.text.isNotBlank()) {
                    dashboardViewModel.signUp(edt_account.text.toString(), edt_introduction.text.toString())
                    dismiss()
                }
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