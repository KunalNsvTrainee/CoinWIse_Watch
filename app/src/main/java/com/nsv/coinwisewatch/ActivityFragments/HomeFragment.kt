package com.nsv.coinwisewatch.ActivityFragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.nsv.coinwisewatch.R
import com.nsv.coinwisewatch.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private val firebase = FirebaseDatabase.getInstance()
    private val users: DatabaseReference = firebase.getReference("users")

    private var calendar = Calendar.getInstance()
    private var map = HashMap<String,Any>()
    private var now_time = 0.0
    private var balance = "0"
    private var last_Claimed = 0.0
    private var time_X = ""
    private var key = ""

    private var prog: ProgressDialog? = null

    private val history: DatabaseReference = firebase.getReference("history")
    private val claimed_time: DatabaseReference = firebase.getReference("claimed_time")
    private val control: DatabaseReference = firebase.getReference("control")


    private var limit = 0.0
    private var click = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        return binding.root
    }
    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.dailyBonus.setOnClickListener { v ->
            if (time_X == "") {
                map["balance"] = (balance.toDouble() + 10).toLong().toString()
                users.child(FirebaseAuth.getInstance().currentUser!!.uid).updateChildren(map)
                map.clear()
                key = history.push().key.toString()
                map = HashMap()
                map["subject"] = "daily bonus"
                map["date"] = SimpleDateFormat("dd-MM-yyyy").format(calendar.time)
                map["amount"] = "10"
                map["key"] = key
                map["uid"] = FirebaseAuth.getInstance().currentUser!!.uid
                history.child(key).updateChildren(map)
                map.clear()
                map = HashMap()
                map["timer"] = calendar.timeInMillis.toString()
                claimed_time.child(FirebaseAuth.getInstance().currentUser!!.uid).updateChildren(map)
                map.clear()
                congratulation("+10 points")
            } else {
                    last_Claimed = time_X.toDouble()
                    now_time = calendar.timeInMillis - last_Claimed
                    if (now_time / (60 * 60000) > 24 || now_time / (60 * 60000) == 24.0) {
                        map["balance"] = (balance.toDouble() + 10).toLong().toString()
                        users.child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .updateChildren(map)
                        map.clear()
                        key = history.push().key.toString()
                        map = java.util.HashMap<String, Any>()
                        map["subject"] = "daily bonus"
                        map["date"] = SimpleDateFormat("dd-MM-yyyy").format(calendar.getTime())
                        map["amount"] = "10"
                        map["key"] = key
                        map["uid"] = FirebaseAuth.getInstance().currentUser!!.uid
                        history.child(key).updateChildren(map)
                        map.clear()
                        map = HashMap()
                        map["timer"] = calendar.timeInMillis.toString()
                        claimed_time.child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .updateChildren(map)
                        map.clear()
                        congratulation("+10 points")
                    } else {
                        Toast.makeText(context, "Wait 24 hours", Toast.LENGTH_SHORT)
                            .show()
                    }
            }
        }
       binding.hist.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_historyFragment) }
       binding.withdraw.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_withdrawFragment) }
       binding.quickQuiz.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_quizFragment) }
       binding.support.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_supportFragment)}

        users.addChildEventListener(object : ChildEventListener {
            @SuppressLint("SetTextI18n")
            override fun onChildAdded(param1: DataSnapshot, param2: String?) {
                val  ind: GenericTypeIndicator<java.util.HashMap<String?, Any?>?> =
                    object : GenericTypeIndicator<java.util.HashMap<String?, Any?>?>() {}
                val  childKey =  param1.key
                val  childValue =  param1.getValue( ind)!!
                if ( childKey == FirebaseAuth.getInstance().currentUser!!.uid) {
                    loadingdialog(false, "")
                    if ( childValue.containsKey("balance")) {
                        balance =  childValue["balance"].toString()
                    }
                    if ( childValue.containsKey("limit")) {
                        limit =  childValue["limit"].toString().toDouble()
                    }
                    if ( childValue.containsKey("click")) {
                        click =  childValue["click"].toString().toDouble()
                    }
                }
            }
            @SuppressLint("SetTextI18n")
            override fun onChildChanged(param1: DataSnapshot, param2: String?) {
                val  ind: GenericTypeIndicator<java.util.HashMap<String?, Any?>?> =
                    object : GenericTypeIndicator<java.util.HashMap<String?, Any?>?>() {}
                val  childKey =  param1.key
                val  childValue =  param1.getValue( ind)!!
                if ( childKey == FirebaseAuth.getInstance().currentUser!!.uid) {
                    loadingdialog(false, "")
                    if ( childValue.containsKey("balance")) {
                        balance =  childValue["balance"].toString()
                    }
                    if ( childValue.containsKey("limit")) {
                        limit =  childValue["limit"].toString().toDouble()
                    }
                    if ( childValue.containsKey("click")) {
                        click =  childValue["click"].toString().toDouble()
                    }
                }
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }
            override fun onChildMoved(param1: DataSnapshot, param2: String?) {}
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        claimed_time.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(param1: DataSnapshot, param2: String?) {
                val  ind: GenericTypeIndicator<java.util.HashMap<String?, Any?>?> =
                    object : GenericTypeIndicator<java.util.HashMap<String?, Any?>?>() {}
                val  childKey =  param1.key
                val  childValue =  param1.getValue( ind)!!
                if ( childKey == FirebaseAuth.getInstance().currentUser!!.uid) {
                    if ( childValue.containsKey("timer")) {
                        time_X =  childValue["timer"].toString()
                        last_Claimed =  childValue["timer"].toString().toDouble()
                        val clnd = Calendar.getInstance()
                        now_time = clnd.timeInMillis - last_Claimed
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
                val  ind: GenericTypeIndicator<java.util.HashMap<String?, Any?>?> =
                    object : GenericTypeIndicator<java.util.HashMap<String?, Any?>?>() {}
                val  childKey =  param1.key
                val  childValue =  param1.getValue( ind)!!
                if ( childKey == FirebaseAuth.getInstance().currentUser!!.uid) {
                    if ( childValue.containsKey("timer")) {
                        time_X =  childValue["timer"].toString()
                        last_Claimed =  childValue["timer"].toString().toDouble()
                        val clnd = Calendar.getInstance()
                        now_time = clnd.timeInMillis - last_Claimed
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
            override fun onChildMoved(param1: DataSnapshot, param2: String?) {}
            override fun onChildRemoved( param1: DataSnapshot) {
                val  ind: GenericTypeIndicator<java.util.HashMap<String?, Any?>?> =
                    object : GenericTypeIndicator<java.util.HashMap<String?, Any?>?>() {}

            }
            override fun onCancelled( param1: DatabaseError) {
            }
        })

        /*	_sponsor_child_listener = new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot _param1, String _param2) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				if (_childValue.get("type").toString().equals("slider1")) {
					if (_childValue.containsKey("image")) {
						slider1 = _childValue.get("image").toString();
					}
					imageview3.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View _view) {
							if (_childValue.containsKey("link1")) {
								go.putExtra("title", "");
								go.putExtra("link", _childValue.get("link1").toString());
								go.setClass(getApplicationContext(), WebActivity.class);
								startActivity(go);
							}
						}
					});
				}
				if (_childValue.get("type").toString().equals("slider2")) {
					if (_childValue.containsKey("image")) {
						slider2 = _childValue.get("image").toString();
					}
					imageview3.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View _view) {
							if (_childValue.containsKey("link2")) {
								go.putExtra("title", "");
								go.putExtra("link", _childValue.get("link2").toString());
								go.setClass(getApplicationContext(), WebActivity.class);
								startActivity(go);
							}
						}
					});
				}
				if (_childValue.get("type").toString().equals("slider3")) {
					if (_childValue.containsKey("image")) {
						slider3 = _childValue.get("image").toString();
					}
					if (_childValue.containsKey("link3")) {
						imageview3.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View _view) {
								go.putExtra("title", "");
								go.putExtra("link", _childValue.get("link3").toString());
								go.setClass(getApplicationContext(), WebActivity.class);
								startActivity(go);
							}
						});
					}
				}
			}

			@Override
			public void onChildChanged(DataSnapshot _param1, String _param2) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				if (_childValue.get("type").toString().equals("slider1")) {
					if (_childValue.containsKey("image")) {
						slider3 = _childValue.get("image").toString();
					}
					imageview3.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View _view) {
							if (_childValue.containsKey("link1")) {
								go.putExtra("title", "");
								go.putExtra("link", _childValue.get("link1").toString());
								go.setClass(getApplicationContext(), WebActivity.class);
								startActivity(go);
							}
						}
					});
				}
				if (_childValue.get("type").toString().equals("slider2")) {
					if (_childValue.containsKey("image")) {
						slider3 = _childValue.get("image").toString();
					}
					imageview3.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View _view) {
							if (_childValue.containsKey("link2")) {
								go.putExtra("title", "");
								go.putExtra("link", _childValue.get("link2").toString());
								go.setClass(getApplicationContext(), WebActivity.class);
								startActivity(go);
							}
						}
					});
				}
				if (_childValue.get("type").toString().equals("slider3")) {
					if (_childValue.containsKey("image")) {
						slider3 = _childValue.get("image").toString();
					}
					if (_childValue.containsKey("link3")) {
						imageview3.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View _view) {
								go.putExtra("title", "");
								go.putExtra("link", _childValue.get("link3").toString());
								go.setClass(getApplicationContext(), WebActivity.class);
								startActivity(go);
							}
						});
					}
				}
			}

			@Override
			public void onChildMoved(DataSnapshot _param1, String _param2) {

			}

			@Override
			public void onChildRemoved(DataSnapshot _param1) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);

			}

			@Override
			public void onCancelled(DatabaseError _param1) {
				final int _errorCode = _param1.getCode();
				final String _errorMessage = _param1.getMessage();

			}
		};*/
        //	sponsor.addChildEventListener(_sponsor_child_listener);
        control.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(_param1: DataSnapshot, _param2: String?) {
                val _ind: GenericTypeIndicator<java.util.HashMap<String?, Any?>?> =
                    object : GenericTypeIndicator<java.util.HashMap<String?, Any?>?>() {}
                val _childValue = _param1.getValue(_ind)!!!!
                if (_childValue["control"].toString() == "disable") {
                    val dialog2 = AlertDialog.Builder(context).create()
                    val inflate: View = layoutInflater.inflate(R.layout.custom, null)
                    dialog2.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog2.setView(inflate)
                    val t1 = inflate.findViewById<View>(R.id.t1) as TextView
                    val t2 = inflate.findViewById<View>(R.id.t2) as TextView
                    val b2 = inflate.findViewById<View>(R.id.b2) as LinearLayout
                    val i1 = inflate.findViewById<View>(R.id.i1) as ImageView
                    val bg = inflate.findViewById<View>(R.id.bg) as LinearLayout
                    val t3 = inflate.findViewById<View>(R.id.t3) as TextView
                    t2.setTextColor(-0xc0ae4b)
                    i1.setImageResource(R.drawable.img)
                    t1.text = "Temporary Closed"
                    t2.text = "But, Will Be Back Soon"
                    t3.text = "Exit"
                    b2.setOnClickListener { }
                    dialog2.setCancelable(false)
                    dialog2.show()
                }
            }

            override fun onChildChanged(_param1: DataSnapshot, _param2: String?) {
                val _ind: GenericTypeIndicator<java.util.HashMap<String?, Any?>?> =
                    object : GenericTypeIndicator<java.util.HashMap<String?, Any?>?>() {}
                val _childValue = _param1.getValue(_ind)!!
                if (_childValue["control"].toString() == "disable") {
                    val dialog2 = AlertDialog.Builder(context).create()
                    val inflate: View = layoutInflater.inflate(R.layout.custom, null)
                    dialog2.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog2.setView(inflate)
                    val t1 = inflate.findViewById<View>(R.id.t1) as TextView
                    val t2 = inflate.findViewById<View>(R.id.t2) as TextView
                    val b2 = inflate.findViewById<View>(R.id.b2) as LinearLayout
                    val i1 = inflate.findViewById<View>(R.id.i1) as ImageView
                    val bg = inflate.findViewById<View>(R.id.bg) as LinearLayout
                    val t3 = inflate.findViewById<View>(R.id.t3) as TextView
                    t2.setTextColor(-0xc0ae4b)
                    i1.setImageResource(R.drawable.img)
                    t1.text = "Temporary Closed"
                    t2.text = "But, Will Be Back Soon"
                    t3.text = "Exit"
                    b2.setOnClickListener { }
                    dialog2.setCancelable(false)
                    dialog2.show()
                }
            }

            override fun onChildMoved(_param1: DataSnapshot, _param2: String?) {}
            override fun onChildRemoved(_param1: DataSnapshot) {

            }

            override fun onCancelled(_param1: DatabaseError) {

            }
        })

    }
    private fun congratulation(point: String) {
        val dialog1 = AlertDialog.Builder(context).create()
        val inflate: View = layoutInflater.inflate(R.layout.congolayout, null)
        dialog1.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog1.setView(inflate)
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


    fun loadingdialog(ifShow: Boolean,title: String?) {
        if (ifShow) {
            if (prog == null) {
                prog = ProgressDialog(context)
                prog!!.max = 100
                prog!!.isIndeterminate = true
                prog!!.setCancelable(false)
                prog!!.setCanceledOnTouchOutside(false)
            }
            prog!!.setMessage(title)
            prog!!.show()
        } else {
            if (prog != null) {
                prog!!.dismiss()
            }
        }
    }


}