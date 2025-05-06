package com.example.app.util

import android.content.Context
import android.net.Uri
import java.io.File

fun uriToFile(context: Context, uri: Uri): File {
    val fileName = getFileName(context, uri)
    val tempFile = File(context.cacheDir, fileName)

    context.contentResolver.openInputStream(uri)?.use { inputStream ->
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
    return tempFile
}

fun getMimeType(context: Context, uri: Uri): String? {
    return context.contentResolver.getType(uri)
}

private fun getFileName(context: Context, uri: Uri): String {
    var fileName = "file_${System.currentTimeMillis()}"

    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex("_display_name")
            if (nameIndex != -1) {
                fileName = cursor.getString(nameIndex)
            }
        }
    }

    return fileName
}
