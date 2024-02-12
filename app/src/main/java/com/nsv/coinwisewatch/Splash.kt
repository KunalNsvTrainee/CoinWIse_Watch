package com.nsv.coinwisewatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nsv.coinwisewatch.databinding.ActivitySplashBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Splash : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var intent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.animationView.playAnimation()
        CoroutineScope(Dispatchers.Main).launch {
            delay(2500)

            intent = if(FirebaseAuth.getInstance().currentUser!=null){
                Intent(this@Splash, MainActivity::class.java)
            }else{
                Intent(this@Splash, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }
}