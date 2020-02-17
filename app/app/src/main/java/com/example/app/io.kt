package com.example.app

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import org.json.JSONObject
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.Serializable


// const val BASE_URL = "http://35.180.115.236/"
const val BASE_URL = "http://10.0.2.2:8000/"


data class Product(
    // required
    val name: String,
    // optional
    val year: String? = null,
    val store: String? = null,
    val type: String? = null,
    val vol:String? = null
) : Serializable

data class Rating(
    // required
    val product: Product,
    val value: String,
    // optional
    val comment: String? = null,
    // auto filled
    val date: String? = null,
    val id: String? = null
) : Serializable
    {
        fun serialize(): Map<String, String>{
            val params = mutableMapOf<String, String>()
            // required
            params["name"] = product.name
            params["value"] = value
            // optional
            comment?.let{ params.put("comment", comment)}
            product.year?.let{ params.put("year", product.year)}
            product.store?.let{ params.put("store", product.store)}
            product.type?.let{ params.put("type", product.type)}
            product.vol?.let{ params.put("vol", product.vol)}
            return params.toMap()
        }
    }


class API (context: Context) {

    private val context: Context = context
    private val client: OkHttpClient = OkHttpClient()
    private var token: String? = null

    init {
        this.token = TokenHandler(this.context).read()
    }

    private fun createForm(parameters: Map<String, String>): MultipartBody {
        val formBuilder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
        parameters.forEach { (param, value) -> formBuilder.addFormDataPart(param, value) }
        return formBuilder.build()
    }

    private fun addHeaders(builder: Request.Builder, headers: Map<String, String>?) {
        headers?.let{
            headers.forEach { (param, value) -> builder.addHeader(param, value) } }
        token?.let{builder.addHeader("Authorization", "Token $token")}
    }

    private fun createURL(endpoint: String, parameters: Map<String, String>?): HttpUrl {
        val url = BASE_URL + endpoint
        val urlBuilder = HttpUrl.parse(url)!!.newBuilder()
        parameters?.let {
            parameters.forEach { (param, value) -> urlBuilder.addQueryParameter(param, value) }
        }
        return urlBuilder.build()
    }

    private fun makeRequest(
        endpoint: String, method: String, urlParams: Map<String, String>? = null,
        formParams: Map<String, String>? = null, headerParams: Map<String, String>? = null
    ): Request {
        val url = this.createURL(endpoint, urlParams)
        val requestBuilder = Request.Builder().url(url)
        when (method) {
            "GET" -> { requestBuilder.get() }
            "POST" -> { requestBuilder.post(this.createForm(formParams!!)) }
            "DELETE" -> { requestBuilder.delete(this.createForm(formParams!!)) }
            else -> throw NotImplementedError("Available methods : ['GET', 'POST', 'DELETE']")
        }
        this.addHeaders(requestBuilder, headerParams)
        return requestBuilder.build()
    }

    private fun request(
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

    fun register(username: String, password: String): Boolean {
        val formParams = mapOf("username" to username, "password" to password)
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
        val formParams = mapOf("username" to username, "password" to password)
        val response = this.request(
            endpoint = "api-auth-token/", method = "POST", formParams = formParams
        ) ?: return false
        return if (response.isSuccessful) {
            val token = JSONObject(response.body()!!.string()).get("token").toString()
            this.token = token
            TokenHandler(this.context).write(token)
            true
        } else {
            println(response.body()?.string())
            false
        }
    }

    fun getRatings(): ArrayList<Rating>? {
        val response = this.request(endpoint = "rating/", method="GET") ?: return null
        return if (response.isSuccessful) {
            var bodyStr = response.body()!!.string()
            val gson = Gson()
            val type = object : TypeToken<ArrayList<Rating>>() {}.type
            return gson.fromJson(bodyStr, type)
        } else {
            null
        }
    }

    fun addRating(rating: Rating): Boolean {
        val params = rating.serialize()
        val response = this.request(
            endpoint = "rating/", method="POST", formParams = params
        ) ?: return false
        return true
    }

    fun deleteRating(id: String): Boolean {
        val response = this.request(
            endpoint = "rating/", method="DELETE", formParams = mapOf("id" to id)
        ) ?: return false
        return true
    }

}


class TokenHandler (context: Context) {

    private val context: Context = context
    private val file = "token"

    fun write(token: String){
        val stream: FileOutputStream = this.context.openFileOutput(this.file, Context.MODE_PRIVATE)
        stream.write(token.toByteArray())
        stream.close()
    }

    fun read(): String?{
        try {
            val stream: FileInputStream = this.context.openFileInput(this.file)
            var c = 0
            var token = ""
            while (stream.read().also { c = it } != -1) {
                token += Character.toString(c.toChar())
            }
            stream.close()
            return token
        } catch(e: Exception){
            return null
        }
    }
}
