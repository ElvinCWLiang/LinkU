package com.example.linku.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.linku.databinding.FragmentDashboardBinding
import kotlin.jvm.internal.Intrinsics




class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val TAG = "ev_" + javaClass.simpleName

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

        binding.dashboardViewModel = dashboardViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        dashboardViewModel.showDialog().observe(viewLifecycleOwner) {
            if (!it) {
                LoginDialog(requireContext(), dashboardViewModel).show()
            }

        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}