package edu.rvc.student.audioapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Environment
import android.view.View
import android.media.MediaPlayer
import android.Manifest
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

        private val RECORD_REQUEST_CODE = 101
        private val STORAGE_REQUEST_CODE = 102

        private var mediaRecorder: MediaRecorder? = null
        private var mediaPlayer: MediaPlayer? = null

        private var audioFilePath: String? = null
        private var isRecording = false

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            val record = findViewById<Button>(R.id.recordButton)
            val stop = findViewById<Button>(R.id.stopButton)
            val play = findViewById<Button>(R.id.playButton)

            audioSetup()

            record.setOnClickListener(View.OnClickListener {
                recordAudio()
            })

            stop.setOnClickListener(View.OnClickListener {
                stopAudio()
            })

            play.setOnClickListener(View.OnClickListener {
                playAudio()
            })

        }

        private fun hasMicrophone(): Boolean {
            val pmanager = this.packageManager
            return pmanager.hasSystemFeature(
                PackageManager.FEATURE_MICROPHONE

            ) }

        private fun audioSetup() {

            if (!hasMicrophone()) {
                stopButton.isEnabled = false
                playButton.isEnabled = false
                recordButton.isEnabled = true
            } else {
                playButton.isEnabled = false
                stopButton.isEnabled = false
            }

            audioFilePath = Environment.getExternalStorageDirectory()
                .absolutePath + "/myaudio.3gp"

            requestPermission(Manifest.permission.RECORD_AUDIO,
                RECORD_REQUEST_CODE)
        }

        private fun recordAudio() {
            isRecording = true
            stopButton.isEnabled = true
            playButton.isEnabled = false
            recordButton.isEnabled = false

            try {
                mediaRecorder = MediaRecorder()
                mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediaRecorder?.setOutputFormat(
                    MediaRecorder.OutputFormat.THREE_GPP
                )
                mediaRecorder?.setOutputFile(audioFilePath)
                mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                mediaRecorder?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mediaRecorder?.start()
        }


        private fun stopAudio() {

            stopButton.isEnabled = false
            playButton.isEnabled = true

            if (isRecording) {
                recordButton.isEnabled = false
                mediaRecorder?.stop()
                mediaRecorder?.release()
                mediaRecorder = null
                isRecording = false
            } else {
                mediaPlayer?.release()
                mediaPlayer = null
                recordButton.isEnabled = true
            }
        }

        private fun playAudio() {
            playButton.isEnabled = false
            recordButton.isEnabled = false
            stopButton.isEnabled = true

            mediaPlayer = MediaPlayer()
            mediaPlayer?.setDataSource(audioFilePath)
            mediaPlayer?.prepare()
            mediaPlayer?.start()
        }

        private fun requestPermission(permissionType: String, requestCode: Int) {
            val permission = ContextCompat.checkSelfPermission(
                this,
                permissionType
            )

            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permissionType), requestCode
                )
            }
        }

        override fun onRequestPermissionsResult(requestCode: Int,
                                                permissions: Array<String>, grantResults: IntArray) {
            when (requestCode) {
                RECORD_REQUEST_CODE -> {

                    if (grantResults.isEmpty() || grantResults[0]
                        != PackageManager.PERMISSION_GRANTED) {

                        recordButton.isEnabled = false

                        Toast.makeText(this,
                            "Record permission required",
                            Toast.LENGTH_LONG).show()
                    } else {
                        requestPermission(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            STORAGE_REQUEST_CODE)
                    }
                    return
                }
                STORAGE_REQUEST_CODE -> {

                    if (grantResults.isEmpty() || grantResults[0]
                        != PackageManager.PERMISSION_GRANTED) {
                        recordButton.isEnabled = false
                        Toast.makeText(this,
                            "External Storage permission required",
                            Toast.LENGTH_LONG).show()
                    }
                    return
                }
            }
        }


        }

