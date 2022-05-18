package com.example.linku.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.linku.MainActivity
import com.example.linku.R
import com.example.linku.databinding.FragmentArticleBinding
import com.example.linku.ui.utils.GlideApp
import com.example.linku.ui.utils.Parsefun
import com.google.firebase.analytics.FirebaseAnalytics

class ArticleFragment: Fragment() {
    private val TAG = "ev_" + javaClass.simpleName
    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val articleViewModel = ViewModelProvider(this).get(ArticleViewModel::class.java)
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_article, container, false)

        val root: View = binding.root
        binding.articleViewModel = articleViewModel

        /* receive the article information from HomeFragment >> */
        val bundle: Bundle? = getArguments()
        if (bundle != null) {
            val articleId = bundle.getString("articleId", "")
            val time = bundle.getLong("time", 0L)
            val board = bundle.getString("board", "")
            val author = bundle.getString("author", "")
            val title = bundle.getString("title", "")
            val content = bundle.getString(FirebaseAnalytics.Param.CONTENT, "")
            binding.txvAuthor.text = author
            binding.txvContent.text = content
            binding.txvTime.text = Parsefun().parseSecondsToDate(time)
            binding.txvTitle.text = title
            binding.txvBoard.text = board
            GlideApp.with(this).load(MainActivity.userWithUrikeySet.get(author)).placeholder(R.drawable.cat).into(binding.imgAuthor)
            Log.i(TAG,"articleId = $articleId board = $board author $author title = $title time = ${Parsefun().parseSecondsToDate((time))} content = $content")
            articleViewModel.synclocalArticleResponse(articleId, board)
        }
        /* receive the article information from HomeFragment << */

        /* LiveData for the replying message in LocalRepository >> */
        articleViewModel._articleAdapterMaterial.observe(viewLifecycleOwner){
            Log.i(TAG, "size = ${it.size}")
            for (i in 0 until it.size) {
                Log.i(TAG, "i = $i, size = ${it.size}")
                val v: View = LayoutInflater.from(requireContext()).inflate(R.layout.layout_article_response, null)
                Parsefun.getInstance().parseModelToView(requireContext(),it[i], v, i)
                binding.scrollArticleMaterial.addView(v)
            }
        }
        /* LiveData for the replying message in LocalRepository << */

        /* clear the input edittext once user send the reply >> */
        articleViewModel.userReply.observe(viewLifecycleOwner) {
            if(it == "") binding.edtReply.text.clear()
        }
        /* clear the input edittext once user send the reply << */

        return root
    }

}