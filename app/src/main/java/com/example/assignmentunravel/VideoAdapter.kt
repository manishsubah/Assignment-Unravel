package com.example.assignmentunravel

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.assignmentunravel.databinding.ListVideoBinding
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.LoadControl
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource

class VideoAdapter(var context: Context, var videos: ArrayList<Video>, var videoPreparedListener: OnVideoPreparedListener) :RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {
    class VideoViewHolder(
        val binding: ListVideoBinding,
        var context: Context,
        var videoPreparedListener: OnVideoPreparedListener
    ) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var exoPlayer: ExoPlayer
        private lateinit var mediaSource: MediaSource

        fun setVideoPath(url: String) {
            exoPlayer = ExoPlayer.Builder(context).build()
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlayerErrorChanged(error: PlaybackException?) {
                    super.onPlayerErrorChanged(error)
                    Toast.makeText(context,"Can't Play this Video.", Toast.LENGTH_LONG).show()
                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    if(playbackState == Player.STATE_BUFFERING) {
                        binding.pbLoading.visibility = View.VISIBLE
                    } else if(playbackState == Player.STATE_READY) {
                        binding.pbLoading.visibility = View.GONE
                    }
                }
            })

            binding.playerView.player = exoPlayer

            exoPlayer.seekTo(0)
            exoPlayer.repeatMode = Player.REPEAT_MODE_ONE

            val dataSourceFactory = DefaultDataSource.Factory(context)

            mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
                MediaItem.fromUri(Uri.parse(url)))

            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()
            if(absoluteAdapterPosition == 0) {
                exoPlayer.playWhenReady = true
                exoPlayer.play()
            }

            videoPreparedListener.onVideoPrepared(ExoPlayerItem(exoPlayer, absoluteAdapterPosition))
            //Play and Pause the video
            binding.transperant.setOnClickListener{

                    if (exoPlayer.isPlaying) {
                        exoPlayer.playWhenReady = false
                        exoPlayer.playbackState
                        exoPlayer.pause()
                    }
                    else {
                        exoPlayer.playWhenReady = true
                        exoPlayer.playbackState
                        exoPlayer.play()
                    }
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = ListVideoBinding.inflate(LayoutInflater.from(context), parent, false)
        return VideoViewHolder(view, context, videoPreparedListener)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val model = videos[position]

        holder.setVideoPath(model.url)
    }

    override fun getItemCount(): Int {
        return videos.size
    }
    interface OnVideoPreparedListener {
        fun onVideoPrepared(exoPlayerItem: ExoPlayerItem)

    }

}