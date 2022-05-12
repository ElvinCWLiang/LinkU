package com.example.linku.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.linku.R
import com.example.linku.data.local.LocalDatabase
import com.example.linku.databinding.FragmentHomeBinding
import kotlin.jvm.internal.Intrinsics


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    lateinit var textView: TextView
    private val binding get() = _binding!!

    private val TAG = "ev_" + javaClass.simpleName

    lateinit var homeViewModel: HomeViewModel
    lateinit var mHomeAdapter: HomeAdapter


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

        homeViewModel.syncBoard(binding.spnHome.selectedItemPosition)

        //
        homeViewModel.syncArticle.observe(viewLifecycleOwner) {
            if (!it) {
                val database = LocalDatabase.getInstance(requireContext())
                if (database != null) {
                    database.dataDao()
                }
            }
        }

        mHomeAdapter = HomeAdapter(this, container!!)
        binding.recyclerViewArticle.adapter = mHomeAdapter
        binding.recyclerViewArticle.layoutManager = LinearLayoutManager(activity)

        homeViewModel.homeAdapterMaterial.observe(viewLifecycleOwner){
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