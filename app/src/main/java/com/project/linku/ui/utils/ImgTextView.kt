package com.project.linku.ui.utils

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.project.linku.R

class ImgTextView: ConstraintLayout{
    var imgsrc = ResourcesCompat.getDrawable(resources, android.R.drawable.alert_dark_frame, null)
    var txtstring = "Title"
    val TAG = "ev_" + javaClass.simpleName
    lateinit var button : Button

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, def: Int) : super(context, attrs, def) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImgTextView)
        getvalues(typedArray)
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImgTextView)
        getvalues(typedArray)
        initView()
    }

    fun getvalues(typedArray: TypedArray) {
        imgsrc = typedArray.getDrawable(R.styleable.ImgTextView_imgSrc)
        txtstring = typedArray.getString(R.styleable.ImgTextView_calltype) ?: "Voice Call"
        typedArray.recycle()
    }

    private fun initView() {
        val c: View = inflate(context, R.layout.adapter_conversation_call, this)

        val img = c.findViewById<ImageView>(R.id.img_caller)
        button = c.findViewById<Button>(R.id.btn_addcall)
        val text = c.findViewById<TextView>(R.id.txv_calltype)
        img.setImageDrawable(imgsrc)
        text.text = txtstring
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }
}