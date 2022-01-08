package com.erwanvallerie.todo.tasklist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.erwanvallerie.todo.databinding.FragmentTaskListBinding

import com.erwanvallerie.todo.R
import com.erwanvallerie.todo.form.FormActivity
import com.erwanvallerie.todo.network.Api
import com.erwanvallerie.todo.network.TasksRepository
import com.erwanvallerie.todo.user.UserInfoActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.util.*

class TaskListFragment : Fragment() {
    private val myAdapter = TaskListAdapter()
    private val viewModel = TaskListViewModel();


    // private val taskList = mutableListOf<Task>();
    /*private var taskList = mutableListOf(
        Task(id = "id_1", title = "Task 1", description = "description 1"),
        Task(id = "id_2", title = "Task 2"),
        Task(id = "id_3", title = "Task 3")
    )*/

    private val formLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as? Task
        if (task != null) {
            val oldTask = viewModel.taskList.value?.firstOrNull {it.id == task.id}
            if (oldTask != null) {
                viewModel.updateTask(task, oldTask);
            } else {
                viewModel.createTask(task);
            }
            myAdapter.submitList(viewModel.taskList.value?.toList());
        }
    }

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        /*val rootView = inflater.inflate(R.layout.fragment_task_list, container, false);
        return rootView*/
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.taskList.observe(viewLifecycleOwner) { newList ->
            myAdapter.submitList(newList);
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity);

        recyclerView.adapter = myAdapter
        //myAdapter.submitList(taskList.toList());

        viewModel.refresh();
        //val button = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val button = binding.floatingActionButton;

        button.setOnClickListener{
            //Log.d("button", "oui")
            //taskList.add(Task(id = UUID.randomUUID().toString(), title = "Task ${taskList.size + 1}"))
            //myAdapter.submitList(taskList.toList())
            val intent = Intent(context, FormActivity::class.java)
            formLauncher.launch(intent)
        }

        /*myAdapter.onClickDelete = { task ->
            taskList.removeAll { t -> t.id == task.id }
            myAdapter.submitList(taskList.toList())
        }

        myAdapter.onClickEdit = { task ->
            val intent = Intent(activity, FormActivity::class.java)
            intent.putExtra("taskToEdit", task)
            formLauncher.launch(intent)
        }*/

        /*lifecycleScope.launch { // on lance une coroutine car `collect` est `suspend`
            tasksRepository.taskList.collect { newList ->
                // cette lambda est executée à chaque fois que la liste est mise à jour dans le repository
                // -> ici, on met à jour la liste dans l'adapteur
                tasksRepository.refresh();
                taskList.addAll(newList);
                myAdapter.submitList(taskList.toList())
            }
        }*/

        myAdapter.onClickDelete = { task ->
            viewModel.deleteTask(task);
        }

        myAdapter.onClickEdit = { task ->
            val intent = Intent(activity, FormActivity::class.java)
            intent.putExtra("taskToEdit", task)
            formLauncher.launch(intent)
        }

        binding.userImage.setOnClickListener{
            //Log.d("button", "oui")
            //taskList.add(Task(id = UUID.randomUUID().toString(), title = "Task ${taskList.size + 1}"))
            //myAdapter.submitList(taskList.toList())
            val intent = Intent(activity, UserInfoActivity::class.java)
            formLauncher.launch(intent)
        }


    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val userInfo = Api.userWebService.getInfo().body()!!
            val userInfoTextView = binding.userInfoTextView;
            userInfoTextView.text = "${userInfo.firstName} ${userInfo.lastName}"
            if (userInfo.avatar != null) {
                binding.userImage.load(userInfo.avatar) {
                    transformations(CircleCropTransformation())
                }
            }
            else
            {
                binding.userImage.load("https://blog.snowleader.com/wp-content/uploads/2020/12/escalade-1.jpg") {
                    transformations(CircleCropTransformation())
                }
            }
        }
    }
}
