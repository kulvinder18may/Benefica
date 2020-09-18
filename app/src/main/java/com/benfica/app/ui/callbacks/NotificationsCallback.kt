package com.mysqldatabase.app.ui.callbacks

import android.view.View
import com.mysqldatabase.app.data.models.Notification

interface NotificationsCallback {
    fun onNotificationClicked(view: View, notification: Notification)
}