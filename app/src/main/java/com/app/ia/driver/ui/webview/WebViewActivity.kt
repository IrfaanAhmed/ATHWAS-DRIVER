package com.app.ia.driver.ui.webview

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Bundle
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import com.app.ia.driver.R
import com.app.ia.driver.BR
import com.app.ia.driver.ViewModelFactory
import com.app.ia.driver.apiclient.RetrofitFactory
import com.app.ia.driver.base.BaseActivity
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.databinding.ActivityWebViewBinding
import com.app.ia.driver.dialog.DriverDialog
import com.app.ia.driver.utils.setOnApplyWindowInset
import kotlinx.android.synthetic.main.activity_web_view.*
import kotlinx.android.synthetic.main.common_header.view.*

class WebViewActivity : BaseActivity<ActivityWebViewBinding, WebViewViewModel>() {

    private var mBinding: ActivityWebViewBinding? = null
    private var mViewModel: WebViewViewModel? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_web_view
    }

    override fun getViewModel(): WebViewViewModel {
        return mViewModel!!
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mBinding!!)
        mViewModel?.setIntent(intent)

        //makeStatusBarTransparent()
        setOnApplyWindowInset(toolbar, content_container)
        //toolbar.imageViewIcon.invisible()

        webview.webViewClient = webViewClient
        webview.settings.javaScriptEnabled = true
        //webview.loadUrl("http://www.google.com")
        mViewModel?.setupObservers(mViewModel?.url!!.value!!)

    }

    private fun setViewModel() {
        val factory = ViewModelFactory(WebViewViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(WebViewViewModel::class.java)
    }

    private var webViewClient = object : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            return false
        }

        override fun onPageFinished(view: WebView, url: String) {

        }

        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {

            var message = getString(R.string.ssl_error_title)
            when (error!!.primaryError) {
                SslError.SSL_UNTRUSTED -> message = getString(R.string.ssl_untrusted)
                SslError.SSL_EXPIRED -> message = getString(R.string.ssl_expired)
                SslError.SSL_IDMISMATCH -> message = getString(R.string.ssl_id_mismatch)
                SslError.SSL_NOTYETVALID -> message = getString(R.string.ssl_not_yet_valid)
            }
            message += getString(R.string.ssl_do_you_want_continue)

            val dialog = DriverDialog(this@WebViewActivity, getString(R.string.ssl_error_title), message, getString(R.string.ok), getString(R.string.cancel), false)
            dialog.setOnItemClickListener(object : DriverDialog.OnClickListener {
                override fun onPositiveClick() {
                    handler!!.proceed()
                }

                override fun onNegativeClick() {
                    handler!!.cancel()
                }

            })
        }
    }
}