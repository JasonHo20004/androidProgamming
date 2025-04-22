package com.example.to_dolistapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolistapp.adapter.TaskAdapter
import com.example.to_dolistapp.model.Task
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private val tasks = mutableListOf<Task>()
    private lateinit var adapter: TaskAdapter
    private var pickedDate: Long = System.currentTimeMillis()

    override fun onCreate(saved: Bundle?) {
        super.onCreate(saved)
        setContentView(R.layout.activity_main)

        adapter = TaskAdapter(tasks)
        findViewById<RecyclerView>(R.id.rvTasks).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        findViewById<Button>(R.id.btnPickDate).setOnClickListener {
            showDatePicker()
        }

        findViewById<Button>(R.id.btnAdd).setOnClickListener {
            val name = findViewById<EditText>(R.id.etTaskName).text.toString()
            if (name.isNotBlank()) {
                tasks += Task(name, pickedDate)
                adapter.update(tasks)
            }
        }
    }

    private fun showDatePicker() {
        // 1) Get today’s fields from a Calendar
        val cal = Calendar.getInstance()
        val year  = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)      // zero‑based already
        val day   = cal.get(Calendar.DAY_OF_MONTH)

        // 2) Show the picker
        DatePickerDialog(this, { _, y, m, d ->
            // 3) When user picks, store as millis
            pickedDate = Calendar.getInstance().apply {
                set(Calendar.YEAR,  y)
                set(Calendar.MONTH, m)
                set(Calendar.DAY_OF_MONTH, d)
                // optionally zero‑out hours/min/sec if you only care about date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }, year, month, day).show()
    }

    // Inflate menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.sort_menu, menu)
        return true
    }

    // Handle “Sort by …”
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_name -> tasks.sortBy { it.name }
            R.id.sort_date -> tasks.sortBy { it.due }
            else -> return super.onOptionsItemSelected(item)
        }
        adapter.update(tasks)
        return true
    }
}
