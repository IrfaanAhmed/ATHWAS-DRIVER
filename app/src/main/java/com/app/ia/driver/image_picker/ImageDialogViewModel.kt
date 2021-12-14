package com.app.ia.driver.image_picker

import com.app.ia.driver.base.BaseDialogViewModel
import com.app.ia.driver.image_picker.ImagePickerDialogNavigator

class ImageDialogViewModel(private val imagePickerDialogNavigator: ImagePickerDialogNavigator?) : BaseDialogViewModel() {

    fun onCameraClick() {
        imagePickerDialogNavigator?.onCameraClick()
    }

    fun onGalleryClick() {
        imagePickerDialogNavigator?.onGalleryClick()
    }
}