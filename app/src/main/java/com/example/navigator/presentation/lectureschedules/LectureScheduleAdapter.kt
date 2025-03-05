package com.example.navigator.presentation.lectureschedules

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.navigator.R

class LectureScheduleAdapter(
    private val bookings: MutableList<String>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<LectureScheduleAdapter.LectureViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LectureViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lecture_schedule, parent, false)
        return LectureViewHolder(view)
    }

    override fun onBindViewHolder(holder: LectureViewHolder, position: Int) {
        val booking = bookings[position]
        val parts = booking.split(" - ")

        holder.lectureName.text = parts[0]
        holder.roomNumber.text = parts.getOrNull(1) ?: "Unknown Room"
        holder.timeSlot.text = parts.getOrNull(2) ?: "Unknown Time"

        holder.deleteButton.setOnClickListener {
            onDeleteClick(position)
        }
    }

    override fun getItemCount() = bookings.size

    class LectureViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lectureName: TextView = view.findViewById(R.id.textLectureName)
        val roomNumber: TextView = view.findViewById(R.id.textRoomNumber)
        val timeSlot: TextView = view.findViewById(R.id.textTimeSlot)
        val deleteButton: ImageView = view.findViewById(R.id.buttonDeleteLecture)
    }
}
