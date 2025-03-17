package com.example.simplealarmsetter

import android.app.Activity
//import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import android.content.Intent
import android.provider.AlarmClock
import android.content.ActivityNotFoundException
import android.os.Build
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
//import com.example.simplealarmsetter.R
import java.time.format.DateTimeFormatter
import java.time.LocalTime


class MainActivity : Activity() {

    private lateinit var timePicker: TimePicker
    private lateinit var setAlarmButton: Button
    private lateinit var selectedTimeText: TextView
    private lateinit var alarmMessageInput: EditText

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Ui Components
        timePicker = findViewById(R.id.timePicker)
        setAlarmButton = findViewById(R.id.setAlarmButton)
        selectedTimeText = findViewById(R.id.selectedTimeText)
        alarmMessageInput = findViewById(R.id.alarmMessageInput)

        timePicker.setIs24HourView(true)

        timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            updateSelectedTimeText(hourOfDay, minute)
        }
        updateSelectedTimeText(timePicker.hour, timePicker.minute)
        setAlarmButton.setOnClickListener {
            setAlarm()
        }
    }

    /**
     * Updates the TextView showing the selected time
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateSelectedTimeText(hour: Int, minute: Int) {
        val time = LocalTime.of(hour, minute)
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        selectedTimeText.text = "Selected time: ${time.format(formatter)}"
    }

    /**
     * Creates and fires an implicit intent to set an alarm using the
     * device's default alarm app
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun setAlarm() {
        val hour = timePicker.hour
        val minute = timePicker.minute
        val message = if (alarmMessageInput.text.isNotEmpty()) {
            alarmMessageInput.text.toString()
        } else {
            "Wake up!"  // Default message
        }

        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minute)
            putExtra(AlarmClock.EXTRA_MESSAGE, message)
            putExtra(AlarmClock.EXTRA_SKIP_UI, false)
        }

        try {
            startActivity(intent)
            Toast.makeText(this, "Alarm request sent!", Toast.LENGTH_SHORT).show()
        } catch (e: ActivityNotFoundException) {
            // Handle the case where no app can process this intent
            Toast.makeText(this, "No alarm app available on this device", Toast.LENGTH_LONG).show()
        }
    }
}