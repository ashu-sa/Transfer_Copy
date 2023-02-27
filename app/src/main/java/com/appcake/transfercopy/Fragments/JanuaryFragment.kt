package com.appcake.transfercopy.Fragments

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
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
import java.util.*
import kotlin.collections.ArrayList


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

         eventList = getEvents()
         val adapter = EventAdapter(eventList)
         binding.janeventrcView.layoutManager = LinearLayoutManager(requireContext())
         binding.janeventrcView.adapter = adapter
    }
    @SuppressLint("Range")
    fun getEvents():ArrayList<GoogleCalendar>{
        var eventList: ArrayList<GoogleCalendar> = ArrayList()
        val startTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis
        val columns = arrayOf(
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.ORIGINAL_INSTANCE_TIME
        )
        val uri = Uri.parse("content://com.android.calendar/events")
        val calendercursor: Cursor = requireContext().contentResolver.query(uri,columns, null, null,"")!!

        if (calendercursor != null)
            if (calendercursor.moveToNext())
                do {
                    val date = calendercursor.getString(calendercursor.getColumnIndex(
                        CalendarContract.Events.DTSTART)).toLong()
                    val title = calendercursor.getString(calendercursor.getColumnIndex(
                        CalendarContract.Events.TITLE))

                    val startDate = Calendar.getInstance().apply { timeInMillis = date }
                    val startTimeString = String.format("%02d:%02d", startDate.get(Calendar.HOUR_OF_DAY), startDate.get(
                        Calendar.MINUTE))

                    try {
                        val calender = GoogleCalendar(title,startTimeString)
                        eventList.add(calender)

                    }catch (e:java.lang.Exception){}
                }while (calendercursor.moveToNext())
        calendercursor?.close()

        return eventList

    }
}