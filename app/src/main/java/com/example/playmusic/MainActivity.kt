package com.example.playmusic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.playmusic.Constance.PUT_EXTRA_SONG
import com.example.playmusic.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isPlaying : Boolean = false
    private var mSong : Song? = null
    private var  broadcastReceiverMusic : BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val bundle = intent.extras
            if(bundle == null) {
                return
            } else {
                mSong = bundle.getParcelable(Constance.PUT_PARCELABLE_SONG)
                isPlaying = bundle.getBoolean(Constance.PUT_PARCElABLE_ACTIVE)
                var action = bundle.getInt(Constance.PUT_PARCELABLE_STATE)


                handleLayoutMusic(action)
            }

        }

    }

    private fun handleLayoutMusic(action : Int) {
        when(action) {
            Constance.ACTION_START -> {
                with(binding) {
                    clLayoutBottom.visibility = View.VISIBLE
                }
                showInfoSong()
                setStatusButtonPlayOrPause()
            }
            Constance.ACTION_PAUSE -> {
                setStatusButtonPlayOrPause()
            }
            Constance.ACTION_RESUME -> {
                setStatusButtonPlayOrPause()
            }
            Constance.ACTION_CLEAR -> {
                with(binding) {
                    clLayoutBottom.visibility = View.GONE
                }
                isPlaying = false
            }
        }
    }

    private fun showInfoSong() {
        if(mSong == null) {
            return
        } else {
            with(binding) {
                ivSong.setImageResource(mSong?.image ?: R.drawable.toteti)
                tvTitleSong.text = mSong?.title
                tvSingleSong.text = mSong?.single

                ivPlayOrPause.setOnClickListener() {
                    if(isPlaying) {
                        sendActionToService(Constance.ACTION_PAUSE)
                    } else {
                        sendActionToService(Constance.ACTION_RESUME)
                    }
                }

                ivClose.setOnClickListener() {
                    sendActionToService(Constance.ACTION_CLEAR)
                }
            }
        }
    }
    private fun sendActionToService(action : Int) {
        val intent = Intent(this, MyService::class.java)
        intent.putExtra(Constance.PUT_EXTRA_ACTION_MUSIC_SERVICE, action)
        startService(intent)
    }
    private fun setStatusButtonPlayOrPause() {
        if(isPlaying) {
            binding.ivPlayOrPause.setImageResource(R.drawable.ic_pause)
        } else {
            binding.ivPlayOrPause.setImageResource(R.drawable.ic_start)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverMusic, IntentFilter(Constance.SEND_DATA_TO_ACTIVITY))
        with(binding) {
            btStartService.setOnClickListener {
                clickStartService()
            }
            btStopService.setOnClickListener {
                clickStopService()
        }
    }
}

    private fun clickStopService() {
        isPlaying = false
        val intent = Intent(this, MyService::class.java)
        stopService(intent)
    }

    private fun clickStartService() {
        if(checkPermission()) {
            isPlaying = true
            var song = Song("Tò te tí", "Wren Evans", R.drawable.toteti, R.raw.toteti)
            val intent = Intent(this, MyService::class.java)
            var bundle = Bundle()
            bundle.putParcelable(PUT_EXTRA_SONG, song)
            intent.putExtras(bundle)
            startService(intent)
        }
    }
    private fun checkPermission() : Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
            else {
                return true
            }
        }
        else
        {
            return true
        }
        return false
    }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Quyền thông báo đã được cấp!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền để nhận thông báo!", Toast.LENGTH_LONG).show()
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverMusic)
    }
}