package com.example.linku.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.linku.R
import com.example.linku.data.local.ArticleModel
import kotlinx.android.synthetic.main.adapter_chat.view.*

class ConversationAdapter(_fragment: Fragment, _container: ViewGroup?):
    RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    private val TAG = "ev_" + javaClass.simpleName
    private val container: ViewGroup? = null
    private val fragment: Fragment = _fragment
    private var marticleModel: List<ArticleModel> = ArrayList()

    // androidx.recyclerview.widget.RecyclerView.Adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.adapter_home, parent, false)
        return ConversationViewHolder(this, view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(marticleModel[position], position)
    }

    // androidx.recyclerview.widget.RecyclerView.Adapter
    override fun getItemCount(): Int {
        return marticleModel.size
    }

    inner class ConversationViewHolder(mHomeAdapter: ConversationAdapter, itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var pos = 0

        val txv_board = itemView.txv_account
        val txv_title = itemView.txv_time
        val txv_content = itemView.txv_content

        fun bind(articleModel: ArticleModel, position: Int) {
            txv_board.text = articleModel.publishBoard
            txv_title.text = articleModel.publishTitle
            txv_content.text = articleModel.publishContent
            pos = position
            itemView.setOnClickListener(this)
        }

        // android.view.View.OnClickListener
        override fun onClick(v: View?) {
            val bundle = Bundle()
            bundle.putString("articleId", marticleModel[pos].id)
            bundle.putLong("time", marticleModel[pos].publishTime)
            bundle.putString("board", marticleModel[pos].publishBoard)
            bundle.putString("author", marticleModel[pos].publishAuthor)
            bundle.putString("title", marticleModel[pos].publishTitle)
            bundle.putString("content", marticleModel[pos].publishContent)

            fragment.findNavController().navigate(R.id.navigation_article, bundle)
        }
    }

    fun setModelList(articleModel: List<ArticleModel>) {
        marticleModel = articleModel
    }

}