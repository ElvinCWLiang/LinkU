package com.project.linku.ui.utils

import android.content.Context
import android.text.format.DateFormat
import android.view.View
import com.project.linku.MainActivity
import com.project.linku.R
import com.project.linku.data.local.ArticleModel
import kotlinx.android.synthetic.main.layout_article_response.view.*
import java.util.*

class Parsefun {
    private val TAG = "ev_" + javaClass.simpleName

    companion object {
        private var instance: Parsefun? = null

        fun getInstance(): Parsefun {
            return instance ?: Parsefun()
        }
    }

    fun parseEmailasAccount(email: String): String {
        return email.replace(".","__dot__")
    }

    fun parseAccountasEmail(email: String): String {
        return email.replace("__dot__",".")
    }

    fun parseSecondsToDate(seconds: Long?): String {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        if (seconds != null) {
            calendar.timeInMillis = seconds * 1000L
        }
        val date = DateFormat.format("dd-MM-yyyy",calendar).toString()
        return date
    }

    fun parseModelToView(context: Context, articleModel: ArticleModel, v: View, pos: Int) {
        v.txv_author.text = articleModel.publishAuthor
        v.txv_respond.text = articleModel.publishContent
        v.txv_title.text = parseSecondsToDate(articleModel.publishTime)
        v.txv_floor.text = pos.toString()
        GlideApp.with(context).load(MainActivity.userkeySet.get(articleModel.publishAuthor)?.useruri).placeholder(R.drawable.cat).into(v.img_responder)
    }

    fun randomStringGenerator(): String {
        val alphabet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val randomString: String = List(20) { alphabet.random() }.joinToString("")
        return randomString
    }

}