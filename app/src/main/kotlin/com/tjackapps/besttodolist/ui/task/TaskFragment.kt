package com.tjackapps.besttodolist.ui.task

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tjackapps.besttodolist.R
import com.tjackapps.besttodolist.ui.misc.displayedChild
import com.tjackapps.besttodolist.ui.misc.getViewModel
import com.tjackapps.besttodolist.ui.misc.plusAssign
import com.tjackapps.besttodolist.databinding.TaskFragmentBinding
import com.tjackapps.data.model.Task
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import joetr.com.swipereveal.SwipeToLeftCallback
import joetr.com.swipereveal.SwipeToRightCallback
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class TaskFragment : Fragment(), TaskBottomSheet.TaskSheetCallback, TaskItemCallback {

    companion object {
        private const val KEY_GROUP_ID = "group_id"
        private const val KEY_GROUP_NAME = "group_name"

        fun newInstance(
            groupId: Int,
            groupName: String
        ): TaskFragment {
            return TaskFragment().apply {
                arguments = Bundle().apply {
                    putInt(KEY_GROUP_ID, groupId)
                    putString(KEY_GROUP_NAME, groupName)
                }
            }
        }
    }

    private lateinit var binding: TaskFragmentBinding

    @Inject
    lateinit var viewModelProvider: Provider<TaskViewModel>

    private lateinit var viewModel: TaskViewModel

    private lateinit var taskAdapter: TaskAdapter

    private var compositeDisposable = CompositeDisposable()

    private var groupId: Int = -1

    private var groupName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            groupId = it.getInt(KEY_GROUP_ID)
            groupName = it.getString(KEY_GROUP_NAME) ?: ""
        }

        viewModel = requireActivity().getViewModel(viewModelProvider)
        viewModel.getTasksForGroup(groupId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TaskFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = groupName

        setOnClickListeners()
        subscribeToViewModel()
    }

    private fun setOnClickListeners() {
        binding.addTaskButton.setOnClickListener {
            TaskBottomSheet.newInstance(groupId).apply {
                setTargetFragment(this@TaskFragment, 0)
            }.show(requireActivity().supportFragmentManager, TaskBottomSheet::class.simpleName)
        }
    }

    private fun subscribeToViewModel() {
        compositeDisposable += viewModel.fragmentState()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                render(it)
            }
    }

    private fun render(pageState: TaskPageState) {
        when (pageState) {
            is TaskPageState.Loading -> {
                binding.layout.displayedChild(binding.loading)
            }
            is TaskPageState.Content -> {
                setupList(pageState.tasks)
                if (pageState.tasks.isEmpty()) {
                    binding.empty.isVisible = true
                    binding.taskList.isVisible = false
                } else {
                    binding.taskList.isVisible = true
                    binding.empty.isVisible = false
                }
                binding.layout.displayedChild(binding.content)
            }
            is TaskPageState.Error -> {
                // TODO log with timber here
                binding.layout.displayedChild(binding.error)
            }
        }
    }

    private fun setupList(tasks: List<Task>) {
        taskAdapter = TaskAdapter(
            this,
            tasks
        )

        binding.taskList.layoutManager = LinearLayoutManager(requireContext())
        binding.taskList.adapter = taskAdapter

        val swipeToLeftCallback = object : SwipeToLeftCallback(requireContext(), R.drawable.ic_delete, R.color.deleteBackground) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onDeleteClicked(viewHolder.adapterPosition)
            }
        }
        val swipeToLeftItemTouchHelper = ItemTouchHelper(swipeToLeftCallback)
        swipeToLeftItemTouchHelper.attachToRecyclerView(binding.taskList)

        val swipeToRightCallback = object : SwipeToRightCallback(requireContext(), R.drawable.ic_edit, R.color.editBackground) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onEditClicked(viewHolder.adapterPosition)
            }
        }
        val swipeToRightItemTouchHelper = ItemTouchHelper(swipeToRightCallback)
        swipeToRightItemTouchHelper.attachToRecyclerView(binding.taskList)
    }

    fun onEditClicked(index: Int) {
        taskAdapter.notifyItemChanged(index)
        TaskBottomSheet.newInstanceForEdit(viewModel.getTaskFromIndex(index)).apply {
            setTargetFragment(this@TaskFragment, 0)
        }.show(requireActivity().supportFragmentManager.beginTransaction(), TaskBottomSheet::class.simpleName)
    }

    fun onDeleteClicked(index: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_task_title))
            .setMessage(getString(R.string.delete_task_message, viewModel.getTaskFromIndex(index).name))
            .setPositiveButton("OK") { _, _ ->
                viewModel.deleteTask(viewModel.getTaskFromIndex(index))
            }
            .setNegativeButton("CANCEL") { _, _ ->
                taskAdapter.notifyItemChanged(index)
            }
            .setOnCancelListener {
                taskAdapter.notifyItemChanged(index)
            }
            .create()
            .show()
    }

    override fun onTaskSaved() {
        viewModel.getTasksForGroup(groupId)
    }

    override fun onTaskChecked(task: Task, index: Int, completed: Boolean) {
        if (task.completed != completed) {
            val title: String
            val message: String

            if (completed) {
                title = getString(R.string.task_complete_title)
                message = getString(R.string.task_complete_message, task.name)
            } else {
                title = getString(R.string.task_reset_title)
                message = getString(R.string.task_reset_message, task.name)
            }

            AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK") { _, _ ->
                    // update task in database
                    viewModel.completeTask(task.taskId, completed)
                }
                .setNegativeButton("CANCEL") { _, _ ->
                    // reset check state to match data
                    taskAdapter.notifyItemChanged(index)
                }
                .create()
                .show()
        } else {
            Timber.e(TaskError.TASK_COMPLETED_STATE_FAILURE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}