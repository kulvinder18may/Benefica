package com.mysqldatabase.app.ui.activities

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.benfica.app.utils.MaskWatcher
import com.benfica.app.utils.RealStoragePathLibrary
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mikepenz.ionicons_typeface_library.Ionicons
import com.mysqldatabase.app.R
import com.mysqldatabase.app.data.Status
import com.mysqldatabase.app.data.models.Meme
import com.mysqldatabase.app.ui.base.BaseActivity
import com.mysqldatabase.app.ui.viewmodels.MemesViewModel
import com.mysqldatabase.app.utils.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_post.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File


class PostMemeActivity : BaseActivity() {
    private var mediaFile: File? = null
    private var cameraClickFile: File? = null
    private var imageUri: Uri? = null
    private var imageSelected = false
    private var uploadMeme: MenuItem? = null
    private val memesViewModel: MemesViewModel by viewModel()
    var isVideo = false

    companion object {
        private const val GALLERY_REQUEST = 125
        private const val CAMERA_PHOTO_REQUEST = 126
        private const val CAMERA_VIDEO_REQUEST = 127
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        initViews()
        checkIfShareAction()

        initMemesObserver()
    }

    private fun initViews() {
        setSupportActionBar(postToolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            title = null
        }
        postTag.addTextChangedListener(MaskWatcher("##-##-##"))
        postCaption.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (postCaption.hasFocus()) {
                    v!!.getParent().requestDisallowInterceptTouchEvent(true);
                    when (event!!.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_SCROLL -> {
                            v.parent.requestDisallowInterceptTouchEvent(false)
                            return true
                        }
                    }

                }
                return false;
            }
        })

        postAddImage.setOnClickListener {
            AppUtils.requestStoragePermission(this) { granted ->
                if (granted) {
                    AppUtils.requestStorageReadPermission(this) { gant ->
                        if (gant) {
                            MaterialAlertDialogBuilder(this, R.style.ALertTheme)

                                    .setMessage("Choose meme")
                                    .setPositiveButton("Gallery") { p0, p1 ->
                                        val photoPickerIntent = Intent(Intent.ACTION_PICK)
                                        photoPickerIntent.type = "image/* video/*"
                                        startActivityForResult(photoPickerIntent, GALLERY_REQUEST)
                                    }
                                    .setNegativeButton("Camera") { p0, p1 -> pickCamera() }
                                    .show();


                        } else longToast("Storage permission is required to select Avatar")
                    }
                }
            }
        }
    }

    fun pickCamera() {

        MaterialAlertDialogBuilder(this, R.style.ALertTheme)

                .setMessage("What you want to capture?")
                .setPositiveButton("Photo") { p0, p1 ->
                    takePicture()
                }
                .setNegativeButton("Video") { p0, p1 -> captureVideo() }
                .show();
    }

    /**
     *  Check if activity was started by share intent from another app & if intent has image
     */
    private fun checkIfShareAction() {
        val intent = intent
        val action = intent.action
        val type = intent.type

        if (Intent.ACTION_SEND == action && type != null) {
            imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM) as Uri
            startCropActivity(imageUri!!)
        }
    }

    /**
     * Initialize observer for Meme LiveData
     */
    private fun initMemesObserver() {
        memesViewModel.genericResponseLiveData.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                    showLoading("Posting meme...")
                }

                Status.SUCCESS -> {
                    hideLoading()
                    showSelectImage()
                    sessionManager.hasNewContent(true)
                    toast("Meme posted \uD83E\uDD2A\uD83E\uDD2A")
                    finish()
                }

                Status.ERROR -> {
                    hideLoading()
                    toast(it.error!!)
                }
            }
        })
    }

    /**
     * Function to post new Meme
     */


    private fun postMeme() {
        if (!Connectivity.isConnected(this)) {
            toast("Please turn on your internet connection")
            return
        }

        if (!sessionManager.isLoggedIn() || sessionManager.getUserId().isEmpty()) {
            toast("Please login first")
            return
        }

        if (imageUri == null || !imageSelected) {
            toast("Please select a meme")
            return
        }
        if (postCaption.text.toString().trim().isEmpty()) {
            toast("Please write something")
            postCaption.setError("Please write something")
            return
        }
        if (postTag.text.toString().trim().isEmpty()) {
            toast("Please add Plate no.")
            postTag.setError("Please add Plate no.")
            return
        }
        // ThumbnailUtils.createVideoThumbnail(File((imageUri as Uri).path), Size(100,100),null)

        // Create new meme object
        val meme = Meme()
        meme.caption = postCaption.text.toString().trim()
        meme.city = postCity.text.toString().trim()
        meme.hashTag = "${postTag.text.toString().trim()}"
        meme.likesCount = 0
        meme.commentsCount = 0
        meme.memePoster = sessionManager.getUsername()
        meme.memePosterAvatar = sessionManager.getUserAvatar()
        meme.memePosterID = sessionManager.getUserId()
        meme.time = System.currentTimeMillis()
        meme.isVideo = isVideo
        memesViewModel.postMeme(imageUri!!, meme)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_post_meme, menu)

        uploadMeme = menu?.findItem(R.id.menu_post)
        uploadMeme?.icon = AppUtils.getDrawable(this, Ionicons.Icon.ion_android_send, R.color.color_secondary, 20)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.menu_post -> postMeme()
        }

        return true
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = data!!.getData()
            val selectedImagePath: String? = getRealPathFromURI(selectedImageUri)
            println("path $selectedImagePath")
            if (!selectedImagePath!!.endsWith(".mp4"))
                startCropActivity(selectedImageUri!!)
            else {
                isVideo = true
                imageSelected = true
                imageUri = selectedImageUri
                println(getFileSize(getRealPathFromURI(selectedImageUri!!)!!))
                postSelectVideo.visibility = View.VISIBLE
                postAddImage.visibility = View.GONE
                Glide.with(this).load("file://${getThumbnail(selectedImageUri!!)}")
                        .skipMemoryCache(false)
                        .into(postSelectVideo);
                // postSelectImage.setImageBitmap(getThumbnail(selectedImageUri!!))
            }

        }
        var oriFile: File? = null
        if (requestCode == CAMERA_PHOTO_REQUEST) {
            if (cameraClickFile == null) {
                cameraClickFile = File(getSdCardPath().toString() + "/temp.jpg")
            }
            oriFile = cameraClickFile


            startCropActivity(Uri.fromFile(oriFile))


        }
        if (requestCode == CAMERA_VIDEO_REQUEST) {
            if (resultCode == RESULT_OK) {
                isVideo = true
                imageSelected = true

                postSelectVideo.visibility = View.VISIBLE
                postAddImage.visibility = View.GONE
                // if (mediaFile == null) {
                mediaFile = File(getSdCardPath().toString() + "/myvideo.mp4")
                // }
                imageUri = Uri.fromFile(mediaFile)!!

                postSelectVideo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_video))
                toast("Video selected! preview not available")
                /*  Glide.with(this).load("file://${getThumbnail(Uri.fromFile(mediaFile)!!)}")
                          .skipMemoryCache(false)
                          .into(postSelectVideo);*/
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                imageSelected = true
                val resultUri = result.uri

                postSelectImage.setImageURI(resultUri)
                imageUri = resultUri
                showSelectedImage()

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Timber.e("Error cropping meme: ${result.error.localizedMessage}")
            }
        }
    }

    /**
     * Function to launch Image crop activity
     * @param imageUri - Selected image Uri
     */
    private fun startCropActivity(imageUri: Uri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this)
    }

    /**
     *  Image has been selected, show the image in ImageView and hide the select image button
     */
    private fun showSelectedImage() {
        Timber.e("Hiding button to select image")
        postAddImage.visibility = View.GONE
        postSelectImage.showView()
    }

    /**
     *  No image selected, hide ImageView and show the select image button
     */
    private fun showSelectImage() {
        imageSelected = false
        imageUri = null

        postSelectVideo.hideView()
        postSelectImage.hideView()
        showViews(postAddImage)
        postCaption.setText("")
    }

    override fun onBackPressed() {
        if (imageSelected) {
            MaterialAlertDialogBuilder(this, R.style.ALertTheme)

                    .setMessage("Remove selected image?")
                    .setPositiveButton("Remove") { p0, p1 -> showSelectImage() }
                    .setNegativeButton("Cancel") { p0, p1 -> p0!!.cancel() }
                    .show();
            /* alert("Remove selected image?") {
             positiveButton("Remove") {
                 showSelectImage()
             }
             negativeButton("Cancel") {}
         }.show()*/
        } else {
            super.onBackPressed()
            AppUtils.slideLeft(this)
        }
    }

    fun getRealPathFromURI(uri: Uri?): String? {
        if (uri == null) {
            return null
        }
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = getContentResolver().query(uri, projection, null, null, null)
        if (cursor != null) {
            val column_index: Int = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        }
        return uri.path
    }

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraClickFile = File(getSdCardPath().toString() + "/temp.jpg")
        if (Build.VERSION.SDK_INT >= VERSION_CODES.N) {
            val apkURI = FileProvider.getUriForFile(
                    this, this.getPackageName().toString() + ".provider", cameraClickFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, apkURI)
        } else intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraClickFile))
        startActivityForResult(intent, CAMERA_PHOTO_REQUEST)
    }

    private fun getSdCardPath(): String? {
        var path: String? = null
        val realStoragePath = RealStoragePathLibrary(this)
        val inbuiltStoragePath: String? = realStoragePath.getInbuiltStorageAppSpecificDirectoryPath()
        val microSDStoragePath: String? = realStoragePath.getMicroSDStorageAppSpecificDirectoryPath()
        path = if (microSDStoragePath != null && microSDStoragePath.length > 0) {
            microSDStoragePath
        } else {
            inbuiltStoragePath
        }
        return path
    }

    private fun captureVideo() {
        mediaFile = File(getSdCardPath().toString() + "/myvideo.mp4")
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if (Build.VERSION.SDK_INT >= VERSION_CODES.N) {
            val apkURI = FileProvider.getUriForFile(
                    this, this.getPackageName().toString() + ".provider", mediaFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, apkURI)
        } else
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mediaFile))
        startActivityForResult(intent, CAMERA_VIDEO_REQUEST)


    }


    /*        if (Build.VERSION.SDK_INT >= VERSION_CODES.N) {
            val apkURI = FileProvider.getUriForFile(
                    this, this.getPackageName().toString() + ".provider", cameraClickFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, apkURI)
        } else intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraClickFile))*/
}
