package com.rasel.flickergallery.viewmodels

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.textfield.TextInputLayout
import com.rasel.flickergallery.R
import com.rasel.flickergallery.data.models.Item
import com.rasel.flickergallery.utils.GlideApp
import com.rasel.flickergallery.utils.ToasterUtils.showToast
import com.rasel.flickergallery.utils.Validator
import com.rasel.flickergallery.utils.debugLogInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor() : ViewModel() {

    val item: MutableLiveData<Item> = MutableLiveData()

    fun saveImageToGallery(view: View, imagePath: String, imageTitle: String) {
        CoroutineScope(Dispatchers.IO).launch {
            GlideApp.with(view)
                .asBitmap()
                .load(imagePath)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) saveImageInQ(
                            resource,
                            view.context,
                            imageTitle
                        )
                        else saveTheImageLegacyStyle(resource, view.context, imageTitle)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        debugLogInfo("Error occurred while saving image to gallery")
                        view.context?.let {
                            CoroutineScope(Dispatchers.Main).launch {
                                showToast(it, "Couldn't save image! Try again")
                            }
                        }
                    }
                })
        }
    }

    //Make sure to call this function on a worker thread, else it will block main thread
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveImageInQ(bitmap: Bitmap, context: Context, imageTitle: String) {
        val filename = "${imageTitle}.jpg"
        var fos: OutputStream?
        var imageUri: Uri?
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }

        //use application context to get contentResolver
        val contentResolver = context.contentResolver

        contentResolver.also { resolver ->
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = imageUri?.let { resolver.openOutputStream(it) }
        }

        fos?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 70, it) }

        contentValues.clear()
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
        imageUri?.let { contentResolver.update(it, contentValues, null, null) }

        CoroutineScope(Dispatchers.Main).launch {
            showToast(context, "Image saved to gallery")
        }
    }

    //Make sure to call this function on a worker thread, else it will block main thread
    private fun saveTheImageLegacyStyle(bitmap: Bitmap, context: Context, imageTitle: String) {
        val imagesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imagesDir, "${imageTitle}.jpg")
        val fos = FileOutputStream(image)

        fos.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }

        CoroutineScope(Dispatchers.Main).launch {
            showToast(context, "Image saved to gallery")
        }
    }

    private fun shareViaEmail(context: Context, email: String) {
        val url = item.value?.media?.m ?: return
        debugLogInfo("Email: $email")
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Image from FlickerGallery app")
        intent.putExtra(Intent.EXTRA_TEXT, "Please have a look at this link. \n${url}")
        try {
            context.startActivity(Intent.createChooser(intent, "Choose Email Client..."))
        } catch (ex: ActivityNotFoundException) {
            showToast(context, "Application not found")
        }
    }

    fun showAlertWithTextInputLayout(context: Context) {
        val textInputLayout = TextInputLayout(context)
        textInputLayout.setPadding(
            context.resources.getDimensionPixelOffset(R.dimen.dp_20),
            context.resources.getDimensionPixelOffset(R.dimen.dp_20),
            context.resources.getDimensionPixelOffset(R.dimen.dp_20),
            context.resources.getDimensionPixelOffset(R.dimen.dp_20)
        )
        val input = EditText(context)
        input.setPadding(
            0,
            context.resources.getDimensionPixelOffset(R.dimen.dp_20),
            0,
            context.resources.getDimensionPixelOffset(R.dimen.dp_20)
        )
        textInputLayout.hint = "Email"
        textInputLayout.addView(input)

        val alert = AlertDialog.Builder(context)
            .setTitle("Share via email")
            .setView(textInputLayout)
            .setMessage("Please enter email address with whom you want to share")
            .setPositiveButton("Submit") { dialog, _ ->
                val email = input.text.trim().toString()
                val validator = Validator()
                if (validator.validateEmail(email)) {
                    dialog.cancel()
                    shareViaEmail(context, email)
                } else {
                    showToast(context, "Enter valid email")
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }.create()

        alert.show()
    }
}