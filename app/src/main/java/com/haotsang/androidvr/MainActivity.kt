package com.haotsang.androidvr

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener
import com.google.vr.sdk.widgets.pano.VrPanoramaView
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var loadImageSuccessful //全景图是不是加载成功
            = false

    private lateinit var vrPanoramaView: VrPanoramaView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setFullscreenCompat(this, fullscreen = true)

        vrPanoramaView = findViewById<VrPanoramaView>(R.id.vrPanoramaView)
        vrPanoramaView.apply {
            setEventListener(ActivityEventListener()) //设置监听
            setInfoButtonEnabled(false) //设置隐藏最左边信息的按钮
            setStereoModeButtonEnabled(false) //设置隐藏立体模型的按钮
            setPureTouchTracking(true) //禁用陀螺仪控制
            try {
                loadImageFromBitmap(
                    BitmapFactory.decodeStream(assets.open("vr.jpg")),
                    VrPanoramaView.Options().apply {
                        /*  Options是VrPanoramaView所需的设置
                            设置TYPE_MONO，图像被预期以覆盖沿着其水平轴360度，使图片可以360水平旋转。
                            若使用VR设备，可以设置TYPE_STEREO_OVER_UNDER，将图片分割成重合度很高两部分，分别对应左眼与右眼。
                        */
                        inputType = VrPanoramaView.Options.TYPE_MONO
                    }
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }


        val switchBtn = findViewById<SwitchCompat>(R.id.switchButton)
        switchBtn.setOnCheckedChangeListener { _, isChecked ->
            vrPanoramaView.setPureTouchTracking(!isChecked) //陀螺仪控制
            Toast.makeText(this, if (isChecked) "已开启陀螺仪" else "已关闭陀螺仪", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onResume() {
        super.onResume()
        vrPanoramaView.resumeRendering()
    }

    override fun onPause() {
        super.onPause()
        vrPanoramaView.pauseRendering()
    }

    override fun onDestroy() {
        super.onDestroy()
        vrPanoramaView.shutdown()
    }

    inner class ActivityEventListener : VrPanoramaEventListener() {
        override fun onLoadSuccess() {
            loadImageSuccessful = true
        }

        override fun onLoadError(errorMessage: String) {
            loadImageSuccessful = false
            Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
        }

        override fun onClick() {
            super.onClick()
            Toast.makeText(this@MainActivity, "Click", Toast.LENGTH_SHORT).show()
        }

        /**
         * 切换显示模式时触发
         */
        override fun onDisplayModeChanged(newDisplayMode: Int) {
            super.onDisplayModeChanged(newDisplayMode)
        }
    }

    // 全屏显示
    private fun setFullscreenCompat(activity: Activity, fullscreen: Boolean) {
        val window = activity.window
        val decorView = window.decorView
        if (fullscreen) {
            WindowInsetsControllerCompat(window, decorView).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            WindowInsetsControllerCompat(window, decorView).show(WindowInsetsCompat.Type.systemBars())
        }
    }
}