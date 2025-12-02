package com.example.mediscanmain.data.api

data class GuidelinesResponse(
    val data: List<Guideline>
)

data class Guideline(
    val age_group: String,
    val recommendation: String,
    val activity_type: String
)
