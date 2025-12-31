package com.app.beloz.apis.services

import com.app.beloz.data.models.DatosBancarios
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentApi {
    @POST("/api/payment/save")
    suspend fun savePaymentData(@Body datosBancarios: DatosBancarios): Response<Unit>

    @GET("/api/payment/{userId}")
    suspend fun getPaymentData(@Path("userId") userId: Int): Response<DatosBancarios>
}
