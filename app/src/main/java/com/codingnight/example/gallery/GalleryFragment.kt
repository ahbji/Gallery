package com.codingnight.example.gallery

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.codingnight.example.gallery.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    private lateinit var binding: FragmentGalleryBinding

    private val galleryViewModel by activityViewModels<GalleryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGalleryBinding.inflate(inflater)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.swipeIndicator -> {
                binding.swipeLayoutGallery.isRefreshing = true
                Handler(Looper.myLooper()!!).postDelayed({galleryViewModel.resetQuery() },1000)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu,menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        val galleryAdapter = GalleryAdapter(galleryViewModel)
        binding.recyclerView.apply {
            adapter = galleryAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        galleryViewModel.pageListLiveData.observe(viewLifecycleOwner) {
            galleryAdapter.submitList(it)
        }

        binding.swipeLayoutGallery.setOnRefreshListener {
            Handler(Looper.myLooper()!!).postDelayed({galleryViewModel.resetQuery() },1000)
        }

        galleryViewModel.networkStatus.observe((viewLifecycleOwner)) {
            Log.d("GalleryFragment", "onViewCreated + $it ")
            galleryAdapter.updateNetworkStatus(it)
            binding.swipeLayoutGallery.isRefreshing = it == NetworkStatus.INITIAL_LOADING
        }
    }
}