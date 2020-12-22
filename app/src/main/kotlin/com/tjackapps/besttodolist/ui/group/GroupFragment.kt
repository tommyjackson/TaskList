package com.tjackapps.besttodolist.ui.group

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
import com.tjackapps.besttodolist.databinding.GroupFragmentBinding
import com.tjackapps.besttodolist.ui.misc.*
import com.tjackapps.besttodolist.ui.task.TaskFragment
import com.tjackapps.data.model.Group
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import joetr.com.swipereveal.SwipeToLeftCallback
import joetr.com.swipereveal.SwipeToRightCallback
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class GroupFragment : Fragment(), GroupItemCallback, GroupBottomSheet.GroupSheetCallback {

    companion object {
        fun newInstance() = GroupFragment()
    }

    private lateinit var binding: GroupFragmentBinding

    private lateinit var viewModel: GroupViewModel

    @Inject
    lateinit var viewModelProvider: Provider<GroupViewModel>

    private lateinit var groupAdapter: GroupAdapter

    private var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = requireActivity().getViewModel(viewModelProvider)
        viewModel.getGroups()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = GroupFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setTitle(R.string.action_bar_groups)

        setOnClickListeners()
        subscribeToViewModel()
    }

    private fun setOnClickListeners() {
        binding.addGroupButton.setOnClickListener {
            GroupBottomSheet.newInstance().apply {
                setTargetFragment(this@GroupFragment, 0)
            }.show(requireActivity().supportFragmentManager.beginTransaction(), GroupBottomSheet::class.simpleName)
        }
    }

    private fun subscribeToViewModel() {
        compositeDisposable += viewModel.fragmentState()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    render(it)
                }
    }

    private fun render(pageState: GroupPageState) {
        when (pageState) {
            is GroupPageState.Loading -> {
                binding.layout.displayedChild(binding.loading)
            }
            is GroupPageState.Content -> {
                setupList(pageState.groups)
                if (pageState.groups.isEmpty()) {
                    binding.empty.isVisible = true
                    binding.groupList.isVisible = false
                } else {
                    binding.groupList.isVisible = true
                    binding.empty.isVisible = false
                }
                binding.layout.displayedChild(binding.content)
            }
            is GroupPageState.Error -> {
                binding.layout.displayedChild(binding.error)
            }
        }
    }

    private fun setupList(groups: List<Group>) {
        groupAdapter = GroupAdapter(
                this,
                groups
        )

        binding.groupList.layoutManager = LinearLayoutManager(requireContext())
        binding.groupList.adapter = groupAdapter

        val swipeToLeftCallback = object : SwipeToLeftCallback(requireContext(), R.drawable.ic_delete, R.color.deleteBackground) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onDeleteClicked(viewHolder.adapterPosition)
            }
        }
        val swipeToLeftItemTouchHelper = ItemTouchHelper(swipeToLeftCallback)
        swipeToLeftItemTouchHelper.attachToRecyclerView(binding.groupList)

        val swipeToRightCallback = object : SwipeToRightCallback(requireContext(), R.drawable.ic_edit, R.color.editBackground) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onEditClicked(viewHolder.adapterPosition)
            }
        }
        val swipeToRightItemTouchHelper = ItemTouchHelper(swipeToRightCallback)
        swipeToRightItemTouchHelper.attachToRecyclerView(binding.groupList)
    }

    override fun onGroupClicked(group: Group) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.container, TaskFragment.newInstance(group.groupId, group.name))
        transaction.addToBackStack(TaskFragment::class.simpleName)
        transaction.commit()
    }
    fun onEditClicked(index: Int) {
        groupAdapter.notifyItemChanged(index)
        GroupBottomSheet.newInstanceForEdit(viewModel.getGroupFromIndex(index)).apply {
            setTargetFragment(this@GroupFragment, 0)
        }.show(requireActivity().supportFragmentManager.beginTransaction(), GroupBottomSheet::class.simpleName)
    }

    fun onDeleteClicked(index: Int) {
        AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.delete_group_title))
                .setMessage(getString(R.string.delete_group_message, viewModel.getGroupFromIndex(index).name))
                .setPositiveButton("OK") { _, _ ->
                    viewModel.deleteGroup(viewModel.getGroupFromIndex(index))
                }
                .setNegativeButton("CANCEL") { _, _ ->
                    groupAdapter.notifyItemChanged(index)
                }
                .setOnCancelListener {
                    groupAdapter.notifyItemChanged(index)
                }
                .create()
                .show()
    }

    override fun onGroupSaved() {
        viewModel.getGroups()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

}