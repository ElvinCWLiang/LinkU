package com.project.linku.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.project.linku.MainActivity
import com.project.linku.R
import com.project.linku.data.local.ArticleModel
import com.project.linku.ui.utils.GlideApp
import kotlinx.android.synthetic.main.adapter_home.view.*

class HomeAdapter(_fragment: Fragment , _container: ViewGroup?):
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    private val TAG = "ev_" + javaClass.simpleName
    private val fragment: Fragment = _fragment
    private var marticleModel: List<ArticleModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.adapter_home, parent, false)
        return HomeViewHolder(this, view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(marticleModel[position], position)
    }

    override fun getItemCount(): Int {
        return marticleModel.size
    }

    inner class HomeViewHolder(mHomeAdapter: HomeAdapter, itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var pos = 0

        val txv_board = itemView.txv_conversation_local
        val txv_title = itemView.txv_time
        val txv_content = itemView.txv_content
        val img_author = itemView.img_local

        fun bind(articleModel: ArticleModel, position: Int) {
            txv_board.text = articleModel.publishBoard
            txv_title.text = articleModel.publishTitle
            txv_content.text = articleModel.publishContent
            pos = position
            GlideApp.with(itemView).load(MainActivity.userkeySet.get(articleModel.publishAuthor)?.useruri).placeholder(R.drawable.cat).circleCrop().into(img_author)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val bundle = Bundle()
            bundle.putString(fragment.resources.getString(R.string.article_id), marticleModel[pos].id)
            bundle.putLong(fragment.resources.getString(R.string.article_time), marticleModel[pos].publishTime)
            bundle.putString(fragment.resources.getString(R.string.article_board), marticleModel[pos].publishBoard)
            bundle.putString(fragment.resources.getString(R.string.article_author), marticleModel[pos].publishAuthor)
            bundle.putString(fragment.resources.getString(R.string.article_title), marticleModel[pos].publishTitle)
            bundle.putString(fragment.resources.getString(R.string.article_content), marticleModel[pos].publishContent)

            fragment.findNavController().navigate(R.id.navigation_article, bundle)
        }
    }

    fun setModelList(articleModel: List<ArticleModel>) {
        marticleModel = articleModel
    }

}