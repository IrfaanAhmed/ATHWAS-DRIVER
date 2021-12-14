package com.app.ia.driver.image_picker

interface OnImagePickListener {

    fun onImageSelect(path: String)

    fun onImageError(message: String)
}
