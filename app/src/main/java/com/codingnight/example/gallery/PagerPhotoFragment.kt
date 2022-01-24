package com.codingnight.example.gallery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2

class PagerPhotoFragment : Fragment() {
    private lateinit var viewPager: ViewPager2
    private lateinit var photoTag: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_pager_photo, container, false)
        viewPager = root.findViewById(R.id.photoViewPager)
        photoTag = root.findViewById(R.id.page_photo_tag)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val photoList = arguments?.getParcelableArrayList<PhotoItem>("PHOTO_LIST")
        PagerPhotoListAdapter().apply {
            viewPager.adapter = this
            submitList(photoList)
        }

        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (photoList != null) {
                    photoTag.text = "${position + 1}/${photoList.size}"
                }
            }
        })

        arguments?.getInt("PHOTO_POSITION")?.let { viewPager.setCurrentItem(it, false) }
    }
}