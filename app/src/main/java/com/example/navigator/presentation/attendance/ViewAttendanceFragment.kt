package com.example.navigator.presentation.attendance

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.navigator.databinding.FragmentViewAttendanceBinding

class ViewAttendanceFragment : Fragment() {

    private var _binding: FragmentViewAttendanceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewAttendanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val attendanceData = getAttendanceMap(requireContext())

        val adapter = AttendanceAdapter(attendanceData)
        binding.recyclerViewAttendance.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewAttendance.adapter = adapter
    }

    private fun getAttendanceMap(context: Context): Map<String, List<String>> {
        val sharedPref = context.getSharedPreferences("AttendancePrefs", Context.MODE_PRIVATE)
        val allEntries = sharedPref.all
        val attendanceMap = mutableMapOf<String, List<String>>()

        for ((key, attendees) in allEntries) {
            if (key.startsWith("attendance_")) {
                val roomNumber = key.removePrefix("attendance_")
                val names = (attendees as? Set<*>)?.map { it.toString() } ?: emptyList()
                attendanceMap[roomNumber] = names
            }
        }

        return attendanceMap
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
