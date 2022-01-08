package com.erwanvallerie.todo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.erwanvallerie.todo.tasklist.TaskListFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        println("other message")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}