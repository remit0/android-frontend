package com.example.app

import okhttp3.*
import org.json.JSONObject
import java.io.IOException


const val BASE_URL = "http://10.0.2.2:8000/"


class API {

    private val client: OkHttpClient = OkHttpClient()
    private var token: String? = null

    init {
        this.loadToken()
    }

    fun createForm(parameters: Map<String, String>): MultipartBody {
        val formBuilder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
        parameters.forEach { (param, value) -> formBuilder.addFormDataPart(param, value) }
        return formBuilder.build()
    }

    fun addHeaders(builder: Request.Builder, headers: Map<String, String>) {
        headers.forEach { (param, value) -> builder.addHeader(param, value) }
        token?.let{builder.addHeader("Authorization", "Token $token")}
    }

    fun createURL(endpoint: String, parameters: Map<String, String>?): HttpUrl {
        val url = BASE_URL + endpoint
        val urlBuilder = HttpUrl.parse(url)!!.newBuilder()
        parameters?.let {
            parameters.forEach { (param, value) -> urlBuilder.addQueryParameter(param, value) }
        }
        return urlBuilder.build()
    }

    fun makeRequest(
        endpoint: String, method: String, urlParams: Map<String, String>? = null,
        formParams: Map<String, String>? = null, headerParams: Map<String, String>? = null
    ): Request {
        val url = this.createURL(endpoint, urlParams)
        val requestBuilder = Request.Builder().url(url)
        when (method) {
            "GET" -> { requestBuilder.get() }
            "POST" -> { requestBuilder.post(this.createForm(formParams!!)) }
            // TODO "PUT", "DELETE"
            else -> throw NotImplementedError("Available methods : ['GET', 'POST']")
        }
        headerParams?.let { this.addHeaders(requestBuilder, headerParams) }
        return requestBuilder.build()
    }

    fun request(
        endpoint: String, method: String, urlParams: Map<String, String>? = null,
        formParams: Map<String, String>? = null, headerParams: Map<String, String>? = null
    ): Response? {
        val request = this.makeRequest(endpoint, method, urlParams, formParams, headerParams)
        return try {
            this.client.newCall(request).execute()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun saveToken(token: String){
        // TODO
    }

    private fun loadToken(){
        // TODO
    }

    fun register(username: String, password: String): Boolean {
        val formParams = mapOf<String, String>("username" to username, "password" to password)
        val response = this.request(endpoint = "user/", method = "POST", formParams = formParams)
            ?: return false
        return if (response.isSuccessful) {
            true
        } else {
            println(response.body()?.string())
            false
        }
    }

    fun login(username: String, password:String): Boolean{
        val formParams = mapOf<String, String>("username" to username, "password" to password)
        val response = this.request(
            endpoint = "api-auth-token/", method = "POST", formParams = formParams
        ) ?: return false
        return if (response.isSuccessful) {
            val token = JSONObject(response.body()!!.string()).get("token").toString()
            this.token = token
            this.saveToken(token)
            true
        } else {
            println(response.body()?.string())
            false
        }
    }

    fun getRatings() {
        // TODO
    }

    fun getProducts() {
        // TODO
    }

}
