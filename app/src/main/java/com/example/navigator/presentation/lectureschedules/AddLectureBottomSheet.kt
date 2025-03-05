package com.example.navigator.presentation.lectureschedules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.navigator.R
import com.example.navigator.databinding.AddLectureBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddLectureBottomSheet(
    private val onLectureAdded: (String, String, String) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: AddLectureBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var selectedSlot: String? = null
    private val timeSlots = listOf("08:00 AM", "10:00 AM", "12:00 PM", "02:00 PM", "04:00 PM")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddLectureBottomSheetBinding.inflate(inflater, container, false)

        // Time Slot Selection
        binding.selectTimeSlotButton.setOnClickListener {
            showSlotSelectionDialog()
        }

        // Confirm Lecture
        binding.buttonConfirmLecture.setOnClickListener {
            val lectureName = binding.lectureNameInput.text.toString().trim()
            val roomNumber = binding.roomNumberInput.text.toString().trim()

            if (lectureName.isEmpty()) {
                binding.lectureNameLayout.error = "Lecture name is required"
                return@setOnClickListener
            }

            if (roomNumber.isEmpty()) {
                binding.roomNumberLayout.error = "Room number is required"
                return@setOnClickListener
            }

            if (selectedSlot == null) {
                Toast.makeText(requireContext(), "Please select a time slot", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            onLectureAdded(lectureName, roomNumber, selectedSlot!!)
            dismiss()
        }

        return binding.root
    }

    private fun showSlotSelectionDialog() {
        val availableSlots = timeSlots.toTypedArray()

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Select Time Slot")
            .setItems(availableSlots) { _, which ->
                selectedSlot = availableSlots[which]
                binding.selectTimeSlotButton.text = "Selected: $selectedSlot"
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
