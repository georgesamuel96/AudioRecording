package com.example.georgesamuel.audiorecording

import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var mediaRecorder = MediaRecorder()
    private var mediaPlayer = MediaPlayer()
    private lateinit var buttonStart: Button
    private lateinit var buttonStop: Button
    private lateinit var buttonPlayLastRecordAudio: Button
    private lateinit var buttonStopPlayingRecording: Button
    var audioSavePathInDevice:String? = null
    private val RequestPermissionCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
        buttonStart.setOnClickListener {
            if(checkPermission()){
                /*
                    path of the audio
                 */
                audioSavePathInDevice = Environment.getExternalStorageDirectory().path + '/' + createAudioFileName() +
                        "AudioRecording.mp3"
                mediaRecorderReady()
                try {
                    mediaRecorder.prepare()
                    mediaRecorder.start()
                } catch (e: IllegalStateException){
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                buttonStart.isEnabled = false
                buttonStop.isEnabled = true
                Toast.makeText(this@MainActivity, "Recording started",
                    Toast.LENGTH_LONG).show()
            } else {
                requestPermission()
            }
        }
        buttonStop.setOnClickListener{
            mediaRecorder.stop()
            buttonStop.isEnabled = false
            buttonPlayLastRecordAudio.isEnabled = true
            buttonStart.isEnabled = true
            buttonStopPlayingRecording.isEnabled = false
            Toast.makeText(
                this@MainActivity, "Recording Completed",
                Toast.LENGTH_LONG
            ).show()
        }

        buttonPlayLastRecordAudio.setOnClickListener(object : View.OnClickListener {
            @Throws(
                IllegalArgumentException::class,
                SecurityException::class,
                IllegalStateException::class
            )
            override fun onClick(view: View) {
                buttonStop.isEnabled = false
                buttonStart.isEnabled = false
                buttonStopPlayingRecording.isEnabled = true
                try {
                    mediaPlayer.setDataSource(audioSavePathInDevice)
                    mediaPlayer.prepare()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                mediaPlayer.start()
                Toast.makeText(
                    this@MainActivity, "Recording Playing",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        buttonStopPlayingRecording.setOnClickListener {
            buttonStop.isEnabled = false
            buttonStart.isEnabled = true
            buttonStopPlayingRecording.isEnabled = false
            buttonPlayLastRecordAudio.isEnabled = true
            mediaPlayer.stop()
            mediaPlayer.release()
            mediaRecorderReady()
        }
    }

    private fun init() {
        buttonStart = findViewById(R.id.button)
        buttonStop = findViewById(R.id.button2)
        buttonPlayLastRecordAudio = findViewById(R.id.button3)
        buttonStopPlayingRecording = findViewById(R.id.button4)
        buttonStop.isEnabled = false
        buttonPlayLastRecordAudio.isEnabled = false
        buttonStopPlayingRecording.isEnabled = false
    }

    private fun mediaRecorderReady() {
        mediaRecorder = MediaRecorder()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        mediaRecorder.setOutputFile(audioSavePathInDevice)
    }

    private fun createAudioFileName(): String {
        return "${System.currentTimeMillis()}"
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(WRITE_EXTERNAL_STORAGE, RECORD_AUDIO),
            RequestPermissionCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        when(requestCode){
            RequestPermissionCode -> {
                if (grantResults.isNotEmpty()) {
                    val storagePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val recordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if(storagePermission && recordPermission) {
                        Toast.makeText(this@MainActivity, "Permission Granted", Toast.LENGTH_LONG).show()
                    }
                    else {
                        Toast.makeText(this@MainActivity, "Permission Denied", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            applicationContext,
            WRITE_EXTERNAL_STORAGE
        )
        val result1 = ContextCompat.checkSelfPermission(
            applicationContext,
            RECORD_AUDIO
        )
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }
}

