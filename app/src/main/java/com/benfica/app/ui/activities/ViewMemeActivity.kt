package com.benfica.app.ui.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import com.benfica.app.R
import com.benfica.app.ui.base.BaseActivity
import com.benfica.app.utils.Constants
import com.benfica.app.utils.load
import kotlinx.android.synthetic.main.activity_view_meme.*
import android.view.WindowManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.benfica.app.utils.AppUtils


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
