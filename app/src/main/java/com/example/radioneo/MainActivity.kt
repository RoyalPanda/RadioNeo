package com.example.radioneo

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import com.example.radioneo.SingletonRequestQueue


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun toggleRadio(view: View) {
        val url = "http://stream.radioneo.org:8000/;stream/1" // your URL here
        val mediaPlayer: MediaPlayer? = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(url)
            val nowPlayingText = findViewById<TextView>(R.id.nowPlaying)
            nowPlayingText.text = "Chargement en cours"
        }
        val mainHandler = Handler(Looper.getMainLooper())
        try {
            mediaPlayer!!.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                mediaPlayer.start()
                mainHandler.post( object: Runnable {
                    override fun run() {
                        displayWhoIsPlaying()
                        mainHandler.postDelayed(this, 30000)
                    }
                })
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun displayWhoIsPlaying() {

        val url = "http://www.radioneo.org/liveJSON.json"
        val nowPlayingText = findViewById<TextView>(R.id.nowPlaying)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val  artisteChanson = response.getString("artisteNom")
                val titreChanson = response.getString("titreNom")
                nowPlayingText.text = "Chanson : %s - Artiste : %s".format(titreChanson, artisteChanson)
            },
            { error ->
                // TODO: Handle error
            }
        )
        SingletonRequestQueue.getInstance(this).addToRequestQueue(jsonObjectRequest)

    }
}
