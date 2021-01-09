package com.panchayat.takoli.ui.callbacks

import com.panchayat.takoli.data.models.Report

interface ReportsCallback {
    fun onReportClicked(report: Report)
}