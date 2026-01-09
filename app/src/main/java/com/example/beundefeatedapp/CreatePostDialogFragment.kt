package com.example.beundefeatedapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class CreatePostDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_create_post, container, false)

        val shareButton = view.findViewById<View>(R.id.share_button)
        val btnAddImage = view.findViewById<ImageButton>(R.id.btn_add_image)
        val btnAddVideo = view.findViewById<ImageButton>(R.id.btn_add_video)
        val tagInput = view.findViewById<EditText>(R.id.tag_input)
        val postTextInput = view.findViewById<EditText>(R.id.post_text_input)
        val tagsChipGroup = view.findViewById<ChipGroup>(R.id.tags_chip_group)

        btnAddImage.setOnClickListener {
            Toast.makeText(context, "Select Image from Gallery", Toast.LENGTH_SHORT).show()
        }

        btnAddVideo.setOnClickListener {
            Toast.makeText(context, "Select Video from Gallery", Toast.LENGTH_SHORT).show()
        }

        shareButton.setOnClickListener {
            val content = postTextInput.text.toString()
            if (content.isBlank()) {
                Toast.makeText(context, "Please write something...", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Capture the activity before dismissing the dialog
            val homeActivity = activity as? HomeActivity
            
            Toast.makeText(context, "Sharing in 5 seconds...", Toast.LENGTH_LONG).show()
            
            // Wait 5 seconds then add to home page
            Handler(Looper.getMainLooper()).postDelayed({
                homeActivity?.addPost(content)
            }, 5000)
            
            dismiss()
        }

        tagInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString()
                if (text.endsWith(" ") || text.endsWith("\n")) {
                    val tag = text.trim()
                    if (tag.isNotEmpty()) {
                        addTagChip(tag, tagsChipGroup)
                        tagInput.setText("")
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }

    private fun addTagChip(text: String, chipGroup: ChipGroup) {
        val chip = Chip(context, null, com.google.android.material.R.style.Widget_Material3_Chip_Input)
        chip.text = text
        chip.isCloseIconVisible = true
        chip.setChipBackgroundColorResource(R.color.orange)
        chip.setTextColor(resources.getColor(android.R.color.white, null))
        chip.setOnCloseIconClickListener {
            chipGroup.removeView(chip)
        }
        chipGroup.addView(chip)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
