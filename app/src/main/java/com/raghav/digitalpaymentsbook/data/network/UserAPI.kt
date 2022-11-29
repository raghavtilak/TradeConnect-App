package com.raghav.digitalpaymentsbook.data.network

import com.raghav.digitalpaymentsbook.data.model.Customer
import com.raghav.digitalpaymentsbook.data.model.Retailer
import com.raghav.digitalpaymentsbook.data.model.Transaction
import com.raghav.digitalpaymentsbook.data.model.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface UserAPI {

    @POST("")
    suspend fun isCustomer(phone:String):Response<Customer>

    @POST("")
    suspend fun isRetailer(phone:String):Response<Retailer>

    @POST("")
    suspend fun createCustomer(customer: Customer):Response<Customer>

    @POST("")
    suspend fun createRetailer(retailer: Retailer):Response<Retailer>

    @POST("")
    suspend fun getAllCustomers(retailer: Retailer):Response<List<Customer>>

    @POST("")
    suspend fun getAllRetailers(customer: Customer):Response<List<Retailer>>

    @POST("")
    suspend fun addTransaction(transaction: Transaction):Response<Transaction>

    @POST("")
    suspend fun getAllPendingTransactions(custId:Int,retId:Int):Response<List<Transaction>>

    @POST("")
    suspend fun getAllSettledTransactions(custId:Int,retId:Int):Response<List<Transaction>>



}