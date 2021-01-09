package com.panchayat.takoli.ui.activities

import android.os.Bundle
import androidx.lifecycle.Observer
import com.panchayat.takoli.R
import com.panchayat.takoli.ui.base.BaseActivity
import com.panchayat.takoli.ui.viewmodels.MemesViewModel
import com.panchayat.takoli.utils.Constants
import kotlinx.android.synthetic.main.content_video_play.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoPlayActivity : BaseActivity() {
    private val memesViewModel: MemesViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)
       // setSupportActionBar(toolbar)
    showLoading("Please wait")
        initResponseObserver()
        memesViewModel.getvideoUrl(intent.getStringExtra(Constants.PIC_URL))

      //  video_view.setSource(intent.getStringExtra(Constants.PIC_URL));
    }
    private fun initResponseObserver() {
        memesViewModel.urlResponse.observe(this, Observer {
            hideLoading()
            video_view.setSource(it.toString());
        })
    }

    override fun onBackPressed() {
        finish()
    }

}

