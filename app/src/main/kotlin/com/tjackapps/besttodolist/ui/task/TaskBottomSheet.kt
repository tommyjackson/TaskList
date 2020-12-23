package com.tjackapps.besttodolist.ui.task

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tjackapps.besttodolist.R
import com.tjackapps.besttodolist.helper.extensions.getViewModel
import com.tjackapps.besttodolist.helper.extensions.plusAssign
import com.tjackapps.data.model.Priority
import com.tjackapps.data.model.Task
import com.tjackapps.besttodolist.databinding.TaskSheetBinding
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class TaskBottomSheet : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener {

    internal interface TaskSheetCallback {
        fun onTaskSaved()
    }

    @Inject
    lateinit var viewModelProvider: Provider<TaskViewModel>

    private lateinit var viewModel: TaskViewModel

    private lateinit var binding: TaskSheetBinding

    private var groupId: Int = -1

    private var taskToEdit: Task? = null

    private var compositeDisposable = CompositeDisposable()

    private var selectedPriority: Priority = Priority.LOW

    companion object {
        private const val KEY_GROUP_ID = "group_id"
        private const val KEY_TASK_TO_EDIT = "task_to_edit"

        fun newInstance(groupId: Int = -1): TaskBottomSheet {
            return TaskBottomSheet().apply {
                arguments = Bundle().apply {
                    putInt(KEY_GROUP_ID, groupId)
                }
            }
        }

        fun newInstanceForEdit(task: Task): TaskBottomSheet {
            return TaskBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_TASK_TO_EDIT, task)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = requireActivity().getViewModel(viewModelProvider)
        arguments?.let {
            taskToEdit = it.getParcelable(KEY_TASK_TO_EDIT)

            groupId = if (taskToEdit != null) {
                -2
            } else {
                it.getInt(KEY_GROUP_ID)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.setOnShowListener {
            Handler().post {
                val bottomSheet = (dialog as? BottomSheetDialog)?.findViewById<View>(R.id.design_bottom_sheet) as? FrameLayout
                bottomSheet?.let {
                    BottomSheetBehavior.from(it).state = STATE_EXPANDED
                }
            }
        }

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TaskSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSheet()
        setOnClickListeners()
        subscribeToViewModel()
    }

    private fun setupSheet() {
        with (binding) {
            prioritySelector.onItemSelectedListener = this@TaskBottomSheet

            ArrayAdapter.createFromResource(
                    requireContext(),
                    R.array.priority_options,
                    android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                prioritySelector.adapter = adapter
            }

            if (taskToEdit != null) {
                title.text = getString(R.string.edit_task)
                nameEntry.setText(taskToEdit?.name)
                descriptionEntry.setText(taskToEdit?.description)
                prioritySelector.setSelection(Priority.values().indexOf(taskToEdit?.priority))
            }
        }
    }

    private fun setOnClickListeners() {
        binding.closeButton.setOnClickListener {
            dismiss()
        }

        binding.saveButton.setOnClickListener {
            saveTask()
        }
    }

    private fun subscribeToViewModel() {
        compositeDisposable += viewModel.sheetAction()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                showErrorDialog(
                    getString(R.string.task_alert_title),
                    getString(R.string.alert_error_message)
                )
            }
            .subscribe {
                handle(it)
            }
    }

    private fun handle(action: TaskSheetAction) {
        when (action) {
            is TaskSheetAction.SaveTaskSuccess -> {
                if (targetFragment != null) {
                    val callback = targetFragment as? TaskSheetCallback
                    callback?.onTaskSaved()
                    dismiss()
                } else {
                    showErrorDialog(
                        getString(R.string.alert_error_title),
                        getString(R.string.alert_error_message)
                    )
                }
            }
            is TaskSheetAction.SaveTaskFailure -> {
                showErrorDialog(
                    getString(R.string.task_alert_title),
                    getString(R.string.alert_error_message)
                )
            }
        }
    }

    private fun saveTask() {
        with (binding) {
            val name = nameEntry.text.toString()
            val description = descriptionEntry.text.toString()

            if (name.isNotBlank() && description.isNotBlank() && groupId != -1) {

                if (taskToEdit != null) {
                    val editTask = Task(
                        taskId = taskToEdit?.taskId ?: 0,
                        groupId = taskToEdit?.groupId ?: groupId,
                        name = name,
                        description = description,
                        completed = taskToEdit?.completed ?: false,
                        priority = selectedPriority
                    )

                    viewModel.updateTask(editTask)
                } else {
                    val addTask = Task(
                        groupId = groupId,
                        name = name,
                        description = description,
                        completed = false,
                        priority = selectedPriority
                    )

                    viewModel.addTask(addTask)
                }
            } else {
                showErrorDialog(
                    getString(R.string.task_alert_title),
                    getString(R.string.fields_alert_message)
                )
            }
        }
    }

    private fun showErrorDialog(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setNeutralButton(getString(R.string.ok), null)
            .create()
            .show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedPriority = Priority.values()[position]
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        // noop
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}