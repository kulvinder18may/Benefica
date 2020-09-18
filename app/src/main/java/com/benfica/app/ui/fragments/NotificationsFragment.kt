package com.mysqldatabase.app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mysqldatabase.app.R
import com.mysqldatabase.app.ui.activities.ProfileActivity
import com.mysqldatabase.app.ui.adapters.NotificationsAdapter
import com.mysqldatabase.app.ui.callbacks.NotificationsCallback
import com.mysqldatabase.app.ui.base.BaseFragment
import com.mysqldatabase.app.data.models.Notification
import com.mysqldatabase.app.databinding.FragmentNotificationsBinding
import com.mysqldatabase.app.ui.activities.MemeActivity
import com.mysqldatabase.app.ui.viewmodels.NotificationsViewModel
import com.mysqldatabase.app.utils.*
import kotlinx.android.synthetic.main.fragment_notifications.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationsFragment : BaseFragment() {
    private lateinit var notificationsAdapter: NotificationsAdapter
    private val notificationsViewModel: NotificationsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentNotificationsBinding>(inflater, R.layout.fragment_notifications, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initEmptyStateObserver()
        initNotificationsObserver()
    }

    private fun initViews() {
        notificationsAdapter = NotificationsAdapter(callback)

        notifRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(RecyclerFormatter.SimpleDividerItemDecoration(activity!!))
            adapter = notificationsAdapter
            AppUtils.handleHomeScrolling(this)
        }

        notifRefresh.setOnRefreshListener {
            notificationsAdapter.currentList?.dataSource?.invalidate()
            runDelayed(2500) { notifRefresh.isRefreshing = false }
        }
    }

    private fun initNotificationsObserver() {
        notificationsViewModel.fetchNotifications().observe(this, Observer {
            notificationsAdapter.submitList(it)
        })
    }

    /**
     * Initialize function to observer Empty State LiveData
     */
    private fun initEmptyStateObserver() {
        notificationsViewModel.showEmptyStateLiveData.observe(this, Observer {
            when (it) {
                true -> {
                    notifRv.hideView()
                    emptyState.showView()
                }
                else -> {
                    emptyState.hideView()
                    notifRv.showView()
                }
            }
        })
    }

    private val callback = object : NotificationsCallback {
        override fun onNotificationClicked(view: View, notification: Notification) {
            when(view.id) {
                R.id.avatar -> {
                    val i = Intent(activity, ProfileActivity::class.java)
                    i.putExtra(Constants.USER_ID, notification.userId)
                    startActivity(i)
                    AppUtils.slideRight(activity!!)
                }

                else -> {
                    val i = Intent(activity, MemeActivity::class.java)
                    i.putExtra(Constants.MEME_ID, notification.memeId)
                    startActivity(i)
                    AppUtils.slideRight(activity!!)
                }
            }
        }
    }

}

