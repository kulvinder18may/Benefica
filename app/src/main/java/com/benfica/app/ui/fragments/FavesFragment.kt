package com.mysqldatabase.app.ui.fragments


import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.mysqldatabase.app.R
import com.mysqldatabase.app.data.Status
import com.mysqldatabase.app.data.events.PostMemeEvent
import com.mysqldatabase.app.data.models.Fave
import com.mysqldatabase.app.databinding.FragmentFavesBinding
import com.mysqldatabase.app.ui.activities.VideoPlayActivity
import com.mysqldatabase.app.ui.activities.ViewMemeActivity
import com.mysqldatabase.app.ui.adapters.FavesAdapter
import com.mysqldatabase.app.ui.base.BaseFragment
import com.mysqldatabase.app.ui.callbacks.FavesCallback
import com.mysqldatabase.app.ui.viewmodels.MemesViewModel
import com.mysqldatabase.app.utils.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.makeramen.roundedimageview.RoundedDrawable
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.fragment_faves.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavesFragment : BaseFragment() {
    private lateinit var favesAdapter: FavesAdapter
    private val memesViewModel: MemesViewModel by viewModel()

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentFavesBinding>(inflater, R.layout.fragment_faves, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initFavesObserver()
        initStatusObserver()
        initResponseObserver()
    }

    private fun initViews() {
        favesAdapter = FavesAdapter(favesCallback)

        favesRv.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(activity!!, 1)
            addItemDecoration(RecyclerFormatter.GridItemDecoration(activity!!, R.dimen.grid_layout_margin))
            adapter = favesAdapter
            AppUtils.handleHomeScrolling(this)
        }

        favesRefresh.setOnRefreshListener {
            favesAdapter.currentList?.dataSource?.invalidate()
            runDelayed(2500) { favesRefresh.isRefreshing = false }
        }
    }

    /**
     * Initialize observer for Faves LiveData
     */
    private fun initFavesObserver() {
        memesViewModel.fetchFaves().observe(this, Observer {
            favesAdapter.submitList(it)
        })
    }

    /**
     * Initialize function to observer Empty State LiveData
     */
    private fun initStatusObserver() {
        memesViewModel.showStatusLiveData.observe(this, Observer {
            when (it) {
                Status.LOADING -> {
                    emptyState.hideView()
                    loading.showView()
                }
                Status.ERROR -> {
                    loading.hideView()
                    emptyState.showView()
                }
                else -> {
                    loading.hideView()
                    emptyState.hideView()
                }
            }
        })
    }

    /**
     * Initialize observer for (Delete) Response LiveData
     */
    private fun initResponseObserver() {
        memesViewModel.genericResponseLiveData.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> toast("Deleting fave \uD83D\uDC94...")

                Status.SUCCESS -> {
                    toast("Fave deleted \uD83D\uDEAE")
                    favesAdapter.currentList?.dataSource?.invalidate()
                }

                Status.ERROR -> toast("${it.error}. Please try again")
            }
        })
    }

    private val favesCallback = object : FavesCallback {
        override fun onFaveClick(view: RoundedImageView, meme: Fave, longClick: Boolean) {
            if (longClick) handleLongClick(meme.id!!)
            else handleClick(meme, (view.drawable as RoundedDrawable).sourceBitmap)
        }

        override fun onCommentClicked(view: View, meme: Fave) {

        }
    }

    /**
     * Function to handle click on Fave item
     */
    private fun handleClick(mem: Fave, image: Bitmap) {
        if (mem.isVideo) {
            playVideo(mem)
        } else {
            AppUtils.saveTemporaryImage(activity!!, image)

            val i = Intent(activity, ViewMemeActivity::class.java)
            i.putExtra(Constants.PIC_URL, mem.imageUrl)
            i.putExtra(Constants.CAPTION, mem.caption)
            startActivity(i)
            AppUtils.fadeIn(activity!!)
        }
    }

    private fun playVideo(meme: Fave) {


        val i = Intent(activity, VideoPlayActivity::class.java)
        i.putExtra(Constants.PIC_URL, meme.imageUrl)
        i.putExtra(Constants.CAPTION, meme.caption)
        startActivity(i)
        AppUtils.fadeIn(activity!!)
    }

    /**
     * Function to handle long click on Fave item
     */
    private fun handleLongClick(faveId: String) {
        MaterialAlertDialogBuilder(activity,R.style.ALertTheme)

                .setMessage("Delete meme from favorites?")
                .setPositiveButton("Delete",object: DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        memesViewModel.deleteFave(faveId, getUid())                  }
                })
                .setNegativeButton("Cancel" ,object: DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        p0!!.cancel()                       }
                })
                .show();
/*        activity!!.alert("Delete meme from favorites?") {
            positiveButton("Delete") {
                memesViewModel.deleteFave(faveId, getUid())
            }
            negativeButton("Cancel") {}
        }.show()*/
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPostMemeEvent(event: PostMemeEvent) {
        if (event.type == PostMemeEvent.TYPE.FAVORITE) {
            favesAdapter.currentList?.dataSource?.invalidate()
        }
    }

    override fun onStop() {
        super.onStop()
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this)
    }

}
