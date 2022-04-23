package com.example.linku.ui.dashboard

import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.linku.MainActivity
import com.example.linku.R
import com.example.linku.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)



        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.dashboardViewModel = dashboardViewModel

        val textView: TextView = binding.textUsername


        dashboardViewModel.text.observe(viewLifecycleOwner) {
            if (it) {
                var cc = LoginDialog(requireContext())
                cc.show()
            }
        }








        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}