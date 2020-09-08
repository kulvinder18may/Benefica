package com.benfica.app.ui.callbacks

import android.view.View
import com.benfica.app.data.models.Notification

interface NotificationsCallback {
    fun onNotificationClicked(view: View, notification: Notification)
}