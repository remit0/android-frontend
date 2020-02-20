package com.example.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import org.json.JSONObject
import java.io.*


// const val BASE_URL = "http://35.180.115.236/"   // prod
const val BASE_URL = "http://10.0.2.2:8000/" // local

data class Product(
    /* required */
    var name: String,
    /* optional */
    var year: String? = null,
    var store: String? = null,
    var type: String? = null,
    var vol:String? = null,
    var id: String? = null
) : Serializable

data class Rating(
    /* required */
    var product: Product,
    var value: String,
    /* optional */
    var comment: String? = null,
    /* auto filled */
    val date: String? = null,
    val id: String? = null
) : Serializable
    {
        fun serialize(): Map<String, String>{
            /* Serializes a Rating object to a Map in order to send it to the API */
            val params = mutableMapOf<String, String>()
            /* required */
            params.put("name", product.name)
            params.put("value", value)
            /* optional */
            comment?.let{ if (comment != "") params.put("comment", comment.toString()) }
            product.year?.let{ if (product.year != "-") params.put("year", product.year.toString()) }
            product.store?.let{ if (product.store != "-") params.put("store", product.store.toString()) }
            product.type?.let{ if (product.type != "-") params.put("type", product.type.toString()) }
            product.vol?.let{ if (product.vol != "-") params.put("vol", product.vol.toString()) }
            return params.toMap()
        }
    }


class API (context: Context) {

    /* Connects and makes requests to the backend */

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
            "PUT" -> { requestBuilder.put(this.createForm(formParams!!)) }
            else -> throw NotImplementedError("Available methods : ['GET', 'POST', 'DELETE', 'PUT']")
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
            val bodyStr = response.body()!!.string()
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

    fun updateRating(id: String, params: Map<String, String>): Boolean {
        val formParams = params.toMutableMap()
        formParams.put("id", id)
        val response = this.request(
            endpoint = "rating/", method = "PUT", formParams = formParams
        ) ?: return false
        return true
    }

    fun sendPicture(file: File, product_id: String) {
        val url = BASE_URL + "image/"
        val requestBuilder = Request.Builder().url(url)
        this.addHeaders(requestBuilder, null)
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image", file.name, RequestBody.create(MediaType.parse("image/*"), file)
            )
            .addFormDataPart("product", product_id)
            .build()
        val request = requestBuilder.post(body).build()
        try {
            println(this.client.newCall(request).execute())
        } catch (e: Exception){ println(e.printStackTrace())}
    }

    fun getPicture(product_id: String): Bitmap? {

        try {
            val response = request(
                endpoint = "image/", method = "GET", urlParams = mapOf("id" to product_id)
            )
            val bytes = response!!.body()!!.bytes()
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            return bitmap
        } catch (e: Exception){
            println(e.printStackTrace())
            return null
        }
    }
}


class TokenHandler (context: Context) {

    /* Handles tokens read/write on the local file system */

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
