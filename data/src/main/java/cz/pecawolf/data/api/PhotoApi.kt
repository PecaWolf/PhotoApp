package cz.pecawolf.data.api

import cz.pecawolf.data.model.PhotoFeedDto
import retrofit2.http.GET

interface PhotoApi {

    @GET("photos_public.gne?format=json&nojsoncallback=1")
    suspend fun getPhotos(): PhotoFeedDto
}
