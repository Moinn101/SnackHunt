package com.moin.snackhunt

data class YelpBusiness(
    val name: String,
    val image_url: String,
    val rating: Double,
    val location: YelpLocation,
    val phone: String,
    val display_phone: String
)

data class YelpLocation(
    val display_address: List<String>
)

data class YelpSearchResponse(
    val businesses: List<YelpBusiness>
)