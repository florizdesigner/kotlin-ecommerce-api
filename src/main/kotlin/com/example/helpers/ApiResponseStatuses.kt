package com.example.helpers

enum class ApiResponseStatuses (val status: String) {
    SUCCESS(status = "success"),
    FAILED(status = "failed"),
    ERROR(status = "error")
}