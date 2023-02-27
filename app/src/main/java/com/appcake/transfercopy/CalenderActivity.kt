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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.appcake.transfercopy.Adapter.CalendarDateAdapter
import com.appcake.transfercopy.Adapter.CalenderAdapter
import com.appcake.transfercopy.data.GoogleCalendar
import com.appcake.transfercopy.databinding.ActivityCalenderBinding
import com.kizitonwose.calendar.view.ViewContainer
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class CalenderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalenderBinding
    private lateinit var eventList : ArrayList<GoogleCalendar>
    private lateinit var selectedDate : LocalDate


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalenderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            selectedDate= LocalDate.now()
            setMonthView()
            binding.monthBackButton.setOnClickListener {
                selectedDate= selectedDate.minusMonths(1)
                setMonthView()
            }
            binding.monthNextButton.setOnClickListener {
                selectedDate= selectedDate.plusMonths(1)
                setMonthView()
            }
        }



        eventList= getEvents()
        val adapter = CalenderAdapter(eventList)
        binding.apply {
            eventRcView.layoutManager = LinearLayoutManager(this@CalenderActivity)
            eventRcView.adapter = adapter
        }


        binding.apply {
            allEventFilesText.setOnClickListener {
                val intent = Intent(this@CalenderActivity,AllEventsActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            backImg.setOnClickListener {
                val intent = Intent(Intent(this@CalenderActivity,PhoneStorageScreen::class.java))
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMonthView() {
       binding.monthOfYear.text = monthYearFromDate(selectedDate)
        val daysInMonthArray:ArrayList<String> = daysInMonthArray(selectedDate)
        val adapter = CalendarDateAdapter(daysInMonthArray)
        binding.calendarDateRcview.layoutManager = GridLayoutManager(this,7)
        binding.calendarDateRcview.adapter = adapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun daysInMonthArray(date: LocalDate): ArrayList<String> {
      val daysInMonthArray:ArrayList<String> = ArrayList()
        val yearMonth = YearMonth.from(date)
        var daysInMonth = yearMonth.lengthOfMonth()
        val firstOfMonth = selectedDate.withDayOfMonth(1)
        var daysOfWeek = firstOfMonth.dayOfWeek.value

        for (i in 2..42){
            if (i <= daysOfWeek || i> daysInMonth+daysOfWeek){
                daysInMonthArray.add("")
            }else{
                daysInMonthArray.add((i-daysOfWeek).toString())
            }
        }
        return daysInMonthArray
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun monthYearFromDate(date: LocalDate):String{
         val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }


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
