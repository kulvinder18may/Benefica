package com.panchayat.takoli.ui.callbacks

import android.view.View
import com.panchayat.takoli.data.models.Notification

interface NotificationsCallback {
    fun onNotificationClicked(view: View, notification: Notification)
}