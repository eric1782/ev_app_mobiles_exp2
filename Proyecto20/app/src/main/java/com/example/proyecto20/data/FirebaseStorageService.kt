package com.example.proyecto20.data

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.util.UUID

object FirebaseStorageService {
    
    private val storage = FirebaseStorage.getInstance()
    private val ejerciciosRef: StorageReference = storage.reference.child("ejercicios")
    
    /**
     * Sube un video o GIF a Firebase Storage
     * @param fileUri URI del archivo a subir
     * @param entrenadorId ID del entrenador (para organizar los archivos)
     * @return URL de descarga del archivo subido
     */
    suspend fun uploadVideoOrGif(
        fileUri: Uri,
        entrenadorId: String
    ): String {
        try {
            // Generar nombre único para el archivo
            val fileName = "${UUID.randomUUID()}_${System.currentTimeMillis()}"
            val fileExtension = getFileExtension(fileUri.toString())
            val fileRef = ejerciciosRef.child(entrenadorId).child("$fileName.$fileExtension")
            
            // Subir el archivo
            val uploadTask = fileRef.putFile(fileUri)
            uploadTask.await()
            
            // Obtener la URL de descarga
            val downloadUrl = fileRef.downloadUrl.await()
            return downloadUrl.toString()
        } catch (e: Exception) {
            throw Exception("Error al subir el archivo: ${e.message}", e)
        }
    }
    
    /**
     * Elimina un archivo de Firebase Storage
     * @param fileUrl URL del archivo a eliminar
     */
    suspend fun deleteFile(fileUrl: String) {
        try {
            val storageRef = storage.getReferenceFromUrl(fileUrl)
            storageRef.delete().await()
        } catch (e: Exception) {
            // Si el archivo no existe o hay error, no hacer nada
            // (no queremos que falle la eliminación del ejercicio)
        }
    }
    
    private fun getFileExtension(fileName: String): String {
        return fileName.substringAfterLast('.', "mp4")
    }
}

