package com.example.linku.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.linku.R
import com.example.linku.data.local.ArticleModel
import com.google.firebase.analytics.FirebaseAnalytics
import kotlin.jvm.internal.Intrinsics


class HomeAdapter(_fragment: Fragment , _container: ViewGroup): RecyclerView.Adapter<HomeViewHolder> {
    private val container: ViewGroup? = null
    private val fragment: Fragment? = null
    private val TAG = "ev_" + javaClass.simpleName
    private var marticleModel: List<ArticleModel> = ArrayList()

    // androidx.recyclerview.widget.RecyclerView.Adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder? {
        Intrinsics.checkNotNullParameter(parent, "parent")
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.adapater_home, parent, false)
        Intrinsics.checkNotNullExpressionValue(view, "view")
        return HomeViewHolder(this, view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        Intrinsics.checkNotNullParameter(holder, "holder")
        holder.bind(marticleModel[position], position)
    }

    // androidx.recyclerview.widget.RecyclerView.Adapter
    override fun getItemCount(): Int {
        return marticleModel.size
    }

    inner class HomeViewHolder(mHomeAdapter: HomeAdapter, itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var pos = 0
        /* synthetic */ val `this$0`: HomeAdapter
        val txv_board: TextView
        val txv_content: TextView
        val txv_title: TextView

        fun bind(articleModel: ArticleModel, position: Int) {
            Intrinsics.checkNotNullParameter(articleModel, "articleModel")
            txv_board.text = articleModel.publishBoard
            txv_title.text = articleModel.publishTitle
            txv_content.text = articleModel.publishContent
            pos = position
        }

        // android.view.View.OnClickListener
        override fun onClick(v: View?) {
            var supportFragmentManager: FragmentManager
            Log.i(
                `this$0`.getTAG(),
                "v.id = " + (if (v != null) Integer.valueOf(v.getId()) else null) + " pos = " + pos + " board = " + txv_board.text as Any + " title = " + txv_title.text as Any + " content = " + txv_content.text as Any
            )
            val activity: FragmentActivity = `this$0`.getFragment().getActivity()
            val transaction: FragmentTransaction? =
                if (activity == null || activity.supportFragmentManager.also {
                        supportFragmentManager = it
                    } == null) null else supportFragmentManager.beginTransaction()
            if (transaction != null) {
                transaction.addToBackStack(null)
            }
            if (`this$0`.getContainer() != null) {
                val bundle = Bundle()
                bundle.putString("articleId", `this$0`.getMarticleModel().get(pos).getId())
                bundle.putLong("time", `this$0`.getMarticleModel().get(pos).getPublishTime())
                bundle.putString("board", `this$0`.getMarticleModel().get(pos).getPublishBoard())
                bundle.putString("author", `this$0`.getMarticleModel().get(pos).getPublishAuthor())
                bundle.putString("title", `this$0`.getMarticleModel().get(pos).getPublishTitle())
                bundle.putString(
                    FirebaseAnalytics.Param.CONTENT,
                    `this$0`.getMarticleModel().get(pos).getPublishContent()
                )
                fragment.findNavController(`this$0`.getFragment())
                    .navigate(R.id.navigation_article, bundle)
            }
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        init {
            Intrinsics.checkNotNullParameter(itemView, "itemView")
            this.`this$0` = `this$0`
            txv_board = itemView.findViewById(R.id.txv_conversation_local)
            txv_title = itemView.findViewById(R.id.txv_title)
            txv_content = itemView.findViewById(R.id.txv_content)
            itemView.setOnClickListener(this)
        }
    }

    fun setModelList(articleModel: List<ArticleModel>) {
        marticleModel = articleModel
    }

}