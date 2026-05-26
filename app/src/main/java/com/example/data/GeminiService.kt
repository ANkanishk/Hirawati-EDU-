package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    // Relying on modern gemini-3.5-flash as default per skill guidelines
    private const val MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent"

    // Set 60 seconds timeout as strictly required by 'gemini-api' skill to prevent timeouts
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateContent(prompt: String, systemInstruction: String? = null): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Error: Gemini API Key is missing! Ask your developer to enter their API Key via the Google AI Studio Secrets/Environment Variables tab."
        }

        try {
            // Programmatically construct JSON to ensure total immunity from serialization adapter issues
            val requestBodyJson = JSONObject()
            
            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            val partsArray = JSONArray()
            val partObj = JSONObject()
            partObj.put("text", prompt)
            partsArray.put(partObj)
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            requestBodyJson.put("contents", contentsArray)

            if (systemInstruction != null) {
                val sysInstObj = JSONObject()
                val sysPartsArray = JSONArray()
                val sysPartObj = JSONObject()
                sysPartObj.put("text", systemInstruction)
                sysPartsArray.put(sysPartObj)
                sysInstObj.put("parts", sysPartsArray)
                requestBodyJson.put("systemInstruction", sysInstObj)
            }

            val configObj = JSONObject()
            configObj.put("temperature", 0.7)
            requestBodyJson.put("generationConfig", configObj)

            val bodyText = requestBodyJson.toString()
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = bodyText.toRequestBody(mediaType)

            val url = "$BASE_URL?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Content-Type", "application/json")
                .build()

            client.newCall(request).execute().use { response ->
                val responseStr = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    Log.e("GeminiService", "API Error: ${response.code} $responseStr")
                    return@withContext "API Connection Error (Code ${response.code})."
                }

                val responseJson = JSONObject(responseStr)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val resContentObj = candidate.optJSONObject("content")
                    val parts = resContentObj?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text", "No response text found.")
                    }
                }
                "Response was parsed successfully, but did not return any candidates."
            }
        } catch (e: Exception) {
            Log.e("GeminiService", "Exception calling Gemini", e)
            "Failed contact: ${e.localizedMessage ?: "Unknown connection or timeout exception"}"
        }
    }
}
