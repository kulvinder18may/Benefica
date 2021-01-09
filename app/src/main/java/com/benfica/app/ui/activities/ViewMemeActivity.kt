package com.panchayat.takoli.ui.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import com.panchayat.takoli.R
import com.panchayat.takoli.ui.base.BaseActivity
import com.panchayat.takoli.utils.Constants
import com.panchayat.takoli.utils.load
import kotlinx.android.synthetic.main.activity_view_meme.*
import android.view.WindowManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.panchayat.takoli.utils.AppUtils


class ViewMemeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDarkStatusBar()
        setContentView(R.layout.activity_view_meme)

        val image = BitmapFactory.decodeStream(openFileInput("image"))
        viewMemeImage.setImageBitmap(image)

        val url = intent.getStringExtra(Constants.PIC_URL)
        url?.let { viewMemeImage.load(it, R.drawable.loading) }

        val caption = intent.getStringExtra(Constants.CAPTION)
        if (!caption.isNullOrEmpty()) {
            memeCaptionText.text = caption

            viewMemeImage.setOnClickListener {
                if (memeCaption.isShown)
                    memeCaption.visibility = View.GONE
                else
                    memeCaption.visibility = View.VISIBLE
            }
        } else {
            memeCaption.visibility = View.GONE
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.slideLeft(this)
    }
}
