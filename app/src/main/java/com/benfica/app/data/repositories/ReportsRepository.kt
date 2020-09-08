package com.benfica.app.data.repositories

import com.benfica.app.data.models.Report
import com.benfica.app.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import timber.log.Timber

/**
 * Repository class for Reports
 */
class ReportsRepository constructor(private val firestoreDatabase: FirebaseFirestore) {

    /**
     * Fetch all Reports
     */
    suspend fun fetchReports(loadBefore: String? = null, loadAfter: String? = null): List<Report> {
        Timber.e("Fetching reports...")
        val db = firestoreDatabase.collection(Constants.REPORTS)
        var query = db.orderBy(Constants.TIME, Query.Direction.DESCENDING).limit(Constants.MEMES_COUNT)

        loadBefore?.let {
            val report = db.document(it).get().await()
            query = query.endBefore(report)
        }

        loadAfter?.let {
            val report = db.document(it).get().await()
            query = query.startAfter(report)
        }

        return query.get().await().map { it.toObject(Report::class.java) }
    }

}