package com.appcake.transfercopy

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.appcake.transfercopy.Adapter.CalenderAdapter
import com.appcake.transfercopy.data.GoogleCalendar
import com.appcake.transfercopy.databinding.ActivityCalenderBinding
import com.kizitonwose.calendar.view.ViewContainer
import java.util.*
import kotlin.collections.ArrayList


class CalenderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalenderBinding
    private lateinit var eventList : ArrayList<GoogleCalendar>
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalenderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventList= getEvents()
        val adapter = CalenderAdapter(eventList)
        binding.apply {
            eventRcView.layoutManager = LinearLayoutManager(this@CalenderActivity)
            eventRcView.adapter = adapter
        }

//        binding.dateRc.dayBinder = object : MonthDayBinder<DayViewContainer> {
//            // Called only when a new container is needed.
//            override fun create(view: View) = DayViewContainer(view)
//
//            // Called every time we need to reuse a container.
//            @SuppressLint("ResourceAsColor")
//            @RequiresApi(Build.VERSION_CODES.O)
//            override fun bind(container: DayViewContainer, data: CalendarDay) {
//                container.textView.text = data.date.dayOfMonth.toString()
//                if (data.position != DayPosition.MonthDate) {
//                    container.textView.setTextColor(Color.GRAY)
//                }
//            }
//        }
//        val currentMonth = YearMonth.now()
//        val startMonth = currentMonth.minusMonths(100)  // Adjust as needed
//        val endMonth = currentMonth.plusMonths(100)  // Adjust as needed
//        val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY) // Available from the library
//        binding.dateRc.setup(startMonth, endMonth,daysOfWeek.first())
//        binding.dateRc.scrollToMonth(currentMonth)



        binding.apply {
            allEventFilesText.setOnClickListener {
                val intent = Intent(this@CalenderActivity,AllEventsActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }
    class DayViewContainer(view: View) : ViewContainer(view) {
        val textView = view.findViewById<TextView>(R.id.date_text) }

    @SuppressLint("Range", "SuspiciousIndentation")
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
        val calendercursor: Cursor = this@CalenderActivity.contentResolver.query(uri,columns, null, null,"")!!

        if (calendercursor != null)
            if (calendercursor.moveToNext())
                do {
                    val date = calendercursor.getString(calendercursor.getColumnIndex(CalendarContract.Events.DTSTART)).toLong()
                    val title = calendercursor.getString(calendercursor.getColumnIndex(CalendarContract.Events.TITLE))

                    val startDate = Calendar.getInstance().apply { timeInMillis = date }
                    val startTimeString = String.format("%02d:%02d", startDate.get(Calendar.HOUR_OF_DAY), startDate.get(Calendar.MINUTE))

                    try {
                        val calender = GoogleCalendar(title,startTimeString)
                         eventList.add(calender)

                    }catch (e:java.lang.Exception){}
                }while (calendercursor.moveToNext())
        calendercursor?.close()

        return eventList

    }

}
