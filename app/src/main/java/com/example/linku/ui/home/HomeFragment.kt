package com.example.linku.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.getBinding
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.linku.R
import com.example.linku.data.local.LocalDatabase
import com.example.linku.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    // This property is only valid between onCreateView and
    // onDestroyView.
    lateinit var textView: TextView
    private val TAG = "ev_" + javaClass.simpleName

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home ,container, false)
        val root: View = binding.root

        binding.button.setOnClickListener{
            this.findNavController().navigate(R.id.navigation_publish)
        }

        binding.homeViewModel = homeViewModel

        binding.spnHome.setSelection(0, false);

        homeViewModel.syncBoard(binding.spnHome.selectedItemPosition)

        val mHomeAdapter = HomeAdapter(this, container)
        binding.recyclerViewArticle.adapter = mHomeAdapter
        binding.recyclerViewArticle.layoutManager = LinearLayoutManager(activity)

        homeViewModel.homeAdapterMaterial.observe(viewLifecycleOwner){
            Log.i(TAG,"size = ${it.size}")
            mHomeAdapter.setModelList(it)
            mHomeAdapter.notifyDataSetChanged()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}