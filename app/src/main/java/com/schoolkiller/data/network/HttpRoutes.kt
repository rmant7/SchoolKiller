package com.schoolkiller.data.network

object HttpRoutes {
    private const val BASE_URL = "https://generativelanguage.googleapis.com"
    const val UPLOAD = "$BASE_URL/upload"
    const val MODELS = "$BASE_URL/v1beta/models"
}