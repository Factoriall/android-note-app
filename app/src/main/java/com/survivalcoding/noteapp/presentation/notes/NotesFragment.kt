package com.survivalcoding.noteapp.presentation.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.survivalcoding.noteapp.App
import com.survivalcoding.noteapp.R
import com.survivalcoding.noteapp.databinding.FragmentNotesBinding
import com.survivalcoding.noteapp.presentation.add_edit_note.AddEditNoteFragment
import com.survivalcoding.noteapp.presentation.notes.adapter.NoteListAdapter
import com.survivalcoding.noteapp.presentation.order2Id
import com.survivalcoding.noteapp.presentation.sortBy2Id


class NotesFragment : Fragment() {
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<NotesViewModel> {
        NotesViewModelFactory(
            application = requireActivity().application,
            notesRepository = (requireActivity().application as App).notesRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.recyclerView
        val adapter = NoteListAdapter(onClickDeleteButton = { note ->
            viewModel.deleteNote(note)
            Snackbar.make(view, "노트 삭제", Snackbar.LENGTH_LONG)
                .setAction("되돌리기") { viewModel.insertNote(note) }
                .show()
        }, onClickView = {
            moveToAddEditNoteFragment(it.id)
        })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val alignButton = binding.alignButton
        val filterLayout = binding.filterLayout
        alignButton.setOnClickListener {
            if (filterLayout.visibility == VISIBLE) filterLayout.visibility = GONE
            else filterLayout.visibility = VISIBLE
        }

        val sortByRadioGroup = binding.sortByRadioGroup
        val orderRadioGroup = binding.orderRadioGroup

        sortByRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            viewModel.updateFilter(checkedId)
            viewModel.sortNotes()
        }
        orderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            viewModel.updateSort(checkedId)
            viewModel.sortNotes()
        }

        viewModel.uiState.observe(this) {
            adapter.submitList(it.notes)
            sortByRadioGroup.check(sortBy2Id(it.sortBy))
            orderRadioGroup.check(order2Id(it.order))
        }

        val addButton = binding.addButton
        addButton.setOnClickListener {
            moveToAddEditNoteFragment()
        }
    }


    private fun moveToAddEditNoteFragment(id: Int = -1) {
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container_view,
                AddEditNoteFragment().apply {
                    this.arguments = bundleOf(MODIFY to id)
                })
            .addToBackStack(null)
            .commit()
    }

    companion object {
        const val MODIFY = "modify"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}