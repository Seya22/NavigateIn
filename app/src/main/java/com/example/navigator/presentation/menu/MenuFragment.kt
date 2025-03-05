package com.example.navigator.presentation.menu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.navigator.databinding.FragmentMenuBinding
import com.example.navigator.presentation.MainActivity
import com.example.navigator.R

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigate to OrientationFragment
        binding.buttonStart.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_orientationFragment)
        }


        binding.buttonLectureSchedules.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_lectureSchedulesFragment)
        }
    // Navigate to ViewAttendanceFragment
        binding.buttonViewAttendance.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_viewAttendanceFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
