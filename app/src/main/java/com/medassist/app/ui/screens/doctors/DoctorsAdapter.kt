package com.medassist.app.ui.screens.doctors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.medassist.app.R

/**
 * RecyclerView adapter for displaying doctors
 */
class DoctorsAdapter(
    private val doctors: List<Doctor>,
    private val onItemClick: (Doctor) -> Unit
) : RecyclerView.Adapter<DoctorsAdapter.DoctorViewHolder>() {

    class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.doctorName)
        val specialtyText: TextView = itemView.findViewById(R.id.doctorSpecialty)
        val ratingText: TextView = itemView.findViewById(R.id.doctorRating)
        val distanceText: TextView = itemView.findViewById(R.id.doctorDistance)
        val experienceText: TextView = itemView.findViewById(R.id.doctorExperience)
        val priceText: TextView = itemView.findViewById(R.id.doctorPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctors[position]
        
        holder.nameText.text = doctor.name
        holder.specialtyText.text = doctor.specialty
        holder.ratingText.text = "⭐ ${doctor.rating}"
        holder.distanceText.text = doctor.distance
        holder.experienceText.text = doctor.experience
        holder.priceText.text = doctor.price
        
        holder.itemView.setOnClickListener {
            onItemClick(doctor)
        }
    }

    override fun getItemCount(): Int = doctors.size
}

