package com.example.a1011project
data class BusArrivalResponse(
    val odataMetadata: String = "",
    val Services: List<BusService> = emptyList()
)