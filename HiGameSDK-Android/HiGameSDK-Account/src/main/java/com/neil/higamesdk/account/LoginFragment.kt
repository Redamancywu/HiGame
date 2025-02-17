package com.neil.higamesdk.account

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment

class LoginDialogFragment : DialogFragment() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText

    interface OnLoginInteractionListener {
        fun onLoginClicked(username: String, password: String)
        fun onGuestLoginClicked()
        fun onSocialLoginClicked(provider: SocialProvider)
    }

    enum class SocialProvider {
        GOOGLE, FACEBOOK, WECHAT, QQ
    }

    private var listener: OnLoginInteractionListener? = null

    companion object {
        fun newInstance(): LoginDialogFragment {
            return LoginDialogFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.hi_login_fragment, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
        setupClickListeners()
    }

    private fun setupViews(view: View) {
        etUsername = view.findViewById(R.id.username)
        etPassword = view.findViewById(R.id.password)
    }

    private fun setupClickListeners() {
        view?.apply {
            findViewById<View>(R.id.login_button).setOnClickListener {
                handleLoginClick()
            }

            findViewById<View>(R.id.guest_login).setOnClickListener {
                listener?.onGuestLoginClicked()
                dismiss()
            }

            setupSocialLoginListeners()
        }
    }

    private fun setupSocialLoginListeners() {
        view?.apply {
            findViewById<ImageButton>(R.id.google_login).setOnClickListener {
                listener?.onSocialLoginClicked(SocialProvider.GOOGLE)
                dismiss()
            }

            findViewById<ImageButton>(R.id.facebook_login).setOnClickListener {
                listener?.onSocialLoginClicked(SocialProvider.FACEBOOK)
                dismiss()
            }

            findViewById<ImageButton>(R.id.wechat_login).setOnClickListener {
                listener?.onSocialLoginClicked(SocialProvider.WECHAT)
                dismiss()
            }

            findViewById<ImageButton>(R.id.qq_login).setOnClickListener {
                listener?.onSocialLoginClicked(SocialProvider.QQ)
                dismiss()
            }
        }
    }

    private fun handleLoginClick() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (username.isNotEmpty() && password.isNotEmpty()) {
            listener?.onLoginClicked(username, password)
            dismiss()
        } else {
            // 可以添加输入验证提示
            if (username.isEmpty()) etUsername.error = "用户名不能为空"
            if (password.isEmpty()) etPassword.error = "密码不能为空"
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                (360 * resources.displayMetrics.density).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    fun setLoginInteractionListener(listener: OnLoginInteractionListener) {
        this.listener = listener
    }
}