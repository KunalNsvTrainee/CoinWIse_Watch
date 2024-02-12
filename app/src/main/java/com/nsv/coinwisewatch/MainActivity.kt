package com.nsv.coinwisewatch

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.nsv.coinwisewatch.Activities.HistoryActivity
import com.nsv.coinwisewatch.Activities.NotificationActivity
import com.nsv.coinwisewatch.Activities.WalletActivity
import com.nsv.coinwisewatch.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private val toolbar: Toolbar? = null
    private lateinit var binding : ActivityMainBinding
    private val firebase = FirebaseDatabase.getInstance()
    private val users: DatabaseReference = firebase.getReference("users")

    private var calendar = Calendar.getInstance()
    private var map = HashMap<String,Any>()
    private var now_time = 0.0
    private val balance = "0"
    private var last_Claimed = 0.0
    private var time_X = ""
    private var key = ""

    private val history: DatabaseReference = firebase.getReference("history")
    private val claimed_time: DatabaseReference = firebase.getReference("claimed_time")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
       initializing()
        FirebaseApp.initializeApp(this)
    }
    @SuppressLint("SimpleDateFormat")
    private fun initializing(){
       // setSupportActionBar(toolbar)
      //  supportActionBar!!.setDisplayHomeAsUpEnabled(true)
      //  supportActionBar!!.setHomeButtonEnabled(true)
        toolbar?.setNavigationOnClickListener(View.OnClickListener { onBackPressedDispatcher })
        val toggle = ActionBarDrawerToggle(
            this@MainActivity,
            binding.drawer,
            toolbar,
            R.string.app_name,
            R.string.app_name
        )
        binding.drawer.addDrawerListener(toggle)
        toggle.syncState()
       binding.navdrawerBtn.setOnClickListener { v ->
           binding.drawer.openDrawer(GravityCompat.START)
       }

        binding.notiBtn.setOnClickListener { v ->
            val i=Intent(applicationContext,NotificationActivity::class.java)
            startActivity(i);
        }

        binding.dailyBonus.setOnClickListener { v ->
            if (time_X == "") {
                map["balance"] = (balance.toDouble() + 10).toLong().toString()
                users.child(FirebaseAuth.getInstance().currentUser!!.uid).updateChildren(map)
                map.clear()
                key = history.push().key.toString()
                map = HashMap<String, Any>()
                map["subject"] = "daily bonus"
                map["date"] = SimpleDateFormat("dd-MM-yyyy").format(calendar.time)
                map["amount"] = "10"
                map["key"] = key
                map["uid"] = FirebaseAuth.getInstance().currentUser!!.uid
                history.child(key).updateChildren(map)
                map.clear()
                map = HashMap<String, Any>()
                map["timer"] = calendar.timeInMillis.toString()
                claimed_time.child(FirebaseAuth.getInstance().currentUser!!.uid).updateChildren(map)
                map.clear()
                congratulation("+10 points")
            } else {
                if (time_X != "") {
                    last_Claimed = time_X.toDouble()
                    now_time = calendar.timeInMillis - last_Claimed
                    if (now_time / (60 * 60000) > 24 || now_time / (60 * 60000) == 24.0) {
                        map["balance"] = (balance.toDouble() + 10).toLong().toString()
                        users.child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .updateChildren(map)
                        map.clear()
                        key = history.push().key!!
                        map = java.util.HashMap<String, Any>()
                        map["subject"] = "daily bonus"
                        map["date"] = SimpleDateFormat("dd-MM-yyyy").format(calendar.getTime())
                        map["amount"] = "10"
                        map["key"] = key
                        map["uid"] = FirebaseAuth.getInstance().currentUser!!.uid
                        history.child(key).updateChildren(map)
                        map.clear()
                        map = java.util.HashMap<String, Any>()
                        map["timer"] = calendar.timeInMillis.toString()
                        claimed_time.child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .updateChildren(map)
                        map.clear()
                        congratulation("+10 points")
                    } else {
                        Toast.makeText(applicationContext, "Wait 24 hours", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        binding.hist.setOnClickListener {
         val i=Intent(applicationContext,HistoryActivity::class.java)
            startActivity(i);
        }

        binding.others.setOnClickListener(View.OnClickListener {
            val i=Intent(applicationContext,WalletActivity::class.java)
            startActivity(i);
        })

        claimed_time.addChildEventListener( object : ChildEventListener {
            override fun onChildAdded( param1: DataSnapshot, param2: String?) {
                val ind: GenericTypeIndicator<java.util.HashMap<String?, Any?>?> =
                    object : GenericTypeIndicator<java.util.HashMap<String?, Any?>?>() {}
                val childKey = param1.key
                val childValue = param1.getValue(ind)!!
                if (childKey == FirebaseAuth.getInstance().currentUser!!.uid) {
                    if (childValue.containsKey("timer")) {
                        time_X = childValue["timer"].toString()
                        last_Claimed = childValue["timer"].toString().toDouble()
                        now_time = calendar.timeInMillis - last_Claimed
                        if (now_time / (60 * 60000) > 24 || now_time / (60 * 60000) == 24.0) {
                            binding.textview6.text = "Claimed Bonus"
                            binding.textview6.setTextColor(-0x1000000)
                        } else {
                            binding.textview6.text = "Wait 24 hours"
                            binding.textview6.setTextColor(-0x616162)
                        }
                    }
                }
            }

            override fun onChildChanged(param1: DataSnapshot, param2: String?) {
                val ind: GenericTypeIndicator<java.util.HashMap<String?, Any?>?> =
                    object : GenericTypeIndicator<java.util.HashMap<String?, Any?>?>() {}
                val childKey = param1.key
                val childValue = param1.getValue ( ind)!!
                if  ( childKey == FirebaseAuth.getInstance().currentUser!!.uid) {
                    if  ( childValue.containsKey("timer")) {
                        time_X = childValue["timer"].toString()
                        last_Claimed = childValue["timer"].toString().toDouble()
                        now_time = calendar.timeInMillis - last_Claimed
                        if (now_time / (60 * 60000) > 24 || now_time / (60 * 60000) == 24.0) {
                            binding.textview6.setText("Claimed Bonus")
                            binding.textview6.setTextColor(-0x1000000)
                        } else {
                            binding.textview6.setText("Wait 24 hours")
                            binding.textview6.setTextColor(-0x616162)
                        }
                    }
                }
            }

            override fun onChildMoved ( param1: DataSnapshot, param2: String?) {}
            override fun onChildRemoved ( param1: DataSnapshot) {
            }

            override fun onCancelled ( param1: DatabaseError) {
            }
        })

       


    }

    private fun congratulation(point: String) {
        val dialog1 = AlertDialog.Builder(this@MainActivity).create()
        val inflate: View = layoutInflater.inflate(R.layout.congolayout, null)
        dialog1.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog1.setView(inflate)
        val t1 = inflate.findViewById<View>(R.id.t1) as TextView
        val t2 = inflate.findViewById<View>(R.id.t2) as TextView
        val t3 = inflate.findViewById<View>(R.id.t3) as TextView
        val b1 = inflate.findViewById<View>(R.id.b1) as LinearLayout
        val bg = inflate.findViewById<View>(R.id.bg) as LinearLayout
        val bg2 = inflate.findViewById<View>(R.id.bg2) as LinearLayout
        t3.text = point
        rippleRoundStroke(bg, "#FC716A", 10.0, 0.0, "#000000")
        setRadius("#FAFAFA", "#EEEEEE", 3.0, 0.0, 0.0, 10.0, 10.0, b1)
        setRadius("#F06159", "#F06159", 0.0, 10.0, 10.0, 0.0, 0.0, bg2)
        dialog1.setCancelable(true)
        dialog1.show()
    }

    private fun rippleRoundStroke ( view: View, focus: String?, round: Double, stroke: Double, strokeclr: String) {
        val GG = GradientDrawable()
        GG.setColor(Color.parseColor ( focus))
        GG.cornerRadius = round.toFloat()
        GG.setStroke(
            stroke.toInt(),
            Color.parseColor("#" + strokeclr.replace("#", ""))
        )
        val RE = RippleDrawable(
            ColorStateList(
                arrayOf(intArrayOf()),
                intArrayOf(Color.parseColor("#FF757575"))
            ), GG, null
        )
        view.background = RE
        view.elevation = 5f
    }


    private fun setRadius ( color1: String?, color2: String?, str: Double, n1: Double, n2: Double, n3: Double, n4: Double, view: View) {
        val gd = GradientDrawable()
        gd.setColor(Color.parseColor ( color1))
        gd.setStroke ( str.toInt(), Color.parseColor ( color2))
        gd.cornerRadii = floatArrayOf(
            n1.toInt().toFloat(),
            n1.toInt().toFloat(),
            n2.toInt()
                .toFloat(),
            n2.toInt().toFloat(),
            n3.toInt().toFloat(),
            n3.toInt().toFloat(),
            n4.toInt()
                .toFloat(),
            n4.toInt().toFloat()
        )
        view.background = gd
        val RE = RippleDrawable(
            ColorStateList(
                arrayOf(intArrayOf()),
                intArrayOf(Color.parseColor("#FFFFFF"))
            ), gd, null
        )
        view.background = RE
    }

}