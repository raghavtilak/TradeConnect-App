package com.raghav.digitalpaymentsbook.data.network

import com.raghav.digitalpaymentsbook.data.model.*
import com.raghav.digitalpaymentsbook.data.model.apis.RetailerSignIn
import com.raghav.digitalpaymentsbook.data.model.apis.ServerResponse
import com.raghav.digitalpaymentsbook.data.model.enums.BusinessTypes
import com.raghav.digitalpaymentsbook.data.model.enums.ConnectionStatus
import org.bson.types.ObjectId
import retrofit2.Response
import retrofit2.http.*

interface UserAPI {

//    @FormUrlEncoded
    @GET("get_user")
    suspend fun getUser(@Query("phone") rsign: RetailerSignIn):Response<User>


    @POST("retailer/signin")
    suspend fun retailerSignIn(@Body rsign:RetailerSignIn):Response<String>

    @POST("retailer/signup")
    suspend fun createRetailer(@Body retailer: User):Response<String>


    @POST("customer/signup")
    suspend fun createCustomer(@Body customer: User):Response<String>


    @GET("customer/retailers/{customerId}")
    suspend fun getRetailerOfACustomers(@Path("customerId") customerId: String):Response<List<Retailer>>

    @GET("retailer/customers/{retailerId}")
    suspend fun getCustomersOfARetailer(@Path("retailerId") retailerId: String):Response<List<Customer>>

    @POST("retailer/create_transaction/{retailerId}")
    suspend fun addTransaction(@Body transaction: Transaction,@Path("retailerId")retailerId:String):Response<Transaction>

    @GET("retailer/customer/transactions/{retailerId}")
    suspend fun getAllTransactions(@Path("retailerId")retailerId:String,@Query("customerPhone") custPhone:Long):Response<List<Transaction>>

    @GET("business_types")
    suspend fun getAllBusinessTypes():Response<List<BusinessTypes>>

    @GET("my_connections")
    suspend fun getMyConnections(@Query("status") status: ConnectionStatus):Response<List<Connection>>

    @GET("retailer/search_retailers")
    suspend fun getSearchedRetailers(@Query("advance_query") advance_query:String):Response<List<Retailer>>

    @POST("retailer/create_connection_req")
    suspend fun createConnectionRequest(@Body recipient:String):Response<ServerResponse>

    @POST("retailer/create_connection_req")
    suspend fun updateConnectionReq(@Body status: ConnectionStatus):Response<ServerResponse>

    @GET("retailer/my_sells")
    suspend fun mySells():Response<List<SellItem>>

    @GET("retailer/my_inventory")
    suspend fun myStore():Response<List<StoreItem>>


    @GET("retailer/get_batches_by_id")
    suspend fun getBatchesById(@Query("ids") ids:List<ObjectId>):Response<List<Batch>>


}