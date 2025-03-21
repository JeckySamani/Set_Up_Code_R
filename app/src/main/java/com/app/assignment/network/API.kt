package com.app.assignment.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
interface API {

    @GET("{path}")
    fun getRequest(
        @Path("path", encoded = true) endPoint: String,
        @QueryMap map: HashMap<String, String>,
        @HeaderMap headers: HashMap<String, String>,
    ): Call<ResponseBody>

    @Multipart
    @POST("{path}")
    fun postRequest(
        @Path("path", encoded = true) endPoint: String,
        @Part images: List<MultipartBody.Part>,
        @PartMap partMap: HashMap<String, RequestBody>,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("{path}")
    fun postRequest(
        @Path("path", encoded = true) endPoint: String,
        @FieldMap map: HashMap<String, String>,
    ): Call<ResponseBody>

    @POST("{path}")
    fun postBodyRequest(
        @Path("path", encoded = true) endPoint: String,
        @Body json: RequestBody? = null,
    ): Call<ResponseBody>


    // PUT API Method
    @PUT("{path}")
    fun putJsonRequest(
        @Path("path", encoded = true) endPoint: String,
        @Body json: RequestBody,
        //@HeaderMap headers: HashMap<String, String>
    ): Call<ResponseBody>

    // PUT request for form-urlencoded data
    @FormUrlEncoded
    @PUT("{path}")
    fun putFormRequest(
        @Path("path", encoded = true) endPoint: String,
        @FieldMap map: HashMap<String, String>,
        //@HeaderMap headers: HashMap<String, String>
    ): Call<ResponseBody>

    // PUT request for multipart data
    @Multipart
    @PUT("{path}")
    fun putMultipartRequest(
        @Path("path", encoded = true) endPoint: String,
        @Part images: List<MultipartBody.Part>,
        @PartMap partMap: HashMap<String, RequestBody>,
        //@HeaderMap headers: HashMap<String, String>
    ): Call<ResponseBody>


    // Delete Method
    @DELETE("{path}")
    fun deleteRequest(
        @Path("path", encoded = true) endPoint: String,
        @QueryMap map: HashMap<String, String>? = null,
        //@HeaderMap headers: HashMap<String, String>? = null,
    ): Call<ResponseBody>

    @HTTP(method = "DELETE", path = "{path}", hasBody = true)
    fun deleteBodyRequest(
        @Path("path", encoded = true) endPoint: String,
        @Body json: RequestBody? = null,
    ): Call<ResponseBody>

}