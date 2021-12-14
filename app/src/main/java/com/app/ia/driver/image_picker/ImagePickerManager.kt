package com.app.ia.driver.image_picker

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource

class ImagePickerManager(var onImagePickListener: OnImagePickListener, var mContext: Context) {

    var mFragment: Fragment? = null
    private var easyImage: EasyImage = EasyImage.Builder(mContext).build()

    private fun selectImageFromCamera() {
        easyImage.openCameraForImage(mContext as AppCompatActivity)
    }

    private fun selectImageFromCamera(mFragment: Fragment) {
        easyImage.openCameraForImage(mFragment)
    }

    private fun selectImageFromGallery() {
        easyImage.openGallery(mContext as AppCompatActivity)
    }

    private fun selectImageFromGallery(mFragment: Fragment) {
        easyImage.openGallery(mFragment)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        easyImage.handleActivityResult(requestCode, resultCode, data, mContext as AppCompatActivity, object : DefaultCallback() {

            override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                val compressImageFile = CompressFile.compressImage(Uri.fromFile(imageFiles[0].file).toString(), mContext)
                onImagePickListener.onImageSelect(compressImageFile)
            }
        })
    }

    fun askForPermission() {
        Dexter.withContext(mContext as Activity).withPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) {
                    showImageDialog()
                }
            }

            override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                token.continuePermissionRequest()
            }
        }).check()
    }

    private fun showImageDialog() {

        val imagePickerDialog: ImagePickerDialog = ImagePickerDialog.getInstance(mContext as AppCompatActivity)
        imagePickerDialog.setOnClickListener(object : ImagePickerDialogNavigator {

            override fun onCameraClick() {
                fromCamera()
                imagePickerDialog.dismiss()
            }

            override fun onGalleryClick() {
                fromGallery()
                imagePickerDialog.dismiss()
            }
        })

        val ft = (mContext as AppCompatActivity).supportFragmentManager.beginTransaction()
        ft.add(imagePickerDialog, "image_picker")
        ft.commitAllowingStateLoss()
    }

    private fun fromGallery() {
        if (mFragment == null) selectImageFromGallery()
        else selectImageFromGallery(mFragment!!)
    }

    private fun fromCamera() {
        if (mFragment == null) selectImageFromCamera()
        else selectImageFromCamera(mFragment!!)
    }

}