package com.daon.timer_part2_06

import android.annotation.SuppressLint
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.SoundEffectConstants
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val remainMinutesTextView: TextView by lazy {
        findViewById(R.id.remainMinutesTextView)
    }
    private val remainSecondsTextView: TextView by lazy {
        findViewById(R.id.remainSecondsTextView)
    }
    private val seekBar: SeekBar by lazy {
        findViewById(R.id.seekBar)
    }

    private val soundPool = SoundPool.Builder().build()

    private var currentCountDownTimer: CountDownTimer? = null

    private var tickingSoundId: Int? = null

    private var bellSoundId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        initSounds()
    }

    override fun onResume() {
        super.onResume()
        soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()
        soundPool.autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    private fun bindViews() {
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        updateSeekBar(progress * 60 * 1000L)
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    currentCountDownTimer?.cancel()
                    currentCountDownTimer = null
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    seekBar ?: return
                    startCountDown()
                }
            }
        )
    }

    private fun initSounds() {
       tickingSoundId = soundPool.load(this, R.raw.timer_ticking, 1)
       bellSoundId = soundPool.load(this, R.raw.timer_bell, 1)
    }

    private fun createCountDownTimer(initialMills: Long) =
        object: CountDownTimer(initialMills, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                updateRemainTime(millisUntilFinished)
                updateSeekBar(millisUntilFinished)
            }

            override fun onFinish() {
                    completeCountDown()
                }
            }

        private fun startCountDown() {
            currentCountDownTimer = createCountDownTimer(seekBar.progress * 60 * 1000L)
            currentCountDownTimer?.start()

            tickingSoundId?.let { soundId ->
                soundPool.play(soundId, 1F, 1F, 0, -1, 1F)
            }

        }

            private fun completeCountDown() {
                updateRemainTime(0)
                updateSeekBar(0)

                soundPool.autoPause()
                bellSoundId?.let { soundId ->
                    soundPool.play(bellSoundId!!, 1F, 1F, 0, 0, 1F)
            }
        }

    private fun updateRemainTime(remainMillis: Long){
        val remainSeconds = remainMillis / 1000

        remainMinutesTextView.text = "minutes".format(remainSeconds / 60)
        remainSecondsTextView.text = "%02d".format(remainSeconds % 60)
    }

    private fun updateSeekBar(remainMillis: Long) {
        seekBar.progress = (remainMillis / 1000 / 60).toInt()
    }

}