package com.example.linku.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;

import com.example.linku.MainActivity;
import com.google.firebase.database.ChildEventListener;

public class temp extends Dialog implements DialogInterface.OnClickListener {
    public temp(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case 1: {

            }
        }
    }



}
