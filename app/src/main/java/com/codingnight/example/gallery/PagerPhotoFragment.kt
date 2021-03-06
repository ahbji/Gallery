package com.codingnight.example.gallery

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val REQUEST_WRITE_EXTERNAL_STORAGE = 1

class PagerPhotoFragment : Fragment() {
    val galleryViewModel by activityViewModels<GalleryViewModel>()

    private lateinit var viewPager: ViewPager2
    private lateinit var photoTag: TextView
    private lateinit var saveButton: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_pager_photo, container, false)
        viewPager = root.findViewById(R.id.viewPager2)
        photoTag = root.findViewById(R.id.photoTag)
        saveButton = root.findViewById(R.id.saveButton)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = PagerPhotoListAdapter()
        viewPager.adapter = adapter

        galleryViewModel.pageListLiveData.observe(viewLifecycleOwner, { pagedList ->
            adapter.submitList(pagedList)
            arguments?.getInt("PHOTO_POSITION")?.let { viewPager.setCurrentItem(it, false) }
        })

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                photoTag.text = getString(
                    R.string.photo_tag,
                    position + 1,
                    galleryViewModel.pageListLiveData.value?.size
                )
            }
        })

        saveButton.setOnClickListener {
            if (Build.VERSION.SDK_INT < 29 && ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_EXTERNAL_STORAGE
                )
            } else {
                viewLifecycleOwner.lifecycleScope.launch {
                    savePhoto()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        savePhoto()
                    }
                } else {
                    Toast.makeText(requireContext(), "????????????", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun savePhoto() {
        withContext(Dispatchers.IO) {
            val holder =
                (viewPager[0] as RecyclerView).findViewHolderForAdapterPosition(viewPager.currentItem)
                        as PagerPhotoViewHolder
            val bitmap = holder.imageView.drawable.toBitmap()

            val saveUri = requireContext().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ContentValues()
            ) ?: kotlin.run {
                Toast.makeText(requireContext(), "????????????", Toast.LENGTH_SHORT).show()
                return@withContext
            }
            requireContext().contentResolver.openOutputStream(saveUri).use {
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)) {
                    MainScope().launch {
                        Toast.makeText(
                            requireContext(),
                            "????????????",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    MainScope().launch {
                        Toast.makeText(
                            requireContext(),
                            "????????????",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}