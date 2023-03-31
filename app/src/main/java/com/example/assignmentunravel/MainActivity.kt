package com.example.assignmentunravel

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import androidx.viewpager2.widget.ViewPager2
import com.example.assignmentunravel.databinding.ActivityMainBinding
import com.google.android.exoplayer2.ExoPlayer
import org.json.JSONArray
import org.json.JSONObject
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: VideoAdapter
    private val videos = ArrayList<Video>()
    private val exoPlayerItems = ArrayList<ExoPlayerItem>()
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        callAgain()
        resumeVideo()

    }
    private fun callAgain() {
//        val jsonData = applicationContext.resources.openRawResource(
//            applicationContext.resources.getIdentifier(
//                "vid",
//                "raw", applicationContext.packageName
//            )
//        ).bufferedReader().use{it.readText()}
//
//        val outputJsonobject = JSONObject(jsonData)
//
//        Log.d("TAG_Data", ""+outputJsonobject)
//        val link = outputJsonobject.get("recommendation") as JSONArray
//        for(i in 0 until link.length()) {
//            val id = link.getJSONObject(i).get("med")
//            val data = "$id"
//            videos.add(Video(data))
        videos.add(Video("android.resource://"+ packageName +"/"+R.raw.v1))
        videos.add(Video("android.resource://"+ packageName +"/"+R.raw.v2))
        videos.add(Video("android.resource://"+ packageName +"/"+R.raw.v3))
        videos.add(Video("android.resource://"+ packageName +"/"+R.raw.v4))
        videos.add(Video("android.resource://"+ packageName +"/"+R.raw.v5))
        videos.add(Video("android.resource://"+ packageName +"/"+R.raw.v4))
        videos.add(Video("android.resource://"+ packageName +"/"+R.raw.v1))

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

    override fun onStart() {
        super.onStart()
        val index = exoPlayerItems.indexOfFirst { it.position == binding.viewPager2.currentItem }
        if(index != -1) {
            val player = exoPlayerItems[index].exoPlayer

            player.playWhenReady = true
            player.play()
        }
    }

    override fun onStop() {
        super.onStop()
        val index = exoPlayerItems.indexOfFirst { it.position == binding.viewPager2.currentItem }
        if(index != -1) {
            val player = exoPlayerItems[index].exoPlayer
            player.pause()
            player.playWhenReady = false
            player.release()  // IMPORTANT TO RELEASE RESOURCES
        }
    }
    override fun onResume() {
        super.onResume()
        resumeVideo()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(exoPlayerItems.isNotEmpty()) {
            for(item in exoPlayerItems) {
                val player = item.exoPlayer
                player.stop()
                player.clearMediaItems()
                player.release()
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
    binding.viewPager2.adapter = adapter

    binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            val previousIndex = exoPlayerItems.indexOfFirst { it.exoPlayer.isPlaying }
            if(previousIndex != -1) {
                val player = exoPlayerItems[previousIndex].exoPlayer
                player.pause()
                player.playWhenReady = false
            }

        }

        override fun onPageSelected(position: Int) {
            val newIndex = exoPlayerItems.indexOfFirst { it.position == position }
            if(newIndex != -1) {
                val player = exoPlayerItems[newIndex].exoPlayer
                player.playWhenReady = true
                player.play()

                Toast.makeText(applicationContext, "$position", Toast.LENGTH_SHORT).show()
            }
            if(position >= 6) {
                val player = exoPlayerItems[0].exoPlayer
                player.prepare()
                player.playWhenReady = true
                player.play()
                binding.viewPager2.setCurrentItem(0,false)
                return
            }
        }

        override fun onPageScrollStateChanged(position: Int) {
            super.onPageScrollStateChanged(position)
            val previousIndex = exoPlayerItems.indexOfFirst { it.exoPlayer.isPlaying }
            if(previousIndex != -1) {
                val player = exoPlayerItems[previousIndex].exoPlayer
                player.pause()
                player.playWhenReady = false
            }

        }
    })
}
    fun ViewPager2.reduceDragSensitivity() {
        val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        recyclerViewField.isAccessible = true
        val recyclerView = recyclerViewField.get(this) as RecyclerView
        val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
        touchSlopField.isAccessible = true
        val touchSlop = touchSlopField.get(recyclerView) as Int
        touchSlopField.set(recyclerView, touchSlop*8)
    }


}