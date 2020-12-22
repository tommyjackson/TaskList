package com.tjackapps.besttodolist.ui.group

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tjackapps.besttodolist.R
import com.tjackapps.besttodolist.ui.misc.getViewModel
import com.tjackapps.besttodolist.ui.misc.plusAssign
import com.tjackapps.data.model.Group
import com.tjackapps.besttodolist.databinding.GroupSheetBinding
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class GroupBottomSheet : BottomSheetDialogFragment() {

    internal interface GroupSheetCallback {
        fun onGroupSaved()
    }

    @Inject
    lateinit var viewModelProvider: Provider<GroupViewModel>

    private lateinit var viewModel: GroupViewModel

    private lateinit var binding: GroupSheetBinding

    private var compositeDisposable = CompositeDisposable()

    private var isEditSheet = false

    private var groupToEdit: Group? = null

    companion object {
        private const val KEY_IS_EDIT_SHEET = "is_edit_sheet"
        private const val KEY_GROUP_TO_EDIT = "group_to_edit"

        fun newInstance() = GroupBottomSheet()

        fun newInstanceForEdit(group: Group): GroupBottomSheet {
            return GroupBottomSheet().apply {
                arguments = Bundle().apply {
                    putBoolean(KEY_IS_EDIT_SHEET, true)
                    putParcelable(KEY_GROUP_TO_EDIT, group)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = requireActivity().getViewModel(viewModelProvider)

        arguments?.let {
            isEditSheet = it.getBoolean(KEY_IS_EDIT_SHEET)
            groupToEdit = it.getParcelable(KEY_GROUP_TO_EDIT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GroupSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isEditSheet && groupToEdit != null) {
            setupEditSheet()
        }

        setOnClickListeners()
        subscribeToViewModel()
    }

    private fun setOnClickListeners() {
        binding.closeButton.setOnClickListener {
            dismiss()
        }

        binding.saveButton.setOnClickListener {
            saveGroup()
        }
    }

    private fun subscribeToViewModel() {
        compositeDisposable += viewModel.sheetAction()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handle(it)
            }
    }

    private fun handle(action: GroupSheetAction) {
        when (action) {
            is GroupSheetAction.SaveGroupSuccess -> {
                if (targetFragment != null) {
                    val callback = targetFragment as? GroupSheetCallback
                    callback?.onGroupSaved()
                    dismiss()
                }
            }
            is GroupSheetAction.SaveGroupFailure -> {
                showErrorDialog(
                    getString(R.string.group_alert_title),
                    getString(R.string.alert_error_message)
                )
            }
        }
    }

    private fun saveGroup() {
        with (binding) {
            val name = nameEntry.text.toString()
            val description = descriptionEntry.text.toString()

            if (name.isNotBlank() && description.isNotBlank()) {

                if (isEditSheet) {
                    val editGroup = Group(
                        groupId = groupToEdit?.groupId ?: 0,
                        name = name,
                        description = description
                    )

                    viewModel.updateGroup(editGroup)
                } else {
                    val addGroup = Group(
                        name = name,
                        description = description
                    )

                    viewModel.addGroup(addGroup)
                }
            } else {
                showErrorDialog(
                    getString(R.string.group_alert_title),
                    getString(R.string.fields_alert_message)
                )
            }
        }
    }

    private fun setupEditSheet() {
        binding.title.text = getString(R.string.edit_group)
        binding.nameEntry.setText(groupToEdit?.name)
        binding.descriptionEntry.setText(groupToEdit?.description)
    }

    private fun showErrorDialog(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setNeutralButton("OK", null)
            .create()
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}