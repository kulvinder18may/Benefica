package com.benfica.app.ui.callbacks

import com.benfica.app.data.models.Report

interface ReportsCallback {
    fun onReportClicked(report: Report)
}