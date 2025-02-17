package com.higame.sdk.core.module

import android.content.Context
import com.higame.sdk.core.config.HiGameConfig
import com.higame.sdk.core.model.PaymentParams
import com.higame.sdk.core.model.PaymentResult

interface IPaymentModule : IModule {
    /**
     * 发起支付
     * @param params 支付参数
     * @return 支付结果
     */
    fun pay(params: PaymentParams): PaymentResult

    /**
     * 查询订单状态
     * @param orderId 订单ID
     * @return 支付结果
     */
    fun queryOrder(orderId: String): PaymentResult

    /**
     * 获取支持的支付方式列表
     * @return 支付方式列表
     */
    fun getSupportedPaymentMethods(): List<String>
}