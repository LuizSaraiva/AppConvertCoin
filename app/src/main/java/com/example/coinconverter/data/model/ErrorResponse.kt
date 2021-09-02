package com.example.coinconverter.data.model

data class ErrorResponse (
    val status: Long,
    val code: Int,
    val message: String
)