package com.project.linku.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.project.linku.MainActivity
import com.project.linku.R
import com.project.linku.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel : HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        initViews()
        return binding.root
    }

    private fun initViews() {
        val homeAdapter = HomeAdapter {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_article, it)
        }

        with(binding) {
            imgPublish.setOnClickListener{
                it.findNavController().navigate(R.id.navigation_publish)
            }

            spnHome.setSelection(0, false)
            spnHome.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    homeViewModel.accept(HomeViewModel.UiAction.Search(p2))
//                    homeViewModel.syncBoard(p2)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }

            /* TODO **Bug** first time never show the publish icon.
            islogin need to be updated when livadata response back from MainActivity */
            if (MainActivity.islogin) { imgPublish.visibility = View.VISIBLE }
            else { imgPublish.visibility = View.INVISIBLE }

            recyclerViewArticle.apply {
                adapter = homeAdapter
                layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            }

            // Swipe to refresh article
            swipe.setOnRefreshListener {
                homeViewModel.accept(HomeViewModel.UiAction.Search(spnHome.selectedItemPosition))
            }
        }

        with(homeViewModel) {
            accept(HomeViewModel.UiAction.Search(0))
        }

        lifecycleScope.launch {
            launch {
                homeViewModel.isRefresh.collectLatest {
                    binding.swipe.isRefreshing = it
                }
            }

            launch {
                homeViewModel.state.collectLatest {
                    homeAdapter.submitList(it)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}