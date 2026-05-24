/**
 * GeminiRepository odpowiada za komunikację z zewnętrznym API Google Gemini.
 * Obsługuje wysyłanie tekstu, obrazów oraz zarządzanie historią rozmowy w ramach sesji.
 */
package com.gemchat.app.data.repository

import com.gemchat.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

class GeminiRepository {

    private val apiKey = BuildConfig.GEMINI_API_KEY
    // Endpoint do generowania treści przez model Gemini 2.0 Flash
    private val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=$apiKey"
    
    // Lokalna historia wiadomości wysyłana przy każdym zapytaniu (dla kontekstu AI)
    private val history = mutableListOf<JSONObject>()

    /** Funkcja pomocnicza do akceptowania wszystkich certyfikatów SSL (użyteczna przy problemach z połączeniem). */
    private fun trustAll() {
        val tm = object : X509TrustManager {
            override fun checkClientTrusted(c: Array<X509Certificate>, a: String) {}
            override fun checkServerTrusted(c: Array<X509Certificate>, a: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }
        val sc = SSLContext.getInstance("TLS")
        sc.init(null, arrayOf(tm), SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
        HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
    }

    /**
     * Wysyła wiadomość do AI. Obsługuje tekst oraz opcjonalny obraz zakodowany w Base64.
     * text: treść zapytania
     * imageBase64: opcjonalny obraz (String)
     * mimeType: typ pliku (np. "image/jpeg")
     */
    suspend fun sendMessage(text: String, imageBase64: String? = null, mimeType: String? = null): String = withContext(Dispatchers.IO) {
        try {
            trustAll()

            // Tworzenie części zapytania (tekst + opcjonalny obraz)
            val parts = JSONArray().put(JSONObject().put("text", text))
            if (imageBase64 != null && mimeType != null) {
                parts.put(JSONObject().put("inline_data", JSONObject().apply {
                    put("mime_type", mimeType)
                    put("data", imageBase64)
                }))
            }

            // Dodanie wiadomości użytkownika do lokalnej historii sesji
            val userMsg = JSONObject().apply {
                put("role", "user")
                put("parts", parts)
            }
            history.add(userMsg)

            // Budowanie pełnego body zapytania JSON
            val body = JSONObject().put("contents", JSONArray(history))

            // Ręczne wykonanie zapytania HTTP POST bez użycia ciężkich bibliotek typu Retrofit
            val url = URL(endpoint)
            val conn = url.openConnection() as HttpsURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 60000
            conn.readTimeout = 60000

            OutputStreamWriter(conn.outputStream).use { it.write(body.toString()) }

            val responseCode = conn.responseCode
            val response = if (responseCode == 200) {
                conn.inputStream.bufferedReader().readText()
            } else {
                val errorBody = conn.errorStream?.bufferedReader()?.readText() ?: "no error"
                return@withContext "HTTP $responseCode: $errorBody"
            }

            // Parsowanie odpowiedzi JSON od Google
            val json = JSONObject(response)
            val responseText = json
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")

            // Dodanie odpowiedzi AI do historii, aby model "pamiętał" co napisał wcześniej
            val modelMsg = JSONObject().apply {
                put("role", "model")
                put("parts", JSONArray().put(JSONObject().put("text", responseText)))
            }
            history.add(modelMsg)

            responseText
        } catch (e: Exception) {
            e.printStackTrace()
            "Error: ${e.javaClass.simpleName}: ${e.message}"
        }
    }

    /** Czyści historię sesji AI. */
    fun resetChat() {
        history.clear()
    }
}