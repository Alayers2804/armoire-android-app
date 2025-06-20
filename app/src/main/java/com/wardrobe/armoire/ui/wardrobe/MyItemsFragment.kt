package com.wardrobe.armoire.ui.wardrobe

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.wardrobe.armoire.R
import com.wardrobe.armoire.databinding.FragmentMyItemsBinding
import com.wardrobe.armoire.model.wardrobe.WardrobeModel
import java.io.File
import java.io.FileOutputStream

class MyItemsFragment : Fragment() {

    private var _binding: FragmentMyItemsBinding? = null
    private val binding get() = _binding!!

    private val wardrobeViewmodel: WardrobeViewmodel by activityViewModels()
    private lateinit var wardrobeAdapter: WardrobeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wardrobeAdapter = WardrobeAdapter(
            emptyList(),
            onClick = { uid ->
                Toast.makeText(requireContext(), "Clicked item UID: $uid", Toast.LENGTH_SHORT).show()
            }
        )
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = wardrobeAdapter
        }

        wardrobeViewmodel.wardrobeMyitems.observe(viewLifecycleOwner) {
            wardrobeAdapter.updateData(it)
            binding.itemCount.text = "MyItems (${it.size})"
        }

        wardrobeViewmodel.fetchWardrobeByStatus("my_item")

        binding.buttonAddWardrobe.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            handleImageSelection(uri)
        }
    }

    private fun handleImageSelection(uri: Uri) {
        val context = requireContext()

        // Step 1: Build the EditText input
        val input = EditText(context).apply {
            hint = "Describe this wardrobe item"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            setPadding(32, 32, 32, 32)
        }

        // Step 2: Show input dialog
        AlertDialog.Builder(context)
            .setTitle("Add Description")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val description = input.text.toString().ifBlank { "No description" }

                // Step 3: Copy image to internal storage
                val inputStream = context.contentResolver.openInputStream(uri)
                val fileName = "wardrobe_${System.currentTimeMillis()}.jpg"
                val file = File(context.filesDir, fileName)

                inputStream?.use { inStream ->
                    FileOutputStream(file).use { outStream ->
                        inStream.copyTo(outStream)
                    }
                }

                // Step 4: Create and insert WardrobeModel
                val wardrobe = WardrobeModel(
                    path = file.absolutePath,
                    description = description,
                    status = "my_item"
                )
                wardrobeViewmodel.insertWardrobeItem(wardrobe)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
