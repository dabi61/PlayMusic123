package com.example.playmusic

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playmusic.Constance.PUT_EXTRA_NOTIFICATION
import com.example.playmusic.Constance.PUT_EXTRA_SONG
import com.example.playmusic.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
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
        val intent = Intent(this, MyService::class.java)
        stopService(intent)
    }

    private fun clickStartService() {
        if(checkPermission()) {
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
}