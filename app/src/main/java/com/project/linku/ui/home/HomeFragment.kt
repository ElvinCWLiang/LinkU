package com.project.linku.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
    lateinit var textView: TextView
    private val TAG = "ev_" + javaClass.simpleName

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home ,container, false)
        val root: View = binding.root
        binding.homeViewModel = homeViewModel
        initViews(container)

        return root
    }

    fun initViews(container: ViewGroup?) {
        binding.imgPublish.setOnClickListener{
            this.findNavController().navigate(R.id.navigation_publish)
        }
        binding.spnHome.setSelection(0, false);

        homeViewModel.syncBoard(binding.spnHome.selectedItemPosition)

        /* TODO **Bug** first time never show the publish icon.
            islogin need to be updated when livadata response back from MainActivity */
        if (MainActivity.islogin) { binding.imgPublish.visibility = View.VISIBLE }
        else { binding.imgPublish.visibility = View.INVISIBLE }

        val mHomeAdapter = HomeAdapter(this, container)
        binding.recyclerViewArticle.adapter = mHomeAdapter
        val staggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.recyclerViewArticle.layoutManager = staggeredGridLayoutManager

        homeViewModel.homeAdapterMaterial.observe(viewLifecycleOwner){
            Log.i(TAG,"size = ${it.size}")
            mHomeAdapter.setModelList(it)
            mHomeAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}