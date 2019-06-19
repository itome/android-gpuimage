package jp.co.cyberagent.android.gpuimage.sample.activity

import android.graphics.BitmapFactory
import android.net.Uri
import android.opengl.Matrix
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import jp.co.cyberagent.android.gpuimage.GPUImageView
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup
import jp.co.cyberagent.android.gpuimage.filter.GPUImagePixelationFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageTransformFilter
import jp.co.cyberagent.android.gpuimage.sample.R

class PlayerActivity : AppCompatActivity() {

    private val gpuImageView: GPUImageView by lazy { findViewById<GPUImageView>(R.id.gpu_image_view) }
    private val playerView: PlayerView by lazy { findViewById<PlayerView>(R.id.gpu_image_player_view) }
    private val pixelationSeekBar: SeekBar by lazy { findViewById<SeekBar>(R.id.seek_bar_pixelation) }
    private val sepiaSeekBar: SeekBar by lazy { findViewById<SeekBar>(R.id.seek_bar_sepia) }
    private val transformSeekBar: SeekBar by lazy { findViewById<SeekBar>(R.id.seek_bar_transform) }

    private val pixelationFilter = GPUImagePixelationFilter()
    private val sepiaToneFilter = GPUImageSepiaToneFilter()
    private val transformFilter = GPUImageTransformFilter()
    private val filterGroup =
        GPUImageFilterGroup(listOf(pixelationFilter, sepiaToneFilter, transformFilter))

    private lateinit var player: SimpleExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        gpuImageView.setImage(BitmapFactory.decodeResource(resources, R.drawable.dog))
        gpuImageView.filter = filterGroup

        pixelationSeekBar.setOnChangeListener { progress ->
            pixelationFilter.setPixel(range(progress, 1f, 100f))
            gpuImageView.requestRender()
        }

        sepiaSeekBar.setOnChangeListener { progress ->
            sepiaToneFilter.setIntensity(range(progress, 0.0f, 2.0f))
            gpuImageView.requestRender()
        }

        transformSeekBar.setOnChangeListener { progress ->
            val transform = FloatArray(16)
            Matrix.setRotateM(transform, 0, (360 * progress / 100).toFloat(), 0f, 0f, 1.0f)
            transformFilter.transform3D = transform
            gpuImageView.requestRender()
        }

        player = ExoPlayerFactory.newSimpleInstance(this)
        val dataSourceFactory =
            DefaultDataSourceFactory(this, Util.getUserAgent(this, "gpuimagesample"))
        val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse("file:///android_asset/big_buck_bunny.mp4"))
        player.prepare(videoSource)
        player.playWhenReady = true
        playerView.player = player
    }

    private fun range(percentage: Int, start: Float, end: Float): Float {
        return (end - start) * percentage / 100f + start
    }

    private fun SeekBar.setOnChangeListener(onProgressChanged: (progress: Int) -> Unit) {
        this.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                onProgressChanged(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }
}

