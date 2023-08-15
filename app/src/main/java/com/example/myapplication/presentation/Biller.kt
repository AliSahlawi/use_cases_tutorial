package com.example.myapplication.presentation

data class Biller(
    val billerName: String,
    val billerCode: String
)
data class ServiceDetail(
    val serviceName: String,
    val serviceCode: String,
    val subServiceCode: String,
)