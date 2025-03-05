package com.example.navigator.presentation.attendance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.navigator.R

class AttendanceAdapter(private val roomAttendance: Map<String, List<String>>) :
    RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder>() {

    private val expandedRooms = mutableSetOf<String>()  // To track expanded items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attendance_room, parent, false)
        return AttendanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val room = roomAttendance.keys.toList()[position]
        val attendees = roomAttendance[room] ?: emptyList()

        holder.roomTitle.text = "Room $room"
        holder.attendanceList.text = attendees.joinToString("\n")

        val isExpanded = expandedRooms.contains(room)
        holder.attendanceList.visibility = if (isExpanded) View.VISIBLE else View.GONE

        holder.expandButton.setImageResource(
            if (isExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
        )

        holder.expandButton.setOnClickListener {
            if (isExpanded) {
                expandedRooms.remove(room)
            } else {
                expandedRooms.add(room)
            }
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = roomAttendance.size

    inner class AttendanceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val roomTitle: TextView = view.findViewById(R.id.textRoomTitle)
        val attendanceList: TextView = view.findViewById(R.id.textAttendanceList)
        val expandButton: ImageButton = view.findViewById(R.id.buttonExpand)
    }
}
