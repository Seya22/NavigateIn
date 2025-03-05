package com.example.navigator.presentation.confirmer

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.navigator.databinding.FragmentConfirmBinding
import com.example.navigator.presentation.preview.MainEvent
import com.example.navigator.presentation.preview.MainShareModel
import com.example.navigator.presentation.preview.MainUiEvent
import kotlinx.coroutines.launch

class ConfirmFragment : Fragment() {

    private val mainModel: MainShareModel by activityViewModels()

    private var _binding: FragmentConfirmBinding? = null
    private val binding get() = _binding!!

    private val args: ConfirmFragmentArgs by navArgs()
    private val confType by lazy { args.confirmType }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    mainModel.onEvent(MainEvent.RejectConfObject(confType))
                    findNavController().popBackStack()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setEnabled(true)

        binding.acceptButton.setOnClickListener {
            setEnabled(false)
            mainModel.onEvent(MainEvent.AcceptConfObject(confType))
        }

        binding.rejectButton.setOnClickListener {
            setEnabled(false)
            mainModel.onEvent(MainEvent.RejectConfObject(confType))
            findNavController().popBackStack()
        }

        // Show "Mark Attendance" button when confirm_place is triggered
        binding.markAttendanceButton.visibility = View.VISIBLE
        binding.markAttendanceButton.setOnClickListener {
            promptForAttendance()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainModel.mainUiEvents.collect { uiEvent ->
                    when (uiEvent) {
                        is MainUiEvent.InitSuccess,
                        is MainUiEvent.EntryCreated,
                        is MainUiEvent.EntryAlreadyExists -> {
                            val action = ConfirmFragmentDirections.actionConfirmFragmentToRouterFragment()
                            findNavController().navigate(action)
                        }
                        is MainUiEvent.InitFailed -> {
                            findNavController().popBackStack()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun promptForAttendance() {
        val scannedNumber = mainModel.confirmationObject.value?.label
        if (scannedNumber.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "No scanned number found!", Toast.LENGTH_SHORT).show()
            return
        }

        val inputField = EditText(requireContext())
        inputField.hint = "Enter Your Name"

        AlertDialog.Builder(requireContext())
            .setTitle("Mark Attendance for Room $scannedNumber")
            .setView(inputField)
            .setPositiveButton("Confirm") { _, _ ->
                val name = inputField.text.toString().trim()
                if (name.isNotEmpty()) {
                    saveAttendance(scannedNumber, name)
                } else {
                    Toast.makeText(requireContext(), "Name is required!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveAttendance(roomNumber: String, name: String) {
        val sharedPref = requireActivity().getSharedPreferences("AttendancePrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        val attendanceKey = "attendance_$roomNumber"
        val currentAttendance = sharedPref.getStringSet(attendanceKey, mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        currentAttendance.add(name)  // Add new attendee to set

        editor.putStringSet(attendanceKey, currentAttendance.toSet())  // Store updated set
        editor.apply()

        Toast.makeText(requireContext(), "Attendance marked for $name in Room $roomNumber", Toast.LENGTH_SHORT).show()
    }


    private fun setEnabled(enabled: Boolean) {
        binding.acceptButton.isEnabled = enabled
        binding.rejectButton.isEnabled = enabled
        binding.markAttendanceButton.isEnabled = enabled
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val CONFIRM_INITIALIZE = 0
        const val CONFIRM_ENTRY = 1
    }
}
