package com.example.linku.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.getBinding
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.linku.R
import com.example.linku.databinding.FragmentArticleBinding
import com.example.linku.ui.utils.parseFun
import com.google.firebase.analytics.FirebaseAnalytics
import kotlin.jvm.internal.Intrinsics


class ArticleFragment: Fragment {
    private var _binding: FragmentArticleBinding? = null
    var articleId: String? = null
    private val viewModel: ArticleViewModel? = null
    private val TAG = "ev_" + javaClass.simpleName
    private val binding get() = _binding!!



    // androidx.fragment.app.Fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val articleViewModel = ViewModelProvider(this).get(ArticleViewModel::class.java)
        _binding = DataBindingUtil.inflate(
            inflater!!,
            R.layout.fragment_article,
            container,
            false
        ) as FragmentArticleBinding
        val root: View = getBinding<ViewDataBinding>()!!.root
        binding.article(articleViewModel)
        val bundle: Bundle = getArguments()
        if (bundle != null) {
            val articleId = bundle.getString("articleId", "")
            val time = bundle.getLong("time", 0L)
            val board = bundle.getString("board", "")
            val author = bundle.getString("author", "")
            val title = bundle.getString("title", "")
            val content = bundle.getString(FirebaseAnalytics.Param.CONTENT, "")
            getBinding<ViewDataBinding>().txvAuthor.setText(author)
            getBinding<ViewDataBinding>().txvContent.setText(content)
            getBinding<ViewDataBinding>().txvTime.setText(
                parseFun().parseSecondsToDate(
                    java.lang.Long.valueOf(
                        time
                    )
                )
            )
            getBinding<ViewDataBinding>().txvTitle.setText(title)
            getBinding<ViewDataBinding>().txvBoard.setText(board)
            Log.i(
                TAG,
                "articleId = " + articleId + " board = " + board + " author = " + author + " title = " + title + " time = " + parseFun().parseSecondsToDate(
                    java.lang.Long.valueOf(time)
                ) + " content = " + content
            )
            Intrinsics.checkNotNullExpressionValue(articleId, "articleId")
            Intrinsics.checkNotNullExpressionValue(board, "board")
            articleViewModel.fetchlocalArticleResponse(articleId, board)
        }
        articleViewModel.getArticleAdapterMaterial()
            .observe(getViewLifecycleOwner(), object : Observer() {
                // from class: com.example.linku.ui.home.ArticleFragment$$ExternalSyntheticLambda1
                // androidx.lifecycle.Observer
                fun onChanged(obj: Any?) {
                    ArticleFragment.`m2411onCreateView$lambda0`(
                        this@ArticleFragment,
                        obj as List<*>?
                    )
                }
            })
        articleViewModel.getUserReply().observe(getViewLifecycleOwner(), object : Observer() {
            // from class: com.example.linku.ui.home.ArticleFragment$$ExternalSyntheticLambda0
            // androidx.lifecycle.Observer
            fun onChanged(obj: Any?) {
                ArticleFragment.`m2412onCreateView$lambda1`(this@ArticleFragment, obj as String?)
            }
        })
        return root
    }

}