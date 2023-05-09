package com.project.linku.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.linku.MainActivity
import com.project.linku.R
import com.project.linku.data.local.ArticleModel
import com.project.linku.databinding.AdapterHomeBinding
import com.project.linku.ui.utils.GlideApp

class HomeAdapter(private val navigate: (Bundle) -> Unit):
    ListAdapter<ArticleModel, HomeAdapter.HomeViewHolder>(DiffCallback()) {

    private val TAG = "ev_" + javaClass.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(AdapterHomeBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class HomeViewHolder(private val binding: AdapterHomeBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        var pos = 0

        fun bind(articleModel: ArticleModel, position: Int) {
            binding.txvBoard.text = articleModel.publishBoard
            binding.txvTitle.text = articleModel.publishTitle
            binding.txvAuthor.text = articleModel.publishAuthor
            pos = position
            if (articleModel.publishImage != "") {
                GlideApp.with(itemView).load(articleModel.publishImage).into(binding.imgArticlePhoto)
                binding.imgArticlePhoto.visibility = View.VISIBLE
                Log.i(TAG, "!= null ${articleModel.publishImage}")
            } else {
                binding.imgArticlePhoto.visibility = View.GONE
                Log.i(TAG, "null")
            }
            GlideApp.with(itemView).load(MainActivity.userkeySet[articleModel.publishAuthor]?.useruri).placeholder(R.drawable.cat).circleCrop().into(binding.imgLocal)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            val bundle = Bundle()
            view?.resources?.let {
                val articleModel = getItem(pos)
                bundle.putString(it.getString(R.string.article_id), articleModel.id)
                bundle.putLong(it.getString(R.string.article_time), articleModel.publishTime)
                bundle.putString(it.getString(R.string.article_board), articleModel.publishBoard)
                bundle.putString(it.getString(R.string.article_author), articleModel.publishAuthor)
                bundle.putString(it.getString(R.string.article_title), articleModel.publishTitle)
                bundle.putString(it.getString(R.string.article_content), articleModel.publishContent)
                bundle.putString(it.getString(R.string.article_image), articleModel.publishImage)
            }
            navigate.invoke(bundle)
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<ArticleModel>() {
        override fun areItemsTheSame(
            oldItem: ArticleModel,
            newItem: ArticleModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ArticleModel,
            newItem: ArticleModel
        ): Boolean {
            return oldItem.publishImage == newItem.publishImage &&
                    oldItem.publishAuthor == newItem.publishAuthor &&
                    oldItem.publishBoard == newItem.publishBoard &&
                    oldItem.publishContent == newItem.publishContent &&
                    oldItem.publishTime == newItem.publishTime &&
                    oldItem.publishTitle == newItem.publishTitle
        }
    }
}
