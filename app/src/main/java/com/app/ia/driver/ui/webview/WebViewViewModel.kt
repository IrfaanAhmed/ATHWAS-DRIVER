package com.app.ia.driver.ui.webview

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.base.BaseViewModel
import com.app.ia.driver.databinding.ActivityWebViewBinding
import com.app.ia.driver.enums.Status
import com.app.ia.driver.utils.AppConstants
import com.app.ia.driver.utils.Resource
import kotlinx.coroutines.Dispatchers

class WebViewViewModel(private val baseRepository: BaseRepository) : BaseViewModel() {

    lateinit var mActivity: Activity
    lateinit var mBinding: ActivityWebViewBinding

    var url = MutableLiveData("")
    var textTitle = MutableLiveData("")

    fun setVariable(mBinding: ActivityWebViewBinding) {
        this.mBinding = mBinding
        this.mActivity = getActivityNavigator()!!
    }

    fun setIntent(intent: Intent) {
        url.value = intent.extras!!.getString(AppConstants.EXTRA_WEBVIEW_URL)!!
        textTitle.value = intent.extras!!.getString(AppConstants.EXTRA_WEBVIEW_TITLE)!!
        title.set(textTitle.value)
    }

    private fun getContentData(requestParams: String) = liveData(Dispatchers.Main) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = baseRepository.getContentData(requestParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun setupObservers(requestParams: String) {

        getContentData(requestParams).observe(mBinding.lifecycleOwner!!, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { users ->

                            val style1 = "<style>@font-face {font-family: 'arial';src: url('fonts/linotte_regular.otf');}body {font-family: 'verdana';}</style>"
                            val style2 = "<link rel=\"stylesheet\" type=\"text/css\" href=\"webview-style.css\">"

                            val head1 = "<head>$style1 \n $style2</head>"
                            val text = "<html>$head1<body style=font-family:arial><div class=\"ql-viewer\">${users.data?.contentData}</div></body></html>"
                            mBinding.webview.loadDataWithBaseURL("file:///android_asset/", text, "text/html", "UTF-8", null)
                        }
                    }
                    Status.ERROR -> {
                        baseRepository.callback.hideProgress()
                        Toast.makeText(mActivity, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {

                    }
                }
            }
        })
    }

}