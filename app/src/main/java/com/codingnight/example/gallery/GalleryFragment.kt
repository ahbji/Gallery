package com.codingnight.example.gallery

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class GalleryFragment : Fragment() {

    private lateinit var swipeLayoutGallery: SwipeRefreshLayout
    private lateinit var galleryViewModel: GalleryViewModel
    private lateinit var recyclerView: RecyclerView

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
        when (item.itemId) {
            R.id.swipeIndicator -> {
                swipeLayoutGallery.isRefreshing = true
                Handler(Looper.myLooper()!!).postDelayed({ galleryViewModel.resetQuery() }, 1000)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        galleryViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        ).get(GalleryViewModel::class.java)
        val galleryAdapter = GalleryAdapter(galleryViewModel)

        recyclerView.apply {
            adapter = galleryAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        galleryViewModel.photoListLive.observe(viewLifecycleOwner, {
            if (galleryViewModel.needToScrollToTop) {
                recyclerView.scrollToPosition(0)
                galleryViewModel.needToScrollToTop = false
            }
            galleryAdapter.submitList(it)
            swipeLayoutGallery.isRefreshing = false
        })
        galleryViewModel.dataStatusLive.observe(viewLifecycleOwner, {
            galleryAdapter.footerViewStatus = it
            galleryAdapter.notifyItemChanged(galleryAdapter.itemCount - 1)
            if (it == DATA_STATUS_NETWORK_ERROR)
                swipeLayoutGallery.isRefreshing = false
        })

        swipeLayoutGallery.setOnRefreshListener {
            galleryViewModel.resetQuery()
        }

        recyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy < 0) return
                    val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
                    val into = IntArray(2)
                    layoutManager.findLastVisibleItemPositions(into)
                    if (into[0] == galleryAdapter.itemCount - 1) {
                        galleryViewModel.fetchData()
                    }
                }
            }
        )
    }
}