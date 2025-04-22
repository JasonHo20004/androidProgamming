package com.example.to_dolistapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.to_dolistapp.adapter.TaskAdapter
import com.example.to_dolistapp.model.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val tasks = mutableListOf<Task>()
    private lateinit var adapter: TaskAdapter
    private var pickedDate: Long = System.currentTimeMillis()
    private val dateFormatter = SimpleDateFormat("MMM dd", Locale.getDefault())

    // UI Elements
    private lateinit var etTaskName: TextInputEditText
    private lateinit var btnPickDate: MaterialButton
    private lateinit var btnAdd: MaterialButton
//    private lateinit var fabAddTask: ExtendedFloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        etTaskName = findViewById(R.id.etTaskName)
        btnPickDate = findViewById(R.id.btnPickDate)
        btnAdd = findViewById(R.id.btnAdd)
//        fabAddTask = findViewById(R.id.fabAddTask)

        // Set up toolbar
        setSupportActionBar(findViewById(R.id.toolbar))

        // Set up RecyclerView and adapter
        adapter = TaskAdapter(
            tasks,
            { task -> handleTaskClick(task) },
            { task ->
                // Remove from the source list in MainActivity
                tasks.remove(task)
                adapter.update(tasks)
            },
            { task, newPriority ->
                val index = tasks.indexOf(task)
                if (index != -1) {
                    tasks[index] = task.copy(priority = newPriority)
                    adapter.update(tasks)
                }
            }
        )
        findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvTasksToday).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        // Setup date button with current date
        updateDateButtonText()

        // Set click listeners
        btnPickDate.setOnClickListener {
            showDatePicker()
        }

        btnAdd.setOnClickListener {
            addTask()
        }

//        fabAddTask.setOnClickListener {
//            // Scroll to top and focus on input field
//            etTaskName.requestFocus()
//            // Show keyboard
//            val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
//            imm.showSoftInput(etTaskName, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
//        }
    }

    private fun handleTaskClick(task: Task) {
        // Mark task as completed or show details
        val updatedTask = task.copy(isCompleted = !task.isCompleted)
        val index = tasks.indexOf(task)
        if (index != -1) {
            tasks[index] = updatedTask
            adapter.update(tasks)
        }
    }

    private fun addTask() {
        val name = etTaskName.text.toString()
        if (name.isNotBlank()) {
            // Create new task with current picked date
            val newTask = Task(
                name = name,
                due = pickedDate,
                isCompleted = false,
                priority = Task.Priority.MEDIUM // Default priority
            )

            // Add to list and update adapter
            tasks.add(newTask)
            sortTasks()
            adapter.update(tasks)

            // Clear input field
            etTaskName.text?.clear()

            // Reset date to current date
            pickedDate = System.currentTimeMillis()
            updateDateButtonText()
        }
    }

    private fun updateDateButtonText() {
        // Format the picked date and show on button
        btnPickDate.text = dateFormatter.format(pickedDate)
    }

    private fun showDatePicker() {
        // Get date fields from picked date
        val cal = Calendar.getInstance().apply {
            timeInMillis = pickedDate
        }
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        // Show date picker dialog
        DatePickerDialog(this, { _, y, m, d ->
            // When user picks a date, update pickedDate
            pickedDate = Calendar.getInstance().apply {
                set(Calendar.YEAR, y)
                set(Calendar.MONTH, m)
                set(Calendar.DAY_OF_MONTH, d)
                // Zero out hours/minutes/seconds
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            // Update button text
            updateDateButtonText()
        }, year, month, day).show()
    }

    private fun sortTasks() {
        // Default sort by date
        tasks.sortBy { it.due }
    }

    // Inflate menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.sort_menu, menu)
        return true
    }

    // Handle "Sort by …"
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_name -> tasks.sortBy { it.name }
            R.id.sort_date -> tasks.sortBy { it.due }
            R.id.sort_priority -> tasks.sortByDescending { it.priority.ordinal }
            else -> return super.onOptionsItemSelected(item)
        }
        adapter.update(tasks)
        return true
    }

    private fun groupTasks(): Pair<List<Task>, List<Task>> {
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val tomorrowStart = Calendar.getInstance().apply {
            add(Calendar.DATE, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val todayTasks = tasks.filter { it.due in todayStart until tomorrowStart }
        val upcomingTasks = tasks.filter { it.due >= tomorrowStart }

        return Pair(todayTasks, upcomingTasks)
    }
}