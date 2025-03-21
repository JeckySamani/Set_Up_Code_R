@file:Suppress("DEPRECATION", "UNREACHABLE_CODE")

package com.app.assignment.network

import android.accounts.NetworkErrorException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.ParseException
import android.util.Log
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.app.assignment.R
import com.app.assignment.helpers.SharedPrefManager
import com.app.assignment.helpers.Utils
import com.app.assignment.ui.SplashActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class NetworkCall(
    private val context: Context,
    private val url: String,
    private val postFilesMulti: HashMap<String, List<File>> = HashMap(),
    private val postFiles: HashMap<String, File> = HashMap(),
    private val postData: HashMap<String, String> = HashMap(),
    private val headers: HashMap<String, String> = HashMap(),
    private val json: JSONObject = JSONObject(),
    private val type: String = "POST",
    private val isShow: Boolean,
    private val request: MultiPartRequest
) {

    private val isConnectingToInternet: Boolean
        get() {
            val connectivityManager: ConnectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info: Array<NetworkInfo> = connectivityManager.allNetworkInfo
            for (anInfo in info) if (anInfo.state == NetworkInfo.State.CONNECTED) return true
            return false
        }

    private fun sendData() {

        if (Utils.isDebug)
            Log.e("url ==>", url)

        /*if (SharedPrefManager(HiltApplication.context!!).getToken()!! != "") {
            //headers[Parameters.token] = SharedPrefManager(context).getToken()!!
            val bearerToken = SharedPrefManager(context).getToken()!!
            headers["Authorization"] = "Bearer $bearerToken"
        }else
            headers[Parameters.apikey] = API_KEY

        if (type == "POST" || type == "PUT") {
            if (SharedPrefManager(context).getUserId()!!.isNotEmpty()) {
                if (json.length() > 0)
                    json.put(Parameters.vendorId , SharedPrefManager(context).getUserId()!!)
                else
                    postData[Parameters.vendorId] = SharedPrefManager(context).getUserId()!!
            }
        }*/

//        val httpClient = OkHttpClient.Builder()
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(0, TimeUnit.SECONDS) // No connect timeout
            .readTimeout(0, TimeUnit.SECONDS)    // No read timeout
            .writeTimeout(0, TimeUnit.SECONDS) // No write timeout
        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .method(original.method, original.body)

            if (type != "GET") {
                headers.forEach { (key, value) ->
                    requestBuilder.addHeader(key, value)

                    if (Utils.isDebug)
                        Log.e("header ==>", "$key-$value")
                }
            }
            chain.proceed(requestBuilder.build())
        }

        if (json.length() > 0)
            Log.e("jsonData ==>", json.toString())
        for (key in postData.keys) {
            val value = postData[key]
            if (Utils.isDebug)
                Log.e("postData ==>", "$key-$value")
        }
        for (key in postFiles.keys) {
            val value = postFiles[key]
            if (Utils.isDebug)
                Log.e("postFiles ==>", "$key-$value")
        }

        val okHttpClient = httpClient.build()
        val gsonRetrofit = Retrofit.Builder()
            .baseUrl(NetworkConstants.API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val gsonAPI = gsonRetrofit.create(API::class.java)

        val call: Call<ResponseBody> = when {
            type == "GET" -> {
                Log.e("Check--" , "121212121212121212121212121212")
                gsonAPI.getRequest(url.replace(NetworkConstants.API_URL, ""), postData, headers)
            }

            type == "PUT" -> {
                when {

                    headers["Content-Type"] == "application/x-www-form-urlencoded" -> {
                        gsonAPI.putFormRequest(url.replace(NetworkConstants.API_URL, ""), postData)
                    }
                    json.length() > 0 -> {
                        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())
                        gsonAPI.putJsonRequest(url.replace(NetworkConstants.API_URL, ""), requestBody)
                    }
                    else -> {
                        val postDataRequest = postData.mapValues {
                            RequestBody.create("text/plain".toMediaTypeOrNull(), it.value)
                        }
                        val postImages = postFiles.map {
                            MultipartBody.Part.createFormData(it.key, it.value.name, RequestBody.create("image/*".toMediaTypeOrNull(), it.value))
                        }
                        gsonAPI.putMultipartRequest(url.replace(NetworkConstants.API_URL, ""), postImages, HashMap(postDataRequest))
                    }

                }
            }

            type == "DELETE" -> {
                when {
                    json.length() > 0 -> {
                        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())
                        gsonAPI.deleteBodyRequest(url.replace(NetworkConstants.API_URL, ""), requestBody)
                    }
                    else -> {
                        gsonAPI.deleteRequest(url.replace(NetworkConstants.API_URL, ""), postData)
                    }
                }
            }

            headers["Content-Type"] == "application/x-www-form-urlencoded" -> {
                gsonAPI.postRequest(url.replace(NetworkConstants.API_URL, ""), postData)
            }
            json.length() > 0 -> {
                val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())
                gsonAPI.postBodyRequest(url.replace(NetworkConstants.API_URL, ""), requestBody)
            }
            else -> {
                val postDataRequest = postData.mapValues {
                    RequestBody.create("text/plain".toMediaTypeOrNull(), it.value)
                }
                val postImages = ArrayList<MultipartBody.Part>()
                val postImagesOne = postFiles.map {
                    MultipartBody.Part.createFormData(it.key, it.value.name,
                        RequestBody.create("image/*".toMediaTypeOrNull(), it.value))
                }
                postImages.addAll(postImagesOne)
                for(x in postFilesMulti){
                    for (y in x.value){
                        postImages.add(
                            MultipartBody.Part.createFormData(x.key, y.name,
                                RequestBody.create("image/*".toMediaTypeOrNull(), y))
                        )
                    }
                }
                gsonAPI.postRequest(url.replace(NetworkConstants.API_URL, ""),
                    postImages, HashMap(postDataRequest))
            }
        }

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                dismissPopup()
                val statusCode = response.code()
                val responseBody = response.body()?.string() ?: response.errorBody()?.string()

                if (Utils.isDebug) {
                    Log.e("responseCode ==>", statusCode.toString())
                    val maxLogSize = 1000
                }
                request.myResponseMethod(responseBody ?: "", statusCode)
            }

            override fun onFailure(call: Call<ResponseBody>, error: Throwable) {
                dismissPopup()
                val message = when (error) {
                    is NetworkErrorException -> "Please check your internet connection"
                    is ParseException -> "Parsing error! Please try again after some time!"
                    is TimeoutException -> "Connection TimeOut! Please check your internet connection."
                    is UnknownHostException -> "Please check your internet connection and try later"
                    else -> error.message ?: "Unknown error"
                }

                if (Utils.isDebug) {
                    Log.e("response ==>", message)
                }

                request.myResponseMethod("", 0)
            }
        })
    }

    private fun dismissPopup() {
        if (pDialog?.isShowing == true) {
            pDialog?.dismiss()
        }
    }

    private var pDialog: SweetAlertDialog? = null

    private fun showProgress() {
        pDialog = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE).apply {
            progressHelper.barColor = ContextCompat.getColor(context, R.color.colorPrimary)
            titleText = context.resources.getString(R.string.loading)
            setCancelable(false)
        }
        if (isShow) {
            pDialog?.show()
        }
    }

    init {
        if (isConnectingToInternet) {
            showProgress()
            sendData()
        } else {
            Utils.showToast(context, context.resources.getString(R.string.no_internet))
        }
    }

    interface MultiPartRequest {
        fun myResponseMethod(response: String, statusCode: Int)
    }

    fun logout() {
        SharedPrefManager(context).clear()
        val intent = Intent(context, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }

}


