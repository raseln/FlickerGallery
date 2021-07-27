package com.rasel.flickergallery.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.rasel.flickergallery.data.models.Item
import com.rasel.flickergallery.databinding.ActivityDetailBinding
import com.rasel.flickergallery.utils.ToasterUtils.showToast
import com.rasel.flickergallery.viewmodels.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    val viewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val item = intent.getParcelableExtra<Item>(ITEM_KEY)
        if (item == null) {
            showToast(this, "Something went wrong! Try again")
            finish()
            return
        }

        binding.lifecycleOwner = this
        viewModel.item.value = item
        binding.viewModel = viewModel
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val ITEM_KEY: String = "item_key"
    }
}