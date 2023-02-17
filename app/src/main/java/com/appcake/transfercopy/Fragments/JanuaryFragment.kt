package com.appcake.transfercopy.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.appcake.transfercopy.Adapter.EventAdapter
import com.appcake.transfercopy.CalenderActivity
import com.appcake.transfercopy.R
import com.appcake.transfercopy.data.GoogleCalendar
import com.appcake.transfercopy.databinding.FragmentJanuaryBinding


class JanuaryFragment : Fragment() {
    private lateinit var binding:FragmentJanuaryBinding
    private lateinit var eventList: ArrayList<GoogleCalendar>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJanuaryBinding.inflate(layoutInflater,container, false)
        return binding.root
         val activity = requireActivity() as CalenderActivity
         eventList = activity.getEvents()
         val adapter = EventAdapter(eventList)
         binding.janeventrcView.layoutManager = LinearLayoutManager(requireContext())
        binding.janeventrcView.adapter = adapter
    }
}