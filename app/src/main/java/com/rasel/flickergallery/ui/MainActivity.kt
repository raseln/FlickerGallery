package com.rasel.flickergallery.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.rasel.flickergallery.R
import com.rasel.flickergallery.adapters.ImageGalleryAdapter
import com.rasel.flickergallery.databinding.ActivityMainBinding
import com.rasel.flickergallery.utils.ToasterUtils.showToast
import com.rasel.flickergallery.viewmodels.MainViewModel
import com.rasel.flickergallery.viewmodels.ViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: ViewModelFactory

    private val viewModel: MainViewModel by viewModels {
        factory
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var imageAdapter: ImageGalleryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        //Initialize recyclerView with empty data
        setRecyclerview()
        setupObservers()

        //Get default images on init
        viewModel.getImages()

        binding.swipeRefresh.setOnRefreshListener {
            val query: String = binding.searchView.query.toString()
            viewModel.getImages(if (query.isEmpty()) null else query)
        }
    }

    private fun setRecyclerview() {
        //To display image in Gallery style with two image per row
        val layoutManager = GridLayoutManager(this, 2)
        imageAdapter = ImageGalleryAdapter().apply {
            onItemClick = { item ->
                run {
                    val intent = Intent(this@MainActivity, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.ITEM_KEY, item)
                    startActivity(intent)
                }
            }
        }

        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = imageAdapter
    }

    private fun setupObservers() {
        viewModel.error.observe(this, { error ->
            binding.swipeRefresh.isRefreshing = false
            viewModel.isLoading.value = false
            if (!error.isNullOrEmpty()) {
                showToast(this, error)
            }
        })

        viewModel.imageListLiveData.observe(this, { response ->
            binding.swipeRefresh.isRefreshing = false
            viewModel.isLoading.value = false
            imageAdapter.list = response?.items?.sortedBy { it.dateTaken } ?: listOf()
            imageAdapter.notifyDataSetChanged()
        })
    }
}