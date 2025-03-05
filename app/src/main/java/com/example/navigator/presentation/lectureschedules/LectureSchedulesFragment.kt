package com.example.navigator.presentation.lectureschedules

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.navigator.R
import com.example.navigator.databinding.FragmentLectureSchedulesBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class LectureSchedulesFragment : Fragment() {

    private var _binding: FragmentLectureSchedulesBinding? = null
    private val binding get() = _binding!!

    // Stores available slots for each room (room number -> list of time slots)
    private val roomSchedules = mutableMapOf<String, MutableList<String>>()

    // Stores booked lectures (persists using SharedPreferences)
    private val bookedSlots = mutableListOf<String>()
    private lateinit var adapter: LectureScheduleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLectureSchedulesBinding.inflate(inflater, container, false)

        // Set up RecyclerView
        adapter = LectureScheduleAdapter(bookedSlots) { position ->
            deleteBooking(position)
        }
        binding.recyclerViewBookings.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewBookings.adapter = adapter

        // Load saved bookings
        loadBookings()

        // Click listener for adding a new lecture schedule
        binding.buttonAddLecture.setOnClickListener {
            showAddLectureBottomSheet()
        }

        return binding.root
    }

    private fun deleteBooking(position: Int) {
        bookedSlots.removeAt(position)
        saveBookings()
        adapter.notifyItemRemoved(position)
        Toast.makeText(requireContext(), "Lecture deleted", Toast.LENGTH_SHORT).show()
    }

    private fun showAddLectureBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_add_lecture, null)
        bottomSheetDialog.setContentView(bottomSheetView)

        val lectureNameInput =
            bottomSheetView.findViewById<TextInputEditText>(R.id.lectureNameInput)
        val roomNumberInput =
            bottomSheetView.findViewById<TextInputEditText>(R.id.roomNumberInput)
        val lectureNameLayout =
            bottomSheetView.findViewById<TextInputLayout>(R.id.lectureNameLayout)
        val roomNumberLayout =
            bottomSheetView.findViewById<TextInputLayout>(R.id.roomNumberLayout)
        val dateInput = bottomSheetView.findViewById<TextInputEditText>(R.id.dateInput)
        val selectSlotButton =
            bottomSheetView.findViewById<View>(R.id.selectTimeSlotButton)
        val confirmButton =
            bottomSheetView.findViewById<View>(R.id.buttonConfirmLecture)

        var selectedSlot: String? = null
        var selectedDate: String? = null

        // Date Picker Setup
        dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    selectedDate = dateFormat.format(calendar.time)
                    dateInput.setText(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        selectSlotButton.setOnClickListener {
            val slots = mutableListOf("08:00 AM", "10:00 AM", "12:00 PM", "02:00 PM", "04:00 PM")
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select Time Slot")
                .setItems(slots.toTypedArray()) { _, which ->
                    selectedSlot = slots[which]
                    Toast.makeText(requireContext(), "Selected $selectedSlot", Toast.LENGTH_SHORT)
                        .show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        confirmButton.setOnClickListener {
            val lectureName = lectureNameInput.text.toString().trim()
            val roomNumber = roomNumberInput.text.toString().trim()

            if (lectureName.isEmpty()) {
                lectureNameLayout.error = "Lecture name is required"
                return@setOnClickListener
            } else {
                lectureNameLayout.error = null
            }

            if (roomNumber.isEmpty()) {
                roomNumberLayout.error = "Room number is required"
                return@setOnClickListener
            } else {
                roomNumberLayout.error = null
            }

            if (selectedDate == null) {
                Toast.makeText(requireContext(), "Please select a date", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (selectedSlot == null) {
                Toast.makeText(requireContext(), "Please select a time slot", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            showConfirmationDialog(lectureName, roomNumber, selectedDate!!, selectedSlot!!)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun showConfirmationDialog(
        lectureName: String,
        roomNumber: String,
        date: String,
        slot: String
    ) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirm Booking")
            .setMessage("Book $lectureName in Room $roomNumber on $date at $slot?")
            .setPositiveButton("Confirm") { _, _ ->
                val bookingEntry = "$lectureName - Room $roomNumber on $date at $slot"
                bookedSlots.add(bookingEntry)
                saveBookings()

                // Update UI
                adapter.notifyItemInserted(bookedSlots.size - 1)
                Toast.makeText(requireContext(), "Slot booked successfully!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveBookings() {
        val sharedPref = requireActivity().getSharedPreferences("LecturePrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putStringSet("bookedSlots", bookedSlots.toSet())
        editor.apply()
    }


    private fun loadBookings() {
        val sharedPref = requireActivity().getSharedPreferences("LecturePrefs", Context.MODE_PRIVATE)
        bookedSlots.clear()
        bookedSlots.addAll(sharedPref.getStringSet("bookedSlots", emptySet())!!)
        adapter.notifyDataSetChanged()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
