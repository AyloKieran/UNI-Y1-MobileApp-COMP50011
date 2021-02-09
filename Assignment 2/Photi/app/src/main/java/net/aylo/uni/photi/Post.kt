package net.aylo.uni.photi

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/*
    Creates data class that API data can be objected into.
    'SerializedName's to get API naming to fit with Java/Kotlin naming conventions

    Serializable so that the object is persisted into disk - so that that the recyclerview can pass the object to another view - rather than its byte-stream.
 */
data class Post(
    val id: Int,
    val title: String,
    val description: String,
    val image: String,
    val likes: Int,
    val lat: Double,
    val lon: Double,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
) : Serializable