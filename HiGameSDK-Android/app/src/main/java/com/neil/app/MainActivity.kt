package com.neil.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import com.higame.sdk.core.api.HiGameSDK
import com.higame.sdk.core.callback.HiGameAdCallback
import com.higame.sdk.core.callback.HiGameInitCallbackAdapter
import com.higame.sdk.core.callback.HiGameLoginCallback
import com.higame.sdk.core.model.AdType
import com.higame.sdk.core.model.LoginParams
import com.higame.sdk.core.model.LoginType
import com.higame.sdk.core.utils.log.HiLogger
import com.neil.gamesdk.R

class MainActivity : AppCompatActivity() {
    private lateinit var logTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        HiGameSDK.getInstance().setActivity(this)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        logTextView = findViewById(R.id.logTextView)
        HiGameSDK.getInstance().setAdListener(object :HiGameAdCallback{
            override fun onAdLoaded(message: String?) {
               appendLog("广告加载成功:$message")
            }

            override fun onAdFailedToLoad(code: Int?, message: String?) {
                appendLog("广告加载失败:$code $message")
            }

            override fun onAdShow() {
               appendLog("广告展示")
            }

            override fun onCancel() {
               appendLog("广告取消")
            }

            override fun onAdClosed() {
               appendLog("广告关闭")
            }

            override fun onAdClicked() {
               appendLog("广告点击")
            }

            override fun onAdImpression() {
              appendLog("广告曝光")
            }

            override fun onAdFailedToShow(message: String?) {
               appendLog("广告展示失败:$message")
            }

        })
        // 初始化按钮点击事件
        findViewById<Button>(R.id.initButton).setOnClickListener {
            appendLog("开始初始化SDK...")

            HiGameSDK.getInstance().init(this, object : HiGameInitCallbackAdapter() {
                override fun onInitStart() {
                    appendLog("初始化开始")
                }

                override fun onInitComplete() {
                    appendLog("初始化完成")
                }

                override fun onSuccess(message: String, data: Any?) {
                    appendLog("初始化成功：$message  data: ${data}")
                }

                override fun onError(code: Int, message: String, data: Any?) {
                    appendLog("初始化失败：[$code] $message  data${data}")
                }
            })
        }

        // 登录按钮点击事件
        findViewById<Button>(R.id.loginButton).setOnClickListener {
            appendLog("开始登录...")
            HiGameSDK.getInstance()
                .login(false, params = LoginParams.PlatformLogin(LoginType.GOOGLE), object : HiGameLoginCallback {
                    override fun onCancel() {
                        appendLog("登录已取消")
                    }

                    override fun onSuccess(message: String, data: Any?) {
                        appendLog("登录成功：$message")
                    }

                    override fun onError(code: Int, message: String, data: Any?) {
                        HiLogger.e("登录失败：[$code] $message")
                        appendLog("登录失败：[$code] $message")
                    }
                })
        }

        // 支付按钮点击事件
        findViewById<Button>(R.id.paymentButton).setOnClickListener {
            appendLog("暂未实现支付功能")
            // TODO: 实现支付功能
        }

        // 广告按钮点击事件
        findViewById<Button>(R.id.adButton).setOnClickListener {
           // appendLog("暂未实现广告功能")
            HiGameSDK.getInstance().loadAd()
            appendLog("广告加载")
            // TODO: 实现广告功能
        }
        findViewById<Button>(R.id.adButton_show).setOnClickListener{
        HiGameSDK.getInstance().showAd(adType = AdType.BANNER)
            appendLog("广告展示")
        }
        findViewById<Button>(R.id.adButton_dismiss).setOnClickListener{
            HiGameSDK.getInstance().closeAd(adType = AdType.BANNER)
            appendLog("广告关闭")
        }
        findViewById<Button>(R.id.adButton_inter).setOnClickListener{
            HiGameSDK.getInstance().showAd(adType = AdType.INTERSTITIAL)
        }

    }

    private fun appendLog(message: String) {
        runOnUiThread {
            logTextView.append("${message}\n")
            // 自动滚动到底部
            val scrollAmount = logTextView.layout?.let { layout ->
                layout.getLineTop(logTextView.lineCount) - logTextView.height
            } ?: 0
            if (scrollAmount > 0) {
                logTextView.scrollTo(0, scrollAmount)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        HiGameSDK.getInstance().onResume()
    }

    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            HiGameSDK.getInstance().onNewIntent(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        HiGameSDK.getInstance().onActivityResult(requestCode, resultCode, data)
    }
}