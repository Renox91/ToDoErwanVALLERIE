package com.erwanvallerie.todo.form

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.erwanvallerie.todo.R
import com.erwanvallerie.todo.tasklist.Task
import java.util.*

class FormActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        val taskToEdit = intent.getSerializableExtra("taskToEdit") as? Task;
        if (taskToEdit != null) {
            findViewById<EditText>(R.id.editTextTitre).setText(taskToEdit.title)
            findViewById<EditText>(R.id.editTextDescription).setText(taskToEdit.description)
        }

        val buttonInsert = findViewById<Button>(R.id.button);
        buttonInsert.setOnClickListener {
            val title = findViewById<EditText>(R.id.editTextTitre).text.toString();
            val desc = findViewById<EditText>(R.id.editTextDescription).text.toString();
            val newTask = Task(taskToEdit?.id ?: UUID.randomUUID().toString(), title, desc);
            intent.putExtra("task", newTask);
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}