package com.example.mediscanmain.data.api

import retrofit2.Response
import retrofit2.http.GET

interface PhysicalActivityApi {
    @GET("physical-activity-guidelines")
    suspend fun getGuidelines(): Response<GuidelinesResponse>
}
