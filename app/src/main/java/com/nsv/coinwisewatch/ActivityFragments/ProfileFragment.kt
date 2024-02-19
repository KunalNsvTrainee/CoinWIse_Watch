package com.nsv.coinwisewatch.ActivityFragments

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.nsv.coinwisewatch.Activities.LoginActivity
import com.nsv.coinwisewatch.R
import com.nsv.coinwisewatch.databinding.FragmentProfileBinding
import java.text.DecimalFormat
import java.util.Timer
import java.util.TimerTask


class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var binding: FragmentProfileBinding
    private val timer = Timer()
    private val firebase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val users: DatabaseReference = firebase.getReference("users")
    private var shimtimer: TimerTask? = null


    private var fname = ""
    private var lname = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        FirebaseApp.initializeApp(requireContext())

            initializeLogic()


        /*  FirebaseAuth.getInstance().uid?.let {
              firebase.reference.child("users").child(it).child("imageUri")
                  .addValueEventListener(object : ValueEventListener {
                      override fun onDataChange(snapshot: DataSnapshot) {
                          val img = snapshot.getValue<String>(String::class.java)
                          Picasso.get().load(img).placeholder(R.drawable.adb).into(binding.circleimageview1)
                      }

                      override fun onCancelled(error: DatabaseError) {}
                  })
          }

         */


        binding.imageview2.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext())
            val bottomSheetView: View = layoutInflater.inflate(R.layout.custom_spinner, null)
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.window!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent)
            val bg1 = bottomSheetView.findViewById<View>(R.id.bg1) as LinearLayout
            val bg2 = bottomSheetView.findViewById<View>(R.id.bg2) as LinearLayout

            bg1.setOnClickListener {
                bottomSheetDialog.dismiss()
                findNavController().navigate(R.id.action_profileFragment_to_withdrawFragment)
            }
            bg2.setOnClickListener {
                bottomSheetDialog.dismiss()
                showDialogTrue("Logout!", "Are you sure want to logout this id?", "No", "Yes")
            }
            bottomSheetDialog.setCancelable(true)
            bottomSheetDialog.show()
        }

        binding.textview16.setOnClickListener {

            /* val intent = ImagePicker.with(requireActivity())
                    .bothCameraGallery()
                    .cropSquare()
                    .maxResultSize(1080,1080)
                    .createIntent()
               startForProfileImageResult.launch(intent)

             */
        }


        binding.goforpremium.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_membershipFragment)
        }


        users.addChildEventListener(object : ChildEventListener {
            @SuppressLint("SetTextI18n")
            override fun onChildAdded(_param1: DataSnapshot, _param2: String?) {
                val _ind: GenericTypeIndicator<HashMap<String?, Any?>?> =
                    object : GenericTypeIndicator<HashMap<String?, Any?>?>() {}
                val _childKey = _param1.key
                val _childValue = _param1.getValue(_ind)!!
                if (_childKey == FirebaseAuth.getInstance().currentUser!!.uid) {
                    if (_childValue.containsKey("firstname")) {
                        fname = _childValue["firstname"].toString()
                    }
                    if (_childValue.containsKey("lastname")) {
                        lname = _childValue["lastname"].toString()
                    }
                    binding.textview2.text = "$fname $lname"
                    shimtimer = object : TimerTask() {
                        override fun run() {
                            requireActivity().runOnUiThread {
                                binding.shimName.visibility = View.GONE
                                binding.textview2.visibility = View.VISIBLE
                            }
                        }
                    }
                   timer.schedule(shimtimer, 1000.toLong())
                    if (_childValue.containsKey("email")) {
                        binding.textview3.text = _childValue["email"].toString()
                        shimtimer = object : TimerTask() {
                            override fun run() {
                                requireActivity().runOnUiThread {
                                    binding.shimEmail.visibility = View.GONE
                                    binding.textview3.visibility = View.VISIBLE
                                }
                            }
                        }
                        timer.schedule(shimtimer, 1000.toLong())
                    }
                    if (_childValue.containsKey("referralcode")) {
                        binding.textview9.text = _childValue["referralcode"].toString()
                        shimtimer = object : TimerTask() {
                            override fun run() {
                                requireActivity().runOnUiThread {
                                    binding.textview9.visibility = View.VISIBLE
                                    binding.shimRc.visibility = View.GONE
                                }
                            }
                        }
                        timer.schedule(shimtimer, 1000.toLong())
                        binding.textview9.setOnClickListener {
                            val clipboardManager =
                                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clipData = ClipData.newPlainText(
                                "clipboard",
                                _childValue["referralcode"].toString()
                            )
                            clipboardManager.setPrimaryClip(clipData)
                            Toast.makeText(
                                requireContext(),
                                "Refer code copied",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    if (_childValue["Free"].toString() == "true") {
                        binding.textview11.text = "Free"
                        shimtimer = object : TimerTask() {
                            override fun run() {
                                requireActivity().runOnUiThread {
                                    binding.textview11.visibility = View.VISIBLE
                                    binding.shimM.visibility = View.GONE
                                }
                            }
                        }
                        timer.schedule(shimtimer, 1000.toLong())
                    } else {
                        if (_childValue["Silver"].toString() == "true") {
                            binding.textview11.text = "Silver"
                            shimtimer = object : TimerTask() {
                                override fun run() {
                                    requireActivity().runOnUiThread {
                                        binding.textview11.visibility = View.VISIBLE
                                        binding.shimM.visibility = View.GONE
                                    }
                                }
                            }
                            timer.schedule(shimtimer, 1000.toLong())
                        } else {
                            if (_childValue["Gold"].toString() == "true") {
                                binding.textview11.text = "Gold"
                                shimtimer = object : TimerTask() {
                                    override fun run() {
                                        requireActivity().runOnUiThread {
                                            binding.textview11.visibility = View.VISIBLE
                                            binding.shimM.visibility = View.GONE
                                        }
                                    }
                                }
                                timer.schedule(shimtimer, 1000.toLong())
                            } else {
                                if (_childValue["Diamond"].toString() == "true") {
                                    binding.textview11.text = "Diamond"
                                    shimtimer = object : TimerTask() {
                                        override fun run() {
                                            requireActivity().runOnUiThread {
                                                binding.textview11.visibility = View.VISIBLE
                                                binding.shimM.visibility = View.GONE
                                            }
                                        }
                                    }
                                    timer.schedule(shimtimer, 1000.toLong())
                                }
                            }
                        }
                    }
                    if (_childValue["Free"].toString() == "true") {
                        binding.linear10.visibility = View.VISIBLE
                    } else {
                        binding.linear10.visibility = View.GONE
                    }
                    if (_childValue["avatar"].toString() == "null") {
                        binding.textview16.visibility = View.VISIBLE
                        shimtimer = object : TimerTask() {
                            override fun run() {
                                requireActivity().runOnUiThread {
                                    binding.shimAvatar.visibility = View.GONE
                                    binding.circleimageview1.visibility = View.VISIBLE
                                }
                            }
                        }
                        timer.schedule(shimtimer, 1000.toLong())
                    } else {
                        binding.textview16.visibility = View.GONE
                        Glide.with(requireActivity())
                            .load(Uri.parse(_childValue["avatar"].toString()))
                            .into(binding.circleimageview1)
                        shimtimer = object : TimerTask() {
                            override fun run() {
                                requireActivity().runOnUiThread {
                                    binding.circleimageview1.visibility = View.VISIBLE
                                    binding.shimAvatar.visibility = View.GONE
                                }
                            }
                        }
                        timer.schedule(shimtimer, 1000.toLong())
                    }
                    if (_childValue.containsKey("join")) {
                        binding.textview18.text = _childValue["join"].toString()
                        shimtimer = object : TimerTask() {
                            override fun run() {
                                requireActivity().runOnUiThread {
                                    binding.shimJoin.visibility = View.GONE
                                    binding.textview18.visibility = View.VISIBLE
                                }
                            }
                        }
                        timer.schedule(shimtimer, 1000.toLong())
                    }
                    if (_childValue.containsKey("balance")) {
                        binding.textview20.text = DecimalFormat("#,##,##,###.##").format(
                            _childValue["balance"].toString().toDouble()
                        )
                        shimtimer = object : TimerTask() {
                            override fun run() {
                                requireActivity().runOnUiThread {
                                    binding.shimBal.visibility = View.GONE
                                    binding.textview20.visibility = View.VISIBLE
                                }
                            }
                        }
                        timer.schedule(shimtimer, 1000.toLong())
                    }
                    if (_childValue.containsKey("money")) {
                        binding.textview22.text = DecimalFormat("#,##,##,###.##").format(
                            _childValue["money"].toString().toDouble()
                        )
                        shimtimer = object : TimerTask() {
                            override fun run() {
                                requireActivity().runOnUiThread {
                                    binding.shimMon.visibility = View.GONE
                                    binding.textview22.visibility = View.VISIBLE
                                }
                            }
                        }
                        timer.schedule(shimtimer, 1000.toLong())
                    }
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onChildChanged(_param1: DataSnapshot, _param2: String?) {
                val _ind: GenericTypeIndicator<HashMap<String?, Any?>?> =
                    object : GenericTypeIndicator<HashMap<String?, Any?>?>() {}
                val _childKey = _param1.key
                val _childValue = _param1.getValue(_ind)!!
                if (_childKey == FirebaseAuth.getInstance().currentUser!!.uid) {
                    if (_childValue.containsKey("firstname")) {
                        fname = _childValue["firstname"].toString()
                    }
                    if (_childValue.containsKey("lastname")) {
                        lname = _childValue["lastname"].toString()
                    }
                    binding.textview2.text = "$fname $lname"
                    shimtimer = object : TimerTask() {
                        override fun run() {
                            requireActivity().runOnUiThread {
                                binding.shimName.visibility = View.GONE
                                binding.textview2.visibility = View.VISIBLE
                            }
                        }
                    }
                    timer.schedule(shimtimer, 1000.toLong())
                    if (_childValue.containsKey("email")) {
                        binding.textview3.text = _childValue["email"].toString()
                        shimtimer = object : TimerTask() {
                            override fun run() {
                                requireActivity().runOnUiThread {
                                    binding.shimEmail.visibility = View.GONE
                                    binding.textview3.visibility = View.VISIBLE
                                }
                            }
                        }
                        timer.schedule(shimtimer, 1000.toLong())
                    }
                    if (_childValue.containsKey("referralcode")) {
                        binding.textview9.text = _childValue["referralcode"].toString()
                        shimtimer = object : TimerTask() {
                            override fun run() {
                                requireActivity().runOnUiThread {
                                    binding.textview9.visibility = View.VISIBLE
                                    binding.shimRc.visibility = View.GONE
                                }
                            }
                        }
                        timer.schedule(shimtimer, 1000.toLong())
                        binding.textview9.setOnClickListener {
                            val clipboardManager =
                                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clipData = ClipData.newPlainText(
                                "clipboard",
                                _childValue["referralcode"].toString()
                            )
                            clipboardManager.setPrimaryClip(clipData)
                            Toast.makeText(
                                requireContext(),
                                "Refer code copied",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                    if (_childValue["Free"].toString() == "true") {
                        binding.textview11.text = "Free"
                        shimtimer = object : TimerTask() {
                            override fun run() {
                                requireActivity().runOnUiThread {
                                    binding.textview11.visibility = View.VISIBLE
                                    binding.shimM.visibility = View.GONE
                                }
                            }
                        }
                        timer.schedule(shimtimer, 1000.toLong())
                    } else {
                        if (_childValue["Silver"].toString() == "true") {
                            binding.textview11.text = "Silver"
                            shimtimer = object : TimerTask() {
                                override fun run() {
                                    requireActivity().runOnUiThread {
                                        binding.textview11.visibility = View.VISIBLE
                                        binding.shimM.visibility = View.GONE
                                    }
                                }
                            }
                            timer.schedule(shimtimer, 1000.toLong())
                        } else {
                            if (_childValue["Gold"].toString() == "true") {
                                binding.textview11.text = "Gold"
                                shimtimer = object : TimerTask() {
                                    override fun run() {
                                        requireActivity().runOnUiThread {
                                            binding.textview11.visibility = View.VISIBLE
                                            binding.shimM.visibility = View.GONE
                                        }
                                    }
                                }
                                timer.schedule(shimtimer, 1000.toLong())
                            } else {
                                if (_childValue["Diamond"].toString() == "true") {
                                    binding.textview11.text = "Diamond"
                                    shimtimer = object : TimerTask() {
                                        override fun run() {
                                            requireActivity().runOnUiThread {
                                                binding.textview11.visibility = View.VISIBLE
                                                binding.shimM.visibility = View.GONE
                                            }
                                        }
                                    }
                                    timer.schedule(shimtimer, 1000.toLong())
                                }
                            }
                        }
                    }
                    if (_childValue["Free"].toString() == "true") {
                        binding.linear10.visibility = View.VISIBLE
                    } else {
                        binding.linear10.visibility = View.GONE
                    }
                    if (_childValue["avatar"].toString() == "null") {
                        binding.textview16.visibility = View.VISIBLE
                        shimtimer = object : TimerTask() {
                            override fun run() {
                                requireActivity().runOnUiThread {
                                    binding.shimAvatar.visibility = View.GONE
                                    binding.circleimageview1.visibility = View.VISIBLE
                                }
                            }
                        }
                        timer.schedule(shimtimer, 1000.toLong())
                    } else {
                        binding.textview16.visibility = View.GONE
                        Glide.with(requireActivity())
                            .load(Uri.parse(_childValue["avatar"].toString()))
                            .into(binding.circleimageview1)
                        shimtimer = object : TimerTask() {
                            override fun run() {
                                requireActivity().runOnUiThread {
                                    binding.circleimageview1.visibility = View.VISIBLE
                                    binding.shimAvatar.visibility = View.GONE
                                }
                            }
                        }
                        timer.schedule(shimtimer, 1000.toLong())
                    }
                    if (_childValue.containsKey("join")) {
                        binding.textview18.text = _childValue["join"].toString()
                        shimtimer = object : TimerTask() {
                            override fun run() {
                                requireActivity().runOnUiThread {
                                    binding.shimJoin.visibility = View.GONE
                                    binding.textview18.visibility = View.VISIBLE
                                }
                            }
                        }
                        timer.schedule(shimtimer, 1000.toLong())
                    }
                    if (_childValue.containsKey("balance")) {
                        binding.textview20.text = _childValue["balance"].toString()
                        shimtimer = object : TimerTask() {
                            override fun run() {
                                requireActivity().runOnUiThread {
                                    binding.shimBal.visibility = View.GONE
                                    binding.textview20.visibility = View.VISIBLE
                                }
                            }
                        }
                        timer.schedule(shimtimer, 1000.toLong())
                    }
                    if (_childValue.containsKey("money")) {
                        binding.textview22.text = _childValue["money"].toString()
                        shimtimer = object : TimerTask() {
                            override fun run() {
                                requireActivity().runOnUiThread {
                                    binding.shimMon.visibility = View.GONE
                                    binding.textview22.visibility = View.VISIBLE
                                }
                            }
                        }
                        timer.schedule(shimtimer, 1000.toLong())
                    }
                }
            }

            override fun onChildMoved(_param1: DataSnapshot, _param2: String?) {}
            override fun onChildRemoved(_param1: DataSnapshot) {}
            override fun onCancelled(_param1: DatabaseError) {}
        })


    }

    private fun initializeLogic() {
        //adview1.loadAd(new AdRequest.Builder().addTestDevice("708001022B2AEFB4CA5DB3785F35FD14")
        //.build());
        ui()

        binding.shimAvatar.visibility = View.VISIBLE
        binding.linear10.visibility = View.GONE
        binding.circleimageview1.visibility = View.GONE
        binding.textview16.visibility = View.GONE
        binding.textview2.visibility = View.GONE
        binding.textview3.visibility = View.GONE
        binding.textview9.visibility = View.GONE
        binding.textview11.visibility = View.GONE
        binding.textview18.visibility = View.GONE
        binding.textview20.visibility = View.GONE
        binding.textview22.visibility = View.GONE

        binding.shimAvatar.startShimmer()
        binding.shimAvatar.setBackgroundColor(-0x171718)
        binding.shimName.startShimmer()
        binding.shimName.setBackgroundColor(-0x171718)
        binding.shimEmail.startShimmer()
        binding.shimEmail.setBackgroundColor(-0x171718)
        binding.shimRc.startShimmer()
        binding.shimRc.setBackgroundColor(-0x171718)
        binding.shimM.startShimmer()
        binding.shimM.setBackgroundColor(-0x171718)
        binding.shimJoin.startShimmer()
        binding.shimJoin.setBackgroundColor(-0x171718)
        binding.shimBal.startShimmer()
        binding.shimBal.setBackgroundColor(-0x171718)
        binding.shimMon.startShimmer()
        binding.shimMon.setBackgroundColor(-0x171718)
    }

    private fun ui() {
        binding.scrollview.isVerticalScrollBarEnabled = false
        shadow(5.0, 20.0, "#EEEEEE", binding.linear10)
        shadow(5.0, 0.0, "#FFFFFF", binding.linear8)
        shadow(5.0, 0.0, "#FFFFFF", binding.linear9)
        shadow(5.0, 0.0, "#FFFFFF", binding.linear14)
        shadow(5.0, 0.0, "#FFFFFF", binding.linear16)
        shadow(5.0, 0.0, "#FFFFFF", binding.linear19)
        gradientview(binding.linear10)
    }




    private fun shadow(_sadw: Double, _cru: Double, _wc: String?, _widgets: View) {
        val wd = GradientDrawable()
        wd.setColor(Color.parseColor(_wc))
        wd.cornerRadius = _cru.toInt().toFloat()
        _widgets.elevation = _sadw.toInt().toFloat()
        _widgets.background = wd
    }


    private fun gradientview(_view: View) {
        val gd = GradientDrawable(
            GradientDrawable.Orientation.RIGHT_LEFT, intArrayOf(
                -0x455c9,
                -0xb80c4
            )
        )
        gd.cornerRadius = 20f
        _view.elevation = 6f
        _view.setBackgroundDrawable(gd)
    }

    private fun showDialogTrue(
        dtitle: String,
        ddescription: String,
        dpositive: String,
        dnegative: String
    ) {
        val dialog = AlertDialog.Builder(requireContext()).create()
        val inflate: View = layoutInflater.inflate(R.layout.exit_dialog, null)
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.setView(inflate)
        val main = inflate.findViewById<View>(R.id.linear1) as LinearLayout
        val title = inflate.findViewById<View>(R.id.textview2) as TextView
        val description = inflate.findViewById<View>(R.id.textview1) as TextView
        val positive = inflate.findViewById<View>(R.id.textview3) as TextView
        val negative = inflate.findViewById<View>(R.id.textview4) as TextView
        val img = inflate.findViewById<View>(R.id.img) as ImageView
        cardView("#FF111A21", 10.0, 15.0, main)
        title.text = dtitle
        description.text = ddescription
        positive.text = dpositive
        negative.text = dnegative
        positive.setOnClickListener { dialog.dismiss() }
        negative.setOnClickListener {
            dialog.dismiss()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
        }
        img.setImageResource(R.drawable.shutdown)
        dialog.setCancelable(true)
        dialog.show()
    }

    private fun cardView(_color: String?, _radius: Double, _shadow: Double, _view: View) {
        val gd = GradientDrawable()
        gd.setColor(Color.parseColor(_color))
        gd.cornerRadius = _radius.toInt().toFloat()
        _view.background = gd
        try {
            _view.elevation = _shadow.toInt().toFloat()
        } catch (_: Exception) {
        }
    }
    /* private val startForProfileImageResult =
         registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
             val resultCode = result.resultCode
             val data = result.data
             if (resultCode == Activity.RESULT_OK) {
                 val fileUri = data?.data!!
                 uri = fileUri
                 binding.circleimageview1.setImageURI(fileUri)
                 FirebaseStorage.getInstance().reference.child("profile").child(FirebaseAuth.getInstance().uid.toString())
                     .putFile(uri).addOnSuccessListener {Toast.makeText(context, "Profile update Succesfully.", Toast.LENGTH_SHORT).show() }
                 FirebaseDatabase.getInstance().reference.child("users").child("imageuri").setValue(uri.toString()).addOnSuccessListener {  }
             } else if (resultCode == ImagePicker.RESULT_ERROR) {
                 Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
             } else {
                 Toast.makeText(context, "Task Cancelled", Toast.LENGTH_SHORT).show()
             }
         }


     */

}