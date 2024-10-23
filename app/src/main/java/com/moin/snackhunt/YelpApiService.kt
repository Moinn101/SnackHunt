package com.moin.snackhunt

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface YelpApiService {
    @GET("v3/businesses/search")
    suspend fun searchBusinesses(
        @Query("term") term: String,
        @Query("location") location: String,
        @Header("Authorization") authorization: String = "Bearer ${"ulOsQFPmJA5a3RlWBaqPubJ6gusvNQG-J3kkCp_u7E8x6k7V_wLZaSyTe5bliQ0c5S0NSlnBmSahSLm44PMToWjZNTXwP5OfKJAdmOx0KYuMKUBkG623qVBGU2EWZ3Yx"}"
    ): Response<YelpSearchResponse>
}