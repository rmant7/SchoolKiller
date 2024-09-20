package com.schoolkiller.data_Layer.network.api

object HttpRoutes {
    private const val BASE_URL = "https://generativelanguage.googleapis.com"
    const val UPLOAD = "$BASE_URL/upload"
    const val MODELS = "$BASE_URL/v1beta/models"
}