package com.nsv.coinwisewatch

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.nsv.coinwisewatch.databinding.ActivityMainBinding
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private val toolbar: Toolbar? = null
    private lateinit var binding : ActivityMainBinding
    private val firebase = FirebaseDatabase.getInstance()
    private val users: DatabaseReference = firebase.getReference("users")

    private var prog: ProgressDialog? = null





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)

        val navHostFragment =supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        binding.bottomNavBar.setupWithNavController(navHostFragment.navController)

        initializing()


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
                binding.drawer.closeDrawer(GravityCompat.START)
            }else if(!isCurrentDestinationSame("fragment_home")){
                findNavController(R.id.nav_host_fragment).popBackStack()
            }
            else {
                showDialogTrue(
                    "Leave!",
                    "Are you sure want to leave this app?",
                    "No",
                    "Yes")
            }
        }})

    }
    @SuppressLint("SimpleDateFormat", "RestrictedApi")
    private fun initializing(){
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


      /*  binding.navdrawerBtn.setOnClickListener {
            binding.drawer.openDrawer(GravityCompat.START)
        }

        navview.findViewById<View>(R.id.exit).setOnClickListener {
            showDialogTrue(
                "Leave!",
                "Are you sure want to leave this app?",
                "No",
                "Yes"
            )
        }

        navview.findViewById<View>(R.id.report).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:kunalsabhadiya85@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "Report or Suggestion | CoinWise Watch")
            }
            startActivity(intent)
        }

        navview.findViewById<View>(R.id.rateus).setOnClickListener {
            val uri = Uri.parse("market://details?id=$packageName")
            val goToPlaystore = Intent(Intent.ACTION_VIEW, uri)

            goToPlaystore.addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            )
            try {
                startActivity(goToPlaystore)
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            }
        }

        navview.findViewById<View>(R.id.share).setOnClickListener {
            val sendIntent = Intent()
            sendIntent.setAction(Intent.ACTION_SEND)
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Hey! You want to make money from online? KingEarn is best platform. I am using this. You can join with us. Click here: https://play.google.com/"
            )
            sendIntent.setType("text/plain")

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }


       */

    }


    private fun  showDialogTrue(_title: String?, _description: String?, _positive: String?, _negative: String?) {
        val dialog = AlertDialog.Builder(this@MainActivity).create()
        val inflate: View = layoutInflater.inflate(R.layout.exit_dialog, null)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setView(inflate)
        val main = inflate.findViewById<View>(R.id.linear1) as LinearLayout
        val title = inflate.findViewById<View>(R.id.textview2) as TextView
        val description = inflate.findViewById<View>(R.id.textview1) as TextView
        val positive = inflate.findViewById<View>(R.id.textview3) as TextView
        val negative = inflate.findViewById<View>(R.id.textview4) as TextView
        val img = inflate.findViewById<View>(R.id.img) as ImageView

        val gd = GradientDrawable()
        gd.setColor(ContextCompat.getColor(applicationContext,R.color.gradcolor))
        gd.cornerRadius = 10F
        main.background = gd

        title.text = _title
        description.text = _description
        positive.text = _positive
        negative.text = _negative
        positive.setOnClickListener { dialog.dismiss() }
        negative.setOnClickListener {
            dialog.dismiss()
            finishAffinity()
        }
        img.setImageResource(R.drawable.exit)
        dialog.setCancelable(true)
        dialog.show()
    }

    private fun isCurrentDestinationSame(tag :String):Boolean{
        return findNavController(R.id.nav_host_fragment).currentDestination?.label == tag
    }


}