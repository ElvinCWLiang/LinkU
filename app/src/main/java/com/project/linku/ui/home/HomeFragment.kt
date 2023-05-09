package com.project.linku.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.project.linku.MainActivity
import com.project.linku.R
import com.project.linku.databinding.FragmentHomeBinding

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
        binding.homeViewModel = homeViewModel
        initViews()

        return binding.root
    }

    private fun initViews() {
        binding.imgPublish.setOnClickListener{
            this.findNavController().navigate(R.id.navigation_publish)
        }
        binding.spnHome.setSelection(0, false);

        homeViewModel.syncBoard(binding.spnHome.selectedItemPosition)

        /* TODO **Bug** first time never show the publish icon.
            islogin need to be updated when livadata response back from MainActivity */
        if (MainActivity.islogin) { binding.imgPublish.visibility = View.VISIBLE }
        else { binding.imgPublish.visibility = View.INVISIBLE }

        val homeAdapter = HomeAdapter {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_article, it)
        }

        val staggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

        binding.recyclerViewArticle.apply {
            adapter = homeAdapter
            layoutManager = staggeredGridLayoutManager
        }

        homeViewModel.homeAdapterMaterial.observe(viewLifecycleOwner){
            homeAdapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}