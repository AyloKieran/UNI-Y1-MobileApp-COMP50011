package net.aylo.uni.photi

import retrofit2.Call
import retrofit2.http.GET

interface PostService {
    @GET("posts")

    fun getPost() : Call<List<Post>>
}