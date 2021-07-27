package com.rasel.flickergallery.data.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.rasel.flickergallery.data.api.FlickerApiService
import com.rasel.flickergallery.data.models.ImageListResponse
import com.rasel.flickergallery.data.models.Item
import com.rasel.flickergallery.data.models.Media
import com.rasel.flickergallery.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalCoroutinesApi
class FlickerImageRepositoryTest {

    @Mock
    lateinit var flickerApiService: FlickerApiService
    private val dispatcher = TestCoroutineDispatcher()
    private lateinit var repository: FlickerImageRepository

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        repository = FlickerImageRepository(flickerApiService, dispatcher)
    }

    @Test
    fun when_image_acquisition_api_call_is_successful() = runBlockingTest {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = formatter.parse("2021-07-27T07:33:54Z") ?: Date()
        val media = Media("https://live.staticflickr.com/65535/51337480387_18c9215b7e_m.jpg")
        val item = Item("nobody@flickr.com (hitchki.nameplates)", "188868082@N07", date, "description", "https://www.flickr.com/photos/188868082@N07/51337480387/", media, date, "", "Test")
        val response = ImageListResponse(listOf(item), "2021-07-27T07:33:54Z", "Uploads from everyone")

        val responseString = """
            jsonFlickrFeed({
            		"title": "Uploads from everyone",
            		"link": "https://www.flickr.com/photos/",
            		"description": "",
            		"modified": "2021-07-27T07:33:54Z",
            		"generator": "https:\/\/www.flickr.com",
            		"items": [
            	   {
            			"title": "Test",
            			"link": "https://www.flickr.com/photos/188868082@N07/51337480387/",
            			"media": {"m":"https://live.staticflickr.com/65535/51337480387_18c9215b7e_m.jpg"},
            			"date_taken": "2021-07-27T07:33:54Z",
            			"description": "description",
            			"published": "2021-07-27T07:33:54Z",
            			"author": "nobody@flickr.com (hitchki.nameplates)",
            			"author_id": "188868082@N07",
            			"tags": ""
            	   }
                    ]
            })
        """.trimIndent()

        Mockito.`when`(
            flickerApiService.getImages(
                Mockito.anyString(),
                Mockito.anyString()
            )
        )
            .thenReturn(Response.success(responseString))

        repository.getImages("test")

        //Verify response
        assertEquals(response, repository.imageListLiveData.getOrAwaitValue())
        assertEquals(null, repository.error.getOrAwaitValue())
        //Verify number of API calls
        Mockito.verify(flickerApiService, Mockito.times(1)).getImages(
            Mockito.anyString(),
            Mockito.anyString()
        )
        Mockito.verifyNoMoreInteractions(flickerApiService)
    }

    @Test
    fun when_image_acquisition_api_call_is_unsuccessful() = runBlockingTest {
        val errorBody = ByteArray(0).toResponseBody(null)
        Mockito.`when`(
            flickerApiService.getImages(
                Mockito.anyString(),
                Mockito.anyString()
            )
        )
            .thenReturn(Response.error(500, errorBody))

        repository.getImages("test")

        //Verify response
        assertEquals(null, repository.imageListLiveData.getOrAwaitValue())
        assertEquals(errorBody.toString(), repository.error.getOrAwaitValue())
        //Verify number of API calls
        Mockito.verify(flickerApiService, Mockito.times(1)).getImages(
            Mockito.anyString(),
            Mockito.anyString()
        )
        Mockito.verifyNoMoreInteractions(flickerApiService)
    }

    @Test
    fun when_image_acquisition_api_gives_exception() = runBlockingTest {
        val errorMessage = "Invalid request"
        Mockito.`when`(
            flickerApiService.getImages(
                Mockito.anyString(),
                Mockito.anyString()
            )
        )
            .thenThrow(RuntimeException(errorMessage))

        repository.getImages("test")
        assertEquals(null, repository.imageListLiveData.getOrAwaitValue())
        assertEquals(errorMessage, repository.error.getOrAwaitValue())

        //Verify number of API calls
        Mockito.verify(flickerApiService, Mockito.times(1)).getImages(
            Mockito.anyString(),
            Mockito.anyString()
        )
        Mockito.verifyNoMoreInteractions(flickerApiService)
    }
}