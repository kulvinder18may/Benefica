package com.mysqldatabase.app.ui.callbacks

import com.mysqldatabase.app.data.models.Report

interface ReportsCallback {
    fun onReportClicked(report: Report)
}