package com.benfica.app.ui.base

import android.app.ProgressDialog
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.benfica.app.R
import com.benfica.app.utils.Constants
import com.benfica.app.utils.SessionManager
import com.benfica.app.utils.TimeFormatter
import com.google.firebase.database.DatabaseReference
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import org.koin.android.ext.android.inject
import java.io.File


open class BaseActivity : AppCompatActivity() {
    private lateinit var progressDialog: ProgressDialog
    val sessionManager: SessionManager by inject()
    private val firebaseDatabase: DatabaseReference by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this))
        progressDialog = ProgressDialog(this)
    }

    override fun onResume() {
        super.onResume()
        updateLastActive()
    }

    // Set dark status bar
    fun setDarkStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.decorView.systemUiVisibility = 0
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        }
    }

    // Show progress dialog
    fun showLoading(message: String) {
        progressDialog.setMessage(message)
        progressDialog.setCancelable(true)
        progressDialog.show()
    }

    fun hideLoading() {
        progressDialog.dismiss()
    }

    // Get user ID
    fun getUid(): String = sessionManager.getUserId()

    private fun updateLastActive() {
        firebaseDatabase.child(Constants.METADATA)
                .child(Constants.LAST_ACTIVE)
                .child(TimeFormatter().getFullYear(System.currentTimeMillis()))
                .child(getUid())
                .setValue(TimeFormatter().getTime(System.currentTimeMillis()))
    }

    fun getVideoDuration(uri: Uri) {

        val retriever = MediaMetadataRetriever();
        retriever.setDataSource(this, Uri.parse(uri.toString()));
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        println("Video Duration  $duration")
        retriever.release();

    }

    fun getThumbnail(uri: Uri): String {
        val filePathColumn = arrayOf(MediaStore.Video.Media.DATA)
        val cursor =  contentResolver.query(uri, filePathColumn, null, null, null)
        cursor!!.moveToFirst()

        val columnIndex = cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA)
        val picturePath = cursor.getString(columnIndex)
        cursor.close()

        return   picturePath


    }

    fun getFileSize(filepath: String): Long {
        val file = File(filepath)

        var length: Long = file.length()
        length = length / 1024
        return length / 1024
    }
}
