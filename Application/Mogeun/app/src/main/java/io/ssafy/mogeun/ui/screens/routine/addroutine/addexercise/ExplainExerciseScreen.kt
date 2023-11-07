package io.ssafy.mogeun.ui.screens.routine.addroutine.addexercise

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.skydoves.landscapist.glide.GlideImage
import io.ssafy.mogeun.model.VideoItem
import io.ssafy.mogeun.network.YouTubeApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Composable
fun ExplainExerciseScreen(navController: NavHostController, data: String?) {
    val imageResId: Int? = data?.let {
        it.split(".").lastOrNull()?.toIntOrNull()
    }

    val apiKey = "AIzaSyBq9zFbQH6P5KXIwIUf2xuXmPoacNeT5as"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        GlideImage(
            imageModel = imageResId,
            contentDescription = "GIF Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(350.dp)
                .width(350.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "운동 방법", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        YouTubeVideoList(apiKey = apiKey, query = "운동 이름")
    }
}

@Composable
fun YouTubeVideoList(apiKey: String, query: String) {
    val videos = remember { mutableStateOf<List<VideoItem>>(emptyList()) }

    LaunchedEffect(query) {
        val api = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/youtube/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YouTubeApiService::class.java)
        videos.value = api.searchVideos(query = query, apiKey = apiKey).items
    }

    LazyRow {
        items(videos.value) { video ->
            YouTubeVideoItem(video)
        }
    }
}

@Composable
fun YouTubeVideoItem(video: VideoItem) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .width(250.dp)
            .clickable {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/watch?v=${video.id.videoId}")
                )
                context.startActivity(intent)
            }
            .padding(8.dp)
    ) {
        AsyncImage(
            model = video.snippet.thumbnails.medium.url,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}