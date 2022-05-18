package com.example.linku.ui.utils

import android.content.Context
import android.view.View
import com.example.linku.MainActivity
import com.example.linku.R
import com.example.linku.data.local.ArticleModel
import kotlinx.android.synthetic.main.layout_article_response.view.*
import java.text.SimpleDateFormat

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
        return SimpleDateFormat("d MMM yy").format(seconds)
    }

    fun parseModelToView(context: Context, articleModel: ArticleModel, v: View, pos: Int) {
        v.txv_author.text = articleModel.publishAuthor
        v.txv_respond.text = articleModel.publishContent
        v.txv_time.text = parseSecondsToDate(articleModel.publishTime)
        v.txv_floor.text = pos.toString()
        GlideApp.with(context).load(MainActivity.userWithUrikeySet.get(articleModel.publishAuthor)).placeholder(R.drawable.cat).into(v.img_responder)
    }

}