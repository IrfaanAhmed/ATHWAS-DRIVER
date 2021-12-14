package com.app.ia.driver.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.InverseMethod
import com.app.ia.driver.DriverApplication
import com.app.ia.driver.R
import com.app.ia.driver.local.AppPreferencesHelper
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

object CommonUtils {

    val timestamp: String
        get() = SimpleDateFormat(AppConstants.TIME_STAMP_FORMAT, Locale.US).format(Date())

    val voucherTimestamp: String
        get() = SimpleDateFormat(AppConstants.VOUCHER_DATE_FORMAT, Locale.US).format(Date())

    val chatTimeStamp: String
        get() = SimpleDateFormat(AppConstants.CHAT_TIME_STAMP_FORMAT, Locale.US).format(Date())

    @SuppressLint("all")
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validateNumber(countryCode: String, phNumber: String): Boolean {
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        val isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode))
        var phoneNumber: Phonenumber.PhoneNumber? = null
        try {
            phoneNumber = phoneNumberUtil.parse(phNumber, isoCode)
        } catch (e: NumberParseException) {
            e.printStackTrace()
        }
        return phoneNumberUtil.isValidNumber(phoneNumber)
    }

    /*
   * Validation for Name
   * */
    fun isValidName(name: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val LASTNAME_PATTERN = "[a-zA-Z.\\s]+"
        pattern = Pattern.compile(LASTNAME_PATTERN)
        matcher = pattern.matcher(name)
        return matcher.matches()
    }

    /**
     * Show the soft input.
     *
     * @param activity The activity.
     */
    fun showSoftInput(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
            view.isFocusable = true
            view.isFocusableInTouchMode = true
            view.requestFocus()
        }
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }

    /**
     * Hide the soft input.
     *
     * @param activity The activity.
     */
    fun hideSoftInput(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) view = View(activity)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Hide the soft input.
     */
    fun hideSoftInput(view: View) {
        val imm = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive)
            imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @SuppressLint("PackageManagerGetSignatures", "WrongConstant")
    fun hashKey(context: Context): String {
        var keyhash = ""
        try {
            val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            } else {
                context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            }
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.signingInfo.apkContentsSigners
            } else {
                info.signatures
            }

            for (signature in signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                AppLogger.d(Base64.encodeToString(md.digest(), Base64.DEFAULT))
                keyhash = Base64.encodeToString(md.digest(), Base64.DEFAULT)
            }
        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }
        return keyhash
    }

    /*
     * Method to convert number into 2 points after decimal.
     * */
    @InverseMethod("convertToDecimal")
    @JvmStatic fun convertToDecimal(value: Any?): String {
        if (value == null) {
            return "00"
        }

        val symbols = DecimalFormatSymbols(Locale.ENGLISH)
        val df2 = DecimalFormat("#0.00", symbols)
        return when (value) {
            is Float -> df2.format(value)
            is Double -> df2.format(value)
            is String -> df2.format(value.toDouble())
            is Int -> df2.format((value.toString()).toDouble())
            else -> "00"
        }
    }

    /*
     * Method to convert number into 2 points after decimal.
     * */
    fun convertToKm(value: Any): String {
        val symbols = DecimalFormatSymbols(Locale.ENGLISH)
        val df2 = DecimalFormat("#0.0#", symbols)
        return when (value) {
            is Float -> String.format("%.1f", df2.format(value).toDouble())
            is Double -> df2.format(value)
            is String -> df2.format(value.toDouble())
            is Int -> df2.format((value.toString()).toDouble())
            else -> "0"
        }
    }

    /*
     * Method to convert ratings into 1 points after decimal.
     * */
    fun convertToDecimalRating(value: Any): String {
        val symbols = DecimalFormatSymbols(Locale.ENGLISH)
        val df2 = DecimalFormat("0.0", symbols)
        return when (value) {
            is Float -> df2.format(value)
            is Double -> df2.format(value)
            is String -> df2.format(value.toDouble())
            is Int -> df2.format((value.toString()).toDouble())
            else -> "0.0"
        }
    }

    fun setDrawable(view: TextView?) {
        if (view != null) {
            if (AppPreferencesHelper.getInstance().language == "en") {
                val wrappedDrawable = DrawableCompat.wrap(view.compoundDrawables[0])
                if (wrappedDrawable != null) {
                    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(DriverApplication.getInstance(), R.color.light_grey))
                }
            } else if (AppPreferencesHelper.getInstance().language == "ar") {
                var wrappedDrawable = DrawableCompat.wrap(view.compoundDrawables[0])
                if (wrappedDrawable != null) {
                    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(DriverApplication.getInstance(), R.color.light_grey))
                } else {
                    wrappedDrawable = DrawableCompat.wrap(view.compoundDrawables[2])
                    if (wrappedDrawable != null) {
                        DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(DriverApplication.getInstance(), R.color.light_grey))
                    }
                }
            }
        }
    }

    /*
    * Slide Up a View with animation
    * */
    fun slideUp(view: View) {
        view.visibility = View.VISIBLE
        val animate = TranslateAnimation(
            0f, // fromXDelta
            0f, // toXDelta
            view.height.toFloat(), // fromYDelta
            0f  // toYDelta
        )
        animate.duration = 500
        animate.fillAfter = true
        view.startAnimation(animate)
    }

    /*
    *
    * Slide down a view with animation
    * */
    fun slideDown(view: View) {
        val animation = TranslateAnimation(0f, 0f, 0f, view.height.toFloat())
        animation.duration = 500
        animation.fillAfter = true
        view.startAnimation(animation)
        view.visibility = View.GONE
    }

    /*
    * Decode Base64 String
    * */
    fun decodeBase64(base64: String): String {
        return Base64.decode(base64, Base64.DEFAULT).toString(charset("UTF-8"))
    }

    fun isBase64(base64String: String): Boolean {
        if (base64String.isEmpty() || base64String.length % 4 != 0 || base64String.contains(" ") || base64String.contains("\t") || base64String.contains("\r") || base64String.contains("\n")) {
            return false
        } else {
            try {
                return true
            } catch (e: Exception) {
            }
        }
        return false
    }

    /*
    * Get Current Date
    * */
    fun getCurrentDate(): String {
        val date = Calendar.getInstance().time
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return sdf.format(date)
    }

    /*
    * Open Google Map with latitude longitude
    * */
    fun openGoogleMapWithLocation(context: Context, currentLat: String, currentLong: String, desLat: String, desLong: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://ditu.google.cn/maps?f=d&source=s_d&saddr=$currentLat,$currentLong&daddr=$desLat,$desLong&hl=zh&t=m&dirflg=d"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity")
        context.startActivity(intent)
    }

    /*
    * Calculate Distance between two location point
    * */
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val loc1 = Location("")
        loc1.latitude = lat1
        loc1.longitude = lon1

        val loc2 = Location("")
        loc2.latitude = lat2
        loc2.longitude = lon2

        val distanceInMeters = loc1.distanceTo(loc2)
        return distanceInMeters / 1000
    }

    /*
    * Scan Select QR Code Image
    * */
    open fun scanQRImage(bMap: Bitmap): String? {
        var contents: String? = null
        val intArray = IntArray(bMap.width * bMap.height)
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.width, 0, 0, bMap.width, bMap.height)
        val source: LuminanceSource = RGBLuminanceSource(bMap.width, bMap.height, intArray)
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        val reader: Reader = MultiFormatReader()
        try {
            val result = reader.decode(bitmap)
            contents = result.text
        } catch (e: java.lang.Exception) {
            Log.e("QrTest", "Error decoding barcode", e)
        }
        return contents
    }

    /*
    * Get Bitmap from File Path
    * */
    open fun getBitmap(path: String?): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val f = File(path)
            val options: BitmapFactory.Options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            bitmap = BitmapFactory.decodeStream(FileInputStream(f), null, options)
            //image.setImageBitmap(bitmap)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

    /*
    * Create Multipart Image File Object
    * */
    fun prepareFilePart(context: Context, partName: String, fileUri: Uri, imageFile: File): MultipartBody.Part{
        var extension = MimeTypeMap.getFileExtensionFromUrl(fileUri.toString())
        var requestFile = RequestBody.create("image/$extension".toMediaTypeOrNull(), imageFile)
        return MultipartBody.Part.createFormData(partName, imageFile.name, requestFile)
    }

    /*
    * Create Part Data From String
    * */
    fun prepareDataPart(data: String): RequestBody{
        return data.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    /*
     * Date Picker and set on Edit text
     * */
    fun openDatePicker(context: Context, editText: AppCompatEditText) {
        val dpd = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val myCalendar = Calendar.getInstance()
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            editText.setText(sdf.format(myCalendar.time))
        }

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val d = DatePickerDialog(context, dpd, year, month, day)
         /*if (editText.id == R.id.editTextStartDate || editText.id == R.id.editTextEndDate) {
             d.datePicker.minDate = System.currentTimeMillis()
         }*//* else if (editText.id == R.id.edtDateCreated) {
             d.datePicker.maxDate = System.currentTimeMillis()
         } else if (editText.id == R.id.edtLastUpdated) {
             d.datePicker.maxDate = System.currentTimeMillis()
         }*/
        d.show()
    }

    /*
    * Change Date Format
    * */
    fun toDateTime(
        value: String?,
        currFormat: String,
        timeFormat: String
    ): String {
        if (value != null) {
            AppLogger.d(value)
        }
        if (value.isNullOrEmpty())
            return "N/A"
        else {
            try {
                val inputFormat = SimpleDateFormat(currFormat)
                inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                val date = inputFormat.parse(value)
                val formatted= SimpleDateFormat(timeFormat)
                formatted.setTimeZone(TimeZone.getDefault());
                val formattedDate =   formatted  .format(date)
                println(formattedDate) // prints 10-04-2018
                AppLogger.d(" Date : $value")
                AppLogger.d("formatted Date : ${formattedDate}")
                return formattedDate
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }
        }
    }

    /*
   * Scan Select QR Code Image
   * */
    fun scanQRCodeFromImage(bMap: Bitmap): String {
        var contents = ""

        val intArray = IntArray(bMap.getWidth() * bMap.getHeight())
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight())

        var source = RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray)
        var bitmap = BinaryBitmap(HybridBinarizer(source))

        var reader = MultiFormatReader()
        try {
            var result = reader.decode(bitmap)
            contents = result.getText()
        }
        catch (e: Exception) {
            Log.e("QrTest", "Error decoding barcode", e)
        }
        return contents
    }

    /*
    * Get Address from Location
    * */
    fun getAddressFromLocation(context: Context, latitude: Double, longitude: Double): String {

        val geoCoder = Geocoder(context, Locale.getDefault())
        var addresses: List<Address>? = null
        val sb = StringBuilder()

        try {
            addresses = geoCoder.getFromLocation(latitude, longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (e: Exception) {
            e.printStackTrace()
        }


        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0].getAddressLine(0)
            val city = addresses[0].locality
            val state = addresses[0].adminArea
            val country = addresses[0].countryName
            val postalCode = addresses[0].postalCode
            val knownName = addresses[0].featureName

            sb.append(address).append("")
        }
        return sb.toString()
    }

    /*
    * Check JSOSN String valid or not
    * */
    fun isJSONValid(test: String?): Boolean {
        try {
            JSONObject(test!!)
        } catch (ex: JSONException) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                JSONArray(test)
            } catch (ex1: JSONException) {
                return false
            }
        }
        return true
    }
}