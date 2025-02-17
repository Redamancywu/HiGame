package com.higame.sdk.core.model

data class UserInfo(
    val userId: String,
    val username: String,
    val nickname: String? = null,
    val avatar: String? = null,
    val token: String,
    val bindAccounts: List<String> = emptyList()
)