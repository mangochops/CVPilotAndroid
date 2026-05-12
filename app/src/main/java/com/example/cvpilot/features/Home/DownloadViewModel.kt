package com.example.cvpilot.features.Home

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.example.cvpilot.network.SupabaseManager
import io.github.jan.supabase.auth.auth
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.io.File
import java.io.FileOutputStream

suspend fun downloadTailoredResume(
    context: Context,
    jobDescription: String,
    resumeText: String
) {
    try {
        Log.d("DOWNLOAD_DEBUG", "Starting Resume Tailoring Request")

        // 1. Get the session token (as done in CoverLetterViewModel)
        val session = SupabaseManager.client.auth.currentSessionOrNull()
        val token = session?.accessToken

        if (token == null) {
            Log.e("DOWNLOAD_ERROR", "No Auth Token found")
            return
        }

        // 2. Use httpClient directly to avoid the serialization error
        val response = SupabaseManager.client.httpClient.post("https://eocldmwhgovgdhuttwgs.supabase.co/functions/v1/tweak-resume") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $token")

            // Setting the body as a JsonObject (httpClient handles this better)
            setBody(buildJsonObject {
                put("jobDescription", jobDescription)
                put("resumeText", resumeText)
            })
        }

        if (!response.status.isSuccess()) {
            Log.e("DOWNLOAD_ERROR", "Server returned error: ${response.status}")
            return
        }

        // 3. Get bytes from the response
        val pdfBytes = response.bodyAsBytes()

        if (pdfBytes.isEmpty()) {
            Log.e("DOWNLOAD_ERROR", "Received empty byte array from server")
            return
        }

        val fileName = "Tailored_Resume_${System.currentTimeMillis()}.pdf"

        // 4. Save to Downloads (Your existing working logic)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let { targetUri ->
                context.contentResolver.openOutputStream(targetUri)?.use { it.write(pdfBytes) }
            }
        } else {
            val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadDir.exists()) downloadDir.mkdirs()

            val file = File(downloadDir, fileName)
            FileOutputStream(file).use { it.write(pdfBytes) }
            android.media.MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), null, null)
        }

        Log.d("DOWNLOAD_SUCCESS", "Resume saved successfully as $fileName")

    } catch (e: Exception) {
        Log.e("DOWNLOAD_ERROR", "Failed to save PDF: ${e.message}", e)
    }
}