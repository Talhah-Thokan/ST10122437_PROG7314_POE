package com.medassist.app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.medassist.app.R
import com.medassist.app.data.repository.DataRepository
import com.medassist.app.ui.screens.booking.BookingActivity
import kotlinx.coroutines.launch

class BookingsFragment : Fragment() {

    private lateinit var searchEditText: TextInputEditText
    private lateinit var searchButton: MaterialButton
    private lateinit var doctorsRecyclerView: RecyclerView
    private lateinit var bookingsRecyclerView: RecyclerView
    private lateinit var doctorsAdapter: DoctorsAdapter
    private lateinit var bookingsAdapter: BookingsAdapter
    private lateinit var dataRepository: DataRepository

    private var allDoctors: List<Doctor> = emptyList()

    companion object {
        private const val TAG = "BookingsFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bookings, container, false)

        dataRepository = DataRepository(requireContext())

        setupViews(view)
        setupClickListeners()
        setupRecyclerViews()
        loadDoctors()

        Log.d(TAG, "BookingsFragment created")

        return view
    }

    private fun setupViews(view: View) {
        searchEditText = view.findViewById(R.id.searchEditText)
        searchButton = view.findViewById(R.id.searchButton)
        doctorsRecyclerView = view.findViewById(R.id.doctorsRecyclerView)
        bookingsRecyclerView = view.findViewById(R.id.bookingsRecyclerView)
    }

    private fun setupClickListeners() {
        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            if (query.isNotBlank()) {
                searchDoctors(query)
            } else {
                Toast.makeText(requireContext(), "Please enter a search term", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerViews() {
        doctorsAdapter = DoctorsAdapter(emptyList()) { doctor ->
            val intent = Intent(requireContext(), BookingActivity::class.java)
            intent.putExtra("DOCTOR_NAME", doctor.name)
            intent.putExtra("DOCTOR_SPECIALTY", doctor.specialty)
            intent.putExtra("DOCTOR_ID", doctor.id)
            startActivity(intent)
        }

        doctorsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        doctorsRecyclerView.adapter = doctorsAdapter

        val bookings = getSampleBookings()
        bookingsAdapter = BookingsAdapter(bookings) { booking ->
            Toast.makeText(requireContext(), "Booking: ${booking.doctorName} on ${booking.date}", Toast.LENGTH_SHORT).show()
        }

        bookingsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        bookingsRecyclerView.adapter = bookingsAdapter
    }

    private fun loadDoctors() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                Log.d(TAG, "🔄 Loading doctors...")
                val result = dataRepository.getDoctors()
                result.onSuccess { apiDoctors ->
                    allDoctors = apiDoctors.map { apiDoctor ->
                        Doctor(
                            id = apiDoctor.id,
                            name = apiDoctor.name,
                            specialty = apiDoctor.specialty,
                            rating = apiDoctor.rating,
                            distance = apiDoctor.distance,
                            experience = apiDoctor.experience,
                            price = apiDoctor.price,
                            availability = apiDoctor.availability
                        )
                    }
                    doctorsAdapter.updateDoctors(allDoctors)
                    Log.d(TAG, "✅ Successfully loaded ${allDoctors.size} doctors")
                    Toast.makeText(requireContext(), "Loaded ${allDoctors.size} doctors", Toast.LENGTH_SHORT).show()
                }.onFailure { exception ->
                    Log.e(TAG, "❌ Failed to load doctors from both REST API and Firebase", exception)
                    Toast.makeText(requireContext(), "Failed to load doctors. Using sample data.", Toast.LENGTH_SHORT).show()
                    allDoctors = getSampleDoctors()
                    doctorsAdapter.updateDoctors(allDoctors)
                }
            } catch (e: Exception) {
                if (isAdded) {
                    Log.e(TAG, "❌ Error loading doctors", e)
                    Toast.makeText(requireContext(), "Error loading doctors. Using sample data.", Toast.LENGTH_SHORT).show()
                    allDoctors = getSampleDoctors()
                    doctorsAdapter.updateDoctors(allDoctors)
                }
            }
        }
    }

    private fun searchDoctors(query: String) {
        val queryLower = query.lowercase().trim()

        if (queryLower.isEmpty()) {
            doctorsAdapter.updateDoctors(allDoctors)
            return
        }

        val filteredDoctors = allDoctors.filter { doctor ->
            doctor.name.lowercase().contains(queryLower) ||
            doctor.specialty.lowercase().contains(queryLower) ||
            doctor.experience.lowercase().contains(queryLower) ||
            doctor.availability.lowercase().contains(queryLower)
        }

        if (filteredDoctors.isEmpty()) {
            Toast.makeText(requireContext(), "No doctors found for '$query'", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Found ${filteredDoctors.size} doctor(s)", Toast.LENGTH_SHORT).show()
        }

        doctorsAdapter.updateDoctors(filteredDoctors)
        Log.d(TAG, "Search for '$query' returned ${filteredDoctors.size} results")
    }

    private fun getSampleDoctors(): List<Doctor> {
        return listOf(
            Doctor("1", "Dr. Thabo Mokoena", "General Practitioner", "4.9", "1.2 km away", "12 years experience", "R650 per consultation", "Available today"),
            Doctor("2", "Dr. Zanele Khumalo", "Cardiologist", "4.8", "3.5 km away", "18 years experience", "R1,250 per consultation", "Available tomorrow"),
            Doctor("3", "Dr. Sipho Dlamini", "Pediatrician", "4.9", "2.1 km away", "15 years experience", "R850 per consultation", "Available today"),
            Doctor("4", "Dr. Lerato Ndlovu", "Dermatologist", "4.7", "4.8 km away", "10 years experience", "R950 per consultation", "Available next week"),
            Doctor("5", "Dr. Mandla Mbatha", "Orthopedic Surgeon", "4.8", "5.2 km away", "22 years experience", "R1,450 per consultation", "Available tomorrow"),
            Doctor("6", "Dr. Nomvula Nkosi", "Psychiatrist", "4.9", "2.7 km away", "14 years experience", "R1,100 per session", "Available today")
        )
    }

    private fun getSampleBookings(): List<Booking> {
        return listOf(
            Booking("1", "Dr. Thabo Mokoena", "General Practitioner", "2025-10-15", "10:00 AM", "Confirmed"),
            Booking("2", "Dr. Sipho Dlamini", "Pediatrician", "2025-10-18", "2:30 PM", "Pending")
        )
    }
}

data class Doctor(
    val id: String,
    val name: String,
    val specialty: String,
    val rating: String,
    val distance: String,
    val experience: String,
    val price: String,
    val availability: String
)

data class Booking(
    val id: String,
    val doctorName: String,
    val specialty: String,
    val date: String,
    val time: String,
    val status: String
)

class DoctorsAdapter(
    private var doctors: List<Doctor>,
    private val onItemClick: (Doctor) -> Unit
) : RecyclerView.Adapter<DoctorsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: android.widget.TextView = view.findViewById(R.id.doctorName)
        val specialtyText: android.widget.TextView = view.findViewById(R.id.doctorSpecialty)
        val ratingText: android.widget.TextView = view.findViewById(R.id.doctorRating)
        val distanceText: android.widget.TextView = view.findViewById(R.id.doctorDistance)
        val priceText: android.widget.TextView = view.findViewById(R.id.doctorPrice)
        val availabilityText: android.widget.TextView = view.findViewById(R.id.doctorAvailability)
        val bookButton: MaterialButton = view.findViewById(R.id.bookButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_doctor_modern, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val doctor = doctors[position]
        holder.nameText.text = doctor.name
        holder.specialtyText.text = doctor.specialty
        holder.ratingText.text = "⭐ ${doctor.rating}"
        holder.distanceText.text = doctor.distance
        holder.priceText.text = doctor.price
        holder.availabilityText.text = doctor.availability
        holder.bookButton.setOnClickListener { onItemClick(doctor) }
    }

    override fun getItemCount(): Int = doctors.size

    fun updateDoctors(newDoctors: List<Doctor>) {
        doctors = newDoctors
        notifyDataSetChanged()
    }
}

class BookingsAdapter(
    private val bookings: List<Booking>,
    private val onItemClick: (Booking) -> Unit
) : RecyclerView.Adapter<BookingsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val doctorNameText: android.widget.TextView = view.findViewById(R.id.bookingDoctorName)
        val specialtyText: android.widget.TextView = view.findViewById(R.id.bookingSpecialty)
        val dateText: android.widget.TextView = view.findViewById(R.id.bookingDate)
        val timeText: android.widget.TextView = view.findViewById(R.id.bookingTime)
        val statusText: android.widget.TextView = view.findViewById(R.id.bookingStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booking = bookings[position]
        holder.doctorNameText.text = booking.doctorName
        holder.specialtyText.text = booking.specialty
        holder.dateText.text = booking.date
        holder.timeText.text = booking.time
        holder.statusText.text = booking.status

        val statusColor = when (booking.status) {
            "Confirmed" -> android.graphics.Color.parseColor("#4CAF50")
            "Pending" -> android.graphics.Color.parseColor("#FF9800")
            else -> android.graphics.Color.parseColor("#757575")
        }
        holder.statusText.setTextColor(statusColor)

        holder.itemView.setOnClickListener { onItemClick(booking) }
    }

    override fun getItemCount(): Int = bookings.size
}
