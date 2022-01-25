package com.codingnight.example.gallery

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class GalleryFragment : Fragment() {

    private lateinit var swipeLayoutGallery: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView

    private val galleryViewModel by activityViewModels<GalleryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        recyclerView = root.findViewById(R.id.recyclerView)
        swipeLayoutGallery = root.findViewById(R.id.swipeLayoutGallery)
        return root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.swipeIndicator -> {
                swipeLayoutGallery.isRefreshing = true
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
        recyclerView.apply {
            adapter = galleryAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        galleryViewModel.pageListLiveData.observe(viewLifecycleOwner, {
            galleryAdapter.submitList(it)
        })

        swipeLayoutGallery.setOnRefreshListener {
            Handler(Looper.myLooper()!!).postDelayed({galleryViewModel.resetQuery() },1000)
        }

        galleryViewModel.networkStatus.observe((viewLifecycleOwner), {
            Log.d("GalleryFragment", "onViewCreated + $it ")
            galleryAdapter.updateNetworkStatus(it)
            swipeLayoutGallery.isRefreshing = it == NetworkStatus.INITIAL_LOADING
        })
    }
}