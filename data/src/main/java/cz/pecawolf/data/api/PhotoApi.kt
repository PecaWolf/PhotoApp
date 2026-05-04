package cz.pecawolf.data.api

import cz.pecawolf.data.model.PhotoFeedDto
import retrofit2.http.GET
import retrofit2.http.Query

interface PhotoApi {

    @GET("photos_public.gne?format=json&nojsoncallback=1")
    suspend fun getPhotos(
        @Query("tags") tags: String?,
        @Query("tagmode") tagMode: String?,
    ): PhotoFeedDto
}
