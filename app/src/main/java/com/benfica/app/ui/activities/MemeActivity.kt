package com.panchayat.takoli.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.panchayat.takoli.R
import com.panchayat.takoli.data.Status
import com.panchayat.takoli.data.models.Meme
import com.panchayat.takoli.data.models.User
import com.panchayat.takoli.databinding.ActivityMemeBinding
import com.panchayat.takoli.ui.base.BaseActivity
import com.panchayat.takoli.ui.callbacks.MemesCallback
import com.panchayat.takoli.ui.viewmodels.MemesViewModel
import com.panchayat.takoli.utils.AppUtils
import com.panchayat.takoli.utils.Constants
import com.panchayat.takoli.utils.setDrawable
import com.mikepenz.fontawesome_typeface_library.FontAwesome
import com.mikepenz.ionicons_typeface_library.Ionicons
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_meme.*
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import timber.log.Timber

class MemeActivity : BaseActivity() {
    private lateinit var binding: ActivityMemeBinding
    private lateinit var memeId: String
    private lateinit var meme: Meme
    private val memesViewModel: MemesViewModel by inject()
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDarkStatusBar()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_meme)
        binding.lifecycleOwner = this
        binding.callback = memesCallback

        memeId = intent.getStringExtra(Constants.MEME_ID)!!

        initViews()
        initMemeObserver()
        memesViewModel.fetchMeme(memeId)
    }

    private fun initViews() {
        setSupportActionBar(memeToolbar)
        supportActionBar?.title = null
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        memeToolbar.navigationIcon = AppUtils.getDrawable(this, Ionicons.Icon.ion_android_arrow_back, R.color.white, 18)
        memeToolbar?.setNavigationOnClickListener { onBackPressed() }

        memeComment.setDrawable(AppUtils.getDrawable(this, Ionicons.Icon.ion_ios_chatboxes_outline, R.color.color_text_secondary, 20))
        memeComment.setDrawable(AppUtils.getDrawable(this, FontAwesome.Icon.faw_thumbs_up, R.color.color_text_secondary, 20))

        avatar.setOnClickListener {
            if (::meme.isInitialized && meme.memePosterID!! != getUid()) {
                val i = Intent(this, ProfileActivity::class.java)
                i.putExtra(Constants.USER_ID, meme.memePosterID)
                startActivity(i)
                AppUtils.slideRight(this)
            }
        }
    }

    /**
     * Initialize observer for Meme LiveData
     */
    private fun initMemeObserver() {
        memesViewModel.memeResponseLiveData.observe(this, Observer { response ->
            when (response.status) {
                Status.LOADING -> binding.loading = true

                Status.SUCCESS -> {
                    binding.loading = false
                    response.data?.meme?.subscribeBy(
                            onNext = {
                                binding.meme = it
                                meme = it
                            },
                            onError = {
                                Timber.e("Meme deleted")
                                toast("Error loading meme")
                                onBackPressed()
                            }
                    )?.addTo(disposables)
                }

                Status.ERROR -> {
                    binding.loading = false
                    onBackPressed()
                }
            }
        })
    }

    private val memesCallback = object : MemesCallback {
        override fun onMemeClicked(view: View, meme: Meme) {
            val memeId = meme.id!!

            when(view.id) {
                R.id.memeComment -> {
                    val i = Intent(this@MemeActivity, CommentActivity::class.java)
                    i.putExtra(Constants.MEME_ID, memeId)
                    startActivity(i)
                    AppUtils.slideRight(this@MemeActivity)
                }

                R.id.memeFave -> {
                    AppUtils.animateView(view)
                    memesViewModel.faveMeme(memeId, getUid())
                }

                R.id.memeLike -> {
                    AppUtils.animateView(view)
                    memesViewModel.likeMeme(memeId, getUid())
                }

                else -> Timber.e("No action")
            }
        }

        override fun onProfileClicked(view: View, user: User) {
            // Not used
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        AppUtils.slideLeft(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}
