package com.survivalcoding.noteapp.presentation.add_edit_note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.survivalcoding.noteapp.App
import com.survivalcoding.noteapp.databinding.FragmentAddEditNoteBinding
import com.survivalcoding.noteapp.presentation.notes.NotesViewModel
import com.survivalcoding.noteapp.presentation.notes.NotesViewModelFactory

class AddEditNoteFragment : Fragment() {
    private val viewModel by viewModels<AddEditNoteViewModel> {
        AddEditNoteViewModelFactory(
            notesRepository = (requireActivity().application as App).notesRepository,
            owner = this,
            defaultArgs = arguments
        )
    }

    private val activityViewModel by activityViewModels<NotesViewModel> {
        NotesViewModelFactory(
            application = requireActivity().application,
            notesRepository = (requireActivity().application as App).notesRepository
        )
    }

    private var _binding: FragmentAddEditNoteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.addEditNote.observe(this) {}
        val title = binding.titleEditText
        title.setText(viewModel.getNote().title)
        val content = binding.contentEditText
        content.setText(viewModel.getNote().content)

        val saveButton = binding.saveButton
        saveButton.setOnClickListener {
            val titleText = title.text.toString()
            if (titleText == "") {
                Snackbar.make(view, "제목이 존재하지 않습니다.", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val contentText = content.text.toString()
            if (contentText == "") {
                Snackbar.make(view, "내용이 존재하지 않습니다.", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            activityViewModel.insertNote(
                viewModel.getNote().copy(title = titleText, content = contentText)
            )
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}