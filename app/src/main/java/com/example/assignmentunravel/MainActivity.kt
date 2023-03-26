package com.example.assignmentunravel

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import androidx.viewpager2.widget.ViewPager2
import com.example.assignmentunravel.databinding.ActivityMainBinding
import com.google.android.exoplayer2.ExoPlayer
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: VideoAdapter
    private val videos = ArrayList<Video>()
    private val exoPlayerItems = ArrayList<ExoPlayerItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val jsonData = applicationContext.resources.openRawResource(
            applicationContext.resources.getIdentifier(
                "vid",
                "raw", applicationContext.packageName
            )
        ).bufferedReader().use{it.readText()}

        val outputJsonobject = JSONObject(jsonData)

        Log.d("TAG_Data", ""+outputJsonobject)
        val link = outputJsonobject.get("recommendation") as JSONArray

        for(i in 0 until link.length()) {
            val id = link.getJSONObject(i).get("med")
            val data = "$id"
            videos.add(Video(data))
        }
        val id = link.getJSONObject(0).get("med")
        val data = "$id"
        videos.add(Video(data))
//        videos.add(
//            Video(
//                "https://d1tf573zhz3zzy.cloudfront.net/data/content/videos/CantoTranscoded/720p/DMITRY/ul2dqaprm55mnfh5n64idfbp40.mp4"
//            )
//        )
        resumeVideo()
    }
    override fun onPause() {
        super.onPause()
        val index = exoPlayerItems.indexOfFirst { it.position == binding.viewPager2.currentItem }
        if(index != -1) {
            val player = exoPlayerItems[index].exoPlayer
            player.pause()
            player.playWhenReady = false

        }
    }
    override fun onResume() {
        super.onResume()
        val index = exoPlayerItems.indexOfFirst { it.position == binding.viewPager2.currentItem }
        if(index != -1) {
            val player = exoPlayerItems[index].exoPlayer
            player.playWhenReady = true
            player.play()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(exoPlayerItems.isNotEmpty()) {
            for(item in exoPlayerItems) {
                val player = item.exoPlayer
                player.stop()
                player.clearMediaItems()

            }
        }
    }
private fun resumeVideo() {
    adapter = VideoAdapter(this, videos, object : VideoAdapter.OnVideoPreparedListener{
        override fun onVideoPrepared(exoPlayerItem: ExoPlayerItem) {
            exoPlayerItems.add(exoPlayerItem)
        }
    })
    binding.viewPager2.adapter = adapter
    binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            if((videos.size-1) <= position) {
                binding.viewPager2.setCurrentItem(0,false)
                return
            }
            val previousIndex = exoPlayerItems.indexOfFirst { it.exoPlayer.isPlaying }
            if(previousIndex != -1) {
                val player = exoPlayerItems[previousIndex].exoPlayer
                player.pause()
                player.playWhenReady = false
            }
            val newIndex = exoPlayerItems.indexOfFirst { it.position == position }
            if(newIndex != -1) {
                val player = exoPlayerItems[newIndex].exoPlayer
                player.playWhenReady = true
                player.play()

            }

        }
    })
}

}