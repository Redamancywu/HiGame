package com.higame.sdk.core.model

data class LoginSuccessInfo(
    val token: String,
    val gameAccountId: String,
    val guestId: String
)