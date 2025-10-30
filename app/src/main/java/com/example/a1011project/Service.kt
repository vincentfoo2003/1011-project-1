package com.example.a1011project

data class BusService(
    val ServiceNo: String,
    val Operator: String,
    val NextBus: BusArrivalInfo,
    val NextBus2: BusArrivalInfo,
    val NextBus3: BusArrivalInfo
)